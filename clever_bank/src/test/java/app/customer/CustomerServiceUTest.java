package app.customer;

import app.cards.model.Cards;
import app.cards.service.CardService;
import app.customer.model.Country;
import app.customer.model.Customer;
import app.customer.model.Gender;
import app.customer.model.UserRole;
import app.customer.repository.CustomerRepository;
import app.customer.service.CustomerService;
import app.exception.CustomerAlreadyExistException;
import app.exception.DomainException;
import app.loans.service.LoansServiceImpl.LoansServiceImpl;
import app.notification.service.NotificationService;
import app.pocket.model.Pocket;
import app.pocket.service.PocketService;
import app.security.AuthenticationMetadataDetails;
import app.subscription.model.Subscription;
import app.subscription.service.SubscriptionService;
import app.web.dto.CustomerEditRequest;
import app.web.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceUTest {

    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private SubscriptionService subscriptionService;
    @Mock
    private PocketService pocketService;
    @Mock
    private CardService cardsService;
    @Mock
    private NotificationService notificationService;
    @Mock
    private LoansServiceImpl loansService;


    @InjectMocks
    private CustomerService customerService;


    @ParameterizedTest
    @MethodSource("userRoleArguments")
    void whenChangeRoleCustomer_thenCorrectRoleIsAssigned(UserRole currentRole, UserRole expectedRole) {

        // given
        UUID customerId = UUID.randomUUID ();

        Customer customer = Customer.builder ()
                .id (customerId)
                .role (currentRole)
                .build ();

        when (customerRepository.findById (customerId)).thenReturn (Optional.of (customer));

        //when
        customerService.switchCustomerRole (customerId);

        //then
        assertEquals (expectedRole, customer.getRole ());
    }

    private static Stream <Arguments> userRoleArguments() {
        return Stream.of (
                Arguments.of (UserRole.USER, UserRole.ADMIN),
                Arguments.of (UserRole.ADMIN, UserRole.USER)
        );
    }



    @Test
    void givenNoExistCustomerId_whenInvokeGetById_thenReturnException(){

        // given
        UUID customerId = UUID.randomUUID ();

        when (customerRepository.findById (customerId)).thenReturn (Optional.empty ());

        // when/then
        DomainException exception = assertThrows (DomainException.class, () -> customerService.getById (customerId));

        assertTrue (exception.getMassage ().contains ("Customer with id %s not found".formatted (customerId)));
        assertEquals (HttpStatus.BAD_REQUEST, exception.getStatus());

        verify (customerRepository, times (1)).findById (customerId);
    }




    @Test
    void givenCustomerWithStatusActive_whenSwitchStatus_thenCustomerStatusIsInactive() {

        // given
        UUID customerId = UUID.randomUUID ();

        Customer customer = Customer.builder ()
                .id (customerId)
                .isActive (true)
                .build ();

        when (customerRepository.findById (customerId)).thenReturn (Optional.of (customer));

        //when
        customerService.switchCustomerStatus (customerId);

        //then
        assertFalse (customer.isActive ());
        verify (customerRepository, times (1)).save (customer);
    }


    @Test
    void givenCustomerWithStatusInactive_whenSwitchStatus_thenCustomerStatusIsActive() {

        // given
        UUID customerId = UUID.randomUUID ();

        Customer customer = Customer.builder ()
                .id (customerId)
                .isActive (false)
                .build ();

        when (customerRepository.findById (customerId)).thenReturn (Optional.of (customer));

        //when
        customerService.switchCustomerStatus (customerId);

        //then
        assertTrue (customer.isActive ());
        verify (customerRepository, times (1)).save (customer);
    }




    @Test
    void givenCustomersExist_WhenGetAllCustomers_thenReturnThemAll() {

        // given
        List<Customer> customers = List.of (
                Customer.builder().build(),
                Customer.builder().build()
        );

        when (customerRepository.findAll()).thenReturn (customers);

        // when
        List<Customer> result = customerService.getALLCustomers ();

        // then
        assertEquals (customers, result);

    }


    @Test
    void givenExistingCustomer_whenRegister_thenThrowException() {

        // Given
        RegisterRequest register = RegisterRequest.builder()
                .username("Ven123")
                .password("Venelin7")
                .phoneNumber("0895121212")
                .country(Country.BULGARIA)
                .gender (Gender.MALE)
                .build();

        when (customerRepository.findByUsername (any ())).thenReturn (Optional.of (new Customer ()));

        // When/Then
        assertThrows (CustomerAlreadyExistException.class, ()-> customerService.register (register));
        verify (customerRepository, never ()).save (any ());
        verify (subscriptionService, never ()).createDefaultSubscription (any ());
        verify (pocketService, never ()).createWallet (any ());
        verify (cardsService, never ()).createDefaultCard (any ());
        verify (notificationService, never ()).saveNotificationPreference (any (UUID.class), anyBoolean (), anyString ());
    }



    @Test
    void givenRegisterCustomer_whenRegister_thenSaveCustomer_HappyPath() {

        // Given
        RegisterRequest register = RegisterRequest.builder()
                .username("Ven123")
                .password("Venelin7")
                .phoneNumber("0895121212")
                .country(Country.BULGARIA)
                .gender (Gender.MALE)
                .build();

        Customer customer = Customer.builder ()
                .id (UUID.randomUUID ())
                .username (register.getUsername ())
                .password (register.getPassword ())
                .phoneNumber (register.getPhoneNumber ())
                .country (register.getCountry ())
                .gender (register.getGender ())
                .build ();

        when (customerRepository.findByUsername (register.getUsername ())).thenReturn (Optional.empty ());
        when (customerRepository.save (any ())).thenReturn (customer);
        when (subscriptionService.createDefaultSubscription (customer)).thenReturn (new Subscription ());
        when (pocketService.createWallet (customer)).thenReturn (new Pocket ());
        when (cardsService.createDefaultCard (customer)).thenReturn (new Cards ());

        //when

        Customer registerCustomer = customerService.register (register);

        //then
        assertThat(registerCustomer.getSubscriptions ()).hasSize (1);
        assertThat(registerCustomer.getWallets ()).hasSize (1);
        assertThat(registerCustomer.getCards ()).hasSize (1);
        verify (notificationService, times (1)).saveNotificationPreference (customer.getId (), false, null);
    }




    @Test
    void givenMissingCustomerForDatabase_whenLoadCustomerByUsername_thenThrowException() {

        // given
        String username = "Ven123";

        when (customerRepository.findByUsername (username)).thenReturn (Optional.empty ());

        // when/then
        assertThrows (DomainException.class, () -> customerService.loadUserByUsername (username));
    }




    @Test
    void givenCustomerForDatabase_whenLoadCustomerByUsername_thenReturnCustomer() {

        // given
        String username = "Ven123";

        Customer customer = Customer.builder()
                .id (UUID.randomUUID ())
                .username (username)
                .password ("Venelin7")
                .isActive (true)
                .role (UserRole.USER)
                .accountExpireAt (LocalDateTime.now ())
                .credentialsExpireAt (LocalDateTime.now ())
                .build ();

        when (customerRepository.findByUsername (username)).thenReturn (Optional.of (customer));
        
        //when
        UserDetails authenticationCustomer = customerService.loadUserByUsername (username);

        //then
        assertInstanceOf (AuthenticationMetadataDetails.class, authenticationCustomer);
        AuthenticationMetadataDetails result = (AuthenticationMetadataDetails) authenticationCustomer;

        assertEquals (customer.getId (), result.getCustomerId ());
        assertEquals (customer.getUsername (), result.getUsername ());
        assertEquals (customer.getPassword (), result.getPassword ());
        assertEquals (customer.getRole (), result.getRole ());
        assertEquals (customer.isActive (), result.isActive ());
        assertEquals (customer.getAccountExpireAt (), result.getAccountExpireAt ());
        assertEquals (customer.getCredentialsExpireAt (), result.getIsCredentialsExpired ());
    }




     @Test
    void givenExistCustomer_whenEditProfileCustomerDetails_thenSaveCustomerAndChangeNotificationPreference() {

        // given
         UUID customerId = UUID.randomUUID ();
         CustomerEditRequest customerEditRequest = CustomerEditRequest.builder()
                 .firstName ("Venelin")
                 .lastName ("Davidov")
                 .phoneNumber ("0895121212")
                 .email ("venelin.dav@gmail.com")
                 .profilePicture ("https://example.com/profile.jpg")
                 .address ("Sofia")
                 .build ();

         Customer customer = Customer.builder().build ();

         when (customerRepository.findById (customerId)).thenReturn (Optional.of (customer));

         // when
         customerService.editCustomerDetails (customerId, customerEditRequest);

         //then
         assertEquals ("Venelin", customer.getFirstName ());
         assertEquals ("Davidov", customer.getLastName ());
         assertEquals ("0895121212", customer.getPhoneNumber ());
         assertEquals ("venelin.dav@gmail.com", customer.getEmail ());
         assertEquals ("https://example.com/profile.jpg", customer.getProfilePicture ());
         assertEquals ("Sofia", customer.getAddress ());
    }





    @Test
    void givenExistCustomer_whenEditProfileCustomerDetailsWithEmptyEmail_thenChangeNotificationPreferenceAndSaveBase(){

        UUID customerId = UUID.randomUUID ();
        CustomerEditRequest customerEditRequest = CustomerEditRequest.builder()
                .firstName ("Venelin")
                .lastName ("Davidov")
                .phoneNumber ("0895121212")
                .email ("")
                .profilePicture ("https://example.com/profile.jpg")
                .address ("Sofia")
                .build ();

        Customer customer = Customer.builder().build ();

        when (customerRepository.findById (customerId)).thenReturn (Optional.of (customer));

        //when
        customerService.editCustomerDetails (customerId, customerEditRequest);

        //then
        assertEquals ("Venelin", customer.getFirstName ());
        assertEquals ("Davidov", customer.getLastName ());
        assertEquals ("0895121212", customer.getPhoneNumber ());
        assertEquals ("", customer.getEmail ());
        assertEquals ("https://example.com/profile.jpg", customer.getProfilePicture ());
        assertEquals ("Sofia", customer.getAddress ());

        verify (customerRepository, times (1)).save (customer);
        verify (notificationService, times (1)).saveNotificationPreference (customer.getId (), false, null);
    }
}

package app.customer.service;

import app.cards.model.Cards;
import app.cards.service.CardService;
import app.customer.model.Customer;
import app.customer.model.Gender;
import app.customer.model.UserRole;
import app.customer.repository.CustomerRepository;
import app.exception.CustomerAlreadyExistException;
import app.exception.DomainException;
import app.notification.service.NotificationService;
import app.security.AuthenticationMetadataDetails;
import app.subscription.model.Subscription;
import app.subscription.service.SubscriptionService;
import app.pocket.model.Pocket;
import app.pocket.service.PocketService;
import app.transaction.repository.TransactionRepository;
import app.web.dto.CustomerEditRequest;
import app.web.dto.RegisterRequest;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class CustomerService implements UserDetailsService {


    private final static String CREATE_CUSTOMER_MESSAGE = "Successfully created customer %s with id %s";

    private final CustomerRepository customerRepository;
    private final PocketService pocketService;
    private final SubscriptionService subscriptionService;
    private final CardService cardService;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;



    @Autowired
    public CustomerService(CustomerRepository customerRepository,
                           PocketService pocketService,
                           SubscriptionService subscriptionService,
                           PasswordEncoder passwordEncoder,
                           CardService cardService,
                           NotificationService notificationService) {
        this.customerRepository = customerRepository;
        this.pocketService = pocketService;
        this.subscriptionService = subscriptionService;
        this.passwordEncoder = passwordEncoder;
        this.cardService = cardService;
        this.notificationService = notificationService;

    }


    //Method register
    @Transactional
    @CacheEvict(value = "customer", allEntries = true)
    public Customer register(RegisterRequest registerRequest) {

        Optional <Customer> optionalCustomer = customerRepository.findByUsername (registerRequest.getUsername ());

        if (optionalCustomer.isPresent ()) {
            throw new CustomerAlreadyExistException ("Customer with username [%s] already exist!".formatted (registerRequest.getUsername ()));
        }

        Customer customer = customerRepository.save (createNewCustomerAccount (registerRequest));

        Subscription defaultSubscription = subscriptionService.createDefaultSubscription (customer);
        customer.setSubscriptions (List.of (defaultSubscription));

        Pocket pocket = pocketService.createWallet (customer);
        customer.setWallets (List.of (pocket));

        Cards cards = cardService.createDefaultCard (customer);
        customer.setCards (List.of (cards));

        notificationService.saveNotificationPreference (customer.getId (), false, null);

        log.info (CREATE_CUSTOMER_MESSAGE.formatted (customer.getUsername (), customer.getId ()));

        return customer;
    }


    private Customer createNewCustomerAccount(RegisterRequest registerDto) {

        return Customer.builder ()
                .username (registerDto.getUsername ())
                .password (passwordEncoder.encode (registerDto.getPassword ()))
                .phoneNumber (registerDto.getPhoneNumber ())
                .country (registerDto.getCountry ())
                .isActive (true)
                .role (UserRole.USER)
                .gender (Gender.MALE)
                .createdOn (LocalDateTime.now ())
                .updatedOn (LocalDateTime.now ())
                .accountExpireAt (LocalDateTime.now ().plusYears (1))
                .credentialsExpireAt (LocalDateTime.now ().plusYears (1))
                .build ();
    }




    // Retrieve customer by id
    public Customer getById(UUID uuid) {

        return customerRepository.findById (uuid)
                .orElseThrow (() -> new DomainException ("Customer with id %s not found"
                        .formatted (uuid), HttpStatus.BAD_REQUEST));
    }




    // Retrieve all customers
    @Cacheable("customer")
    public List <Customer> getALLCustomers() {
        return customerRepository.findAll ();
    }




    //  everytime after login from user, spring security will call this method
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Customer customer = customerRepository.findByUsername (username)
                .orElseThrow (() -> new DomainException ("User with username=[%s] does not exist."
                        .formatted (username), HttpStatus.BAD_REQUEST));

        return new AuthenticationMetadataDetails (
                customer.getId (),
                customer.getUsername (),
                customer.getPassword (),
                customer.getRole (),
                customer.isActive (),
                customer.getAccountExpireAt (),
                customer.getCredentialsExpireAt ()
        );
    }





    // edit customer details by all information
    @CacheEvict(value = "customer", allEntries = true)
    public void editCustomerDetails(UUID id, CustomerEditRequest customerEditRequest) {

        Customer customer = getById (id);

        customer.setFirstName (customerEditRequest.getFirstName ());
        customer.setLastName (customerEditRequest.getLastName ());
        customer.setProfilePicture (customerEditRequest.getProfilePicture ());
        customer.setPhoneNumber (customerEditRequest.getPhoneNumber ());
        customer.setEmail (customerEditRequest.getEmail ());
        customer.setAddress (customerEditRequest.getAddress ());

        if (!customerEditRequest.getEmail ().isEmpty ()){
            notificationService.saveNotificationPreference (customer.getId (), true, customerEditRequest.getEmail ());
        } else {
            notificationService.saveNotificationPreference (customer.getId (), false, null);
        }

        customerRepository.save (customer);

    }

    @CacheEvict(value = "customer", allEntries = true)
    public void switchCustomerStatus(UUID customerId) {

        Customer customer = getById (customerId);

        if (customer.isActive ()) {
            customer.setActive (false);
        } else {
            customer.setActive (true);
        }
        customerRepository.save (customer);
    }



    @CacheEvict(value = "customer", allEntries = true)
    public void switchCustomerRole(UUID customerId) {

        Customer customer = getById (customerId);

        if (customer.getRole () == UserRole.USER){
            customer.setRole (UserRole.ADMIN);
        } else {
            customer.setRole (UserRole.USER);
        }

        customerRepository.save (customer);
    }

}



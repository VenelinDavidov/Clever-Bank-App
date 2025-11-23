package app.pocket;

import app.customer.model.Country;
import app.customer.model.Customer;
import app.customer.model.Gender;
import app.customer.model.UserRole;
import app.exception.DomainException;
import app.pocket.model.Pocket;
import app.pocket.model.PocketStatus;
import app.pocket.model.PocketType;
import app.pocket.repository.PocketRepository;
import app.pocket.service.PocketService;
import app.transaction.model.Transactions;
import app.transaction.repository.TransactionRepository;
import app.transaction.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PocketServiceUTest {


    @Mock
    private  PocketRepository pocketRepository;
    @Mock
    private  TransactionService transactionService;
    @Mock
    private  TransactionRepository transactionRepository;

    @InjectMocks
    private PocketService pocketService;


   @Test
    void givenRequestToGetLastSevenTransaction_whenInvokeGetLastSevenTransactions_thenReturnLastSevenTransactions() {

       // given
       Customer customer = Customer.builder()
               .id (UUID.randomUUID ())
               .firstName ("Vitoriya")
               .lastName ("Ivanova")
               .email ("vitoriya@abv.bg")
               .username ("Vitoriya123")
               .password ("Vitoriya7")
               .role (UserRole.USER)
               .country (Country.BULGARIA)
               .gender (Gender.FEMALE)
               .address ("Sofia")
               .profilePicture ("pic.png")
               .phoneNumber ("0895121212")
               .createdOn (LocalDateTime.now ())
               .updatedOn (LocalDateTime.now ())
               .accountExpireAt (LocalDateTime.now ())
               .credentialsExpireAt (LocalDateTime.now ())
               .build();

       Pocket pocket = Pocket.builder()
               .id (UUID.randomUUID ())
               .customer (customer)
               .status (PocketStatus.ACTIVE)
               .type (PocketType.BUSINESS)
               .balance (new BigDecimal ("40.00"))
               .currency (Currency.getInstance ("USD"))
               .createdOn (LocalDateTime.now ())
               .updatedOn (LocalDateTime.now ())
               .build();

       List<Pocket> pockets = List.of(pocket);
       List <Transactions> expectedTransaction = List.of (new Transactions (), new Transactions (), new Transactions ());

       //when
       when( transactionService.getLastSevenTransactionsByPocketId (pocket)).thenReturn (expectedTransaction);

       Map <UUID, List <Transactions>> result = pocketService.getLastSevenTransactions (pockets);

       //then
       assertNotNull (result);
       assertEquals (1, result.size ());
       assertEquals (expectedTransaction, result.get (pocket.getId ()));
    }



    @Test
    void givenCustomerRequestToSwitchStatusActivePocket_whenInvokeSwitchStatus_thenReturnSwitchedStatusInactive() {

       //given
        UUID pocketId = UUID.randomUUID ();
        UUID customerId = UUID.randomUUID ();

        Customer customer = Customer.builder()
                .id (customerId)
                .firstName ("Vitoriya")
                .lastName ("Ivanova")
                .email ("vitoriya@abv.bg")
                .username ("Vitoriya123")
                .password ("Vitoriya7")
                .role (UserRole.USER)
                .country (Country.BULGARIA)
                .gender (Gender.FEMALE)
                .address ("Sofia")
                .profilePicture ("pic.png")
                .phoneNumber ("0895121212")
                .createdOn (LocalDateTime.now ())
                .updatedOn (LocalDateTime.now ())
                .accountExpireAt (LocalDateTime.now ())
                .credentialsExpireAt (LocalDateTime.now ())
                .build();

        Pocket pocket = Pocket.builder()
                .id (pocketId)
                .customer (customer)
                .status (PocketStatus.ACTIVE)
                .type (PocketType.BUSINESS)
                .balance (new BigDecimal ("40.00"))
                .currency (Currency.getInstance ("USD"))
                .createdOn (LocalDateTime.now ())
                .updatedOn (LocalDateTime.now ())
                .build();

       // mock the repository response
        when(pocketRepository.findByIdAndCustomerId(pocketId, customerId))
                .thenReturn(Optional.of(pocket));

        //when
        pocketService.switchStatusWallet(pocketId, customerId);

        //then
        assertEquals (PocketStatus.INACTIVE, pocket.getStatus());
        verify (pocketRepository, times (1)).save (pocket);
    }

    @Test
    void givenCustomerRequestToSwitchStatusInActivePocket_whenInvokeSwitchStatus_thenReturnSwitchedStatusActive() {

        //given
        UUID pocketId = UUID.randomUUID ();
        UUID customerId = UUID.randomUUID ();

        Customer customer = Customer.builder()
                .id (customerId)
                .firstName ("Vitoriya")
                .lastName ("Ivanova")
                .email ("vitoriya@abv.bg")
                .username ("Vitoriya123")
                .password ("Vitoriya7")
                .role (UserRole.USER)
                .country (Country.BULGARIA)
                .gender (Gender.FEMALE)
                .address ("Sofia")
                .profilePicture ("pic.png")
                .phoneNumber ("0895121212")
                .createdOn (LocalDateTime.now ())
                .updatedOn (LocalDateTime.now ())
                .accountExpireAt (LocalDateTime.now ())
                .credentialsExpireAt (LocalDateTime.now ())
                .build();

        Pocket pocket = Pocket.builder()
                .id (pocketId)
                .customer (customer)
                .status (PocketStatus.INACTIVE)
                .type (PocketType.BUSINESS)
                .balance (new BigDecimal ("40.00"))
                .currency (Currency.getInstance ("USD"))
                .createdOn (LocalDateTime.now ())
                .updatedOn (LocalDateTime.now ())
                .build();

        // mock the repository response
        when(pocketRepository.findByIdAndCustomerId(pocketId, customerId))
                .thenReturn(Optional.of(pocket));

        //when
        pocketService.switchStatusWallet(pocketId, customerId);

        //then
        assertEquals (PocketStatus.ACTIVE, pocket.getStatus());
        verify (pocketRepository, times (1)).save (pocket);
    }


    @Test
    void givenNotValidPocketIdAndCustomerId_whenInvokeSwitchStatus_thenReturnException() {

       //given
       UUID pocketId = UUID.randomUUID ();
       UUID customerId = UUID.randomUUID ();

        when(pocketRepository.findByIdAndCustomerId(pocketId, customerId))
                .thenReturn(Optional.empty());

       //when + then
       assertThrows (DomainException.class, () -> pocketService.switchStatusWallet(pocketId, customerId));

       verify (pocketRepository, never ()).save(any ());
       verify (pocketRepository, times (1)).findByIdAndCustomerId(pocketId, customerId);
    }
}

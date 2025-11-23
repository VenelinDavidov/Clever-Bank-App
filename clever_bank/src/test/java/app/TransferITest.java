package app;

import app.customer.model.Country;
import app.customer.model.Customer;
import app.customer.model.Gender;
import app.customer.model.UserRole;
import app.customer.repository.CustomerRepository;
import app.customer.service.CustomerService;
import app.pocket.model.Pocket;
import app.pocket.model.PocketStatus;
import app.pocket.repository.PocketRepository;
import app.pocket.service.PocketService;
import app.subscription.repository.SubscriptionRepository;
import app.subscription.service.SubscriptionService;
import app.transaction.model.TransactionStatus;
import app.transaction.model.Transactions;
import app.transaction.repository.TransactionRepository;
import app.transaction.service.TransactionService;
import app.web.dto.DepositRequest;
import app.web.dto.RegisterRequest;
import app.web.dto.TransferResultRequest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
@SpringBootTest
public class TransferITest {


    @Autowired
    private CustomerService customerService;

    @Autowired
    private PocketRepository pocketRepository;

    @Autowired
    private PocketService pocketService;

    @Autowired
    private TransactionService transactionService;
    @Autowired
    private CustomerRepository customerRepository;


    @Test
    void givenRequestToTransferEndpoint_whenTransfer_thenSaveTransaction() throws Exception {


        Customer sender = Customer.builder()
                .username("sender" + System.currentTimeMillis())
                .password("Password1")
                .phoneNumber("0897" + (System.nanoTime() % 100000))
                .country(Country.BULGARIA)
                .gender(Gender.MALE)
                .role(UserRole.USER)
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();
        customerRepository.save(sender);

        Customer receiver = Customer.builder()
                .username("receiver" + System.currentTimeMillis())
                .password("Password2")
                .phoneNumber("0898" + (System.nanoTime() % 100000))
                .country(Country.BULGARIA)
                .role(UserRole.USER)
                .gender(Gender.FEMALE)
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();
        customerRepository.save(receiver);


        Pocket senderPocket = Pocket.builder()
                .customer(sender)
                .balance(new BigDecimal("1000"))
                .currency(Currency.getInstance("USD"))
                .status(PocketStatus.ACTIVE)
                .build();
        pocketRepository.save(senderPocket);

        Pocket receiverPocket = Pocket.builder()
                .customer(receiver)
                .balance(new BigDecimal("500"))
                .currency(Currency.getInstance("USD"))
                .status(PocketStatus.ACTIVE)
                .build();
        pocketRepository.save(receiverPocket);


        TransferResultRequest request = new TransferResultRequest();
        request.setPocketId(senderPocket.getId());
        request.setUsername(receiver.getUsername());
        request.setAmount(new BigDecimal("200"));


        Transactions transaction = pocketService.transfer(request, sender);


        assertEquals(TransactionStatus.SUCCEEDED, transaction.getStatus());
        Pocket updatedSenderPocket = pocketRepository.findById(senderPocket.getId()).get();
        Pocket updatedReceiverPocket = pocketRepository.findById(receiverPocket.getId()).get();

        assertEquals(new BigDecimal("800"), updatedSenderPocket.getBalance());
        assertEquals(new BigDecimal("700"), updatedReceiverPocket.getBalance());

    }



    @Test
    void givenInvalidReceiver_whenTransfer_thenTransactionFailed() {

        Customer sender = Customer.builder()
                .username("sender" + System.currentTimeMillis())
                .password("Password1")
                .phoneNumber("0897" + (System.nanoTime() % 100000))
                .country(Country.BULGARIA)
                .gender(Gender.MALE)
                .role(UserRole.USER)
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();
        customerRepository.save(sender);

        Pocket senderPocket = Pocket.builder()
                .customer(sender)
                .balance(new BigDecimal("1000"))
                .currency(Currency.getInstance("USD"))
                .status(PocketStatus.ACTIVE)
                .build();
        pocketRepository.save(senderPocket);

        TransferResultRequest request = new TransferResultRequest();
        request.setPocketId(senderPocket.getId());
        request.setUsername("invalidReceiver");
        request.setAmount(new BigDecimal("200"));

        Transactions transaction = pocketService.transfer(request, sender);

        assertEquals(TransactionStatus.FAILED, transaction.getStatus());

    }
}

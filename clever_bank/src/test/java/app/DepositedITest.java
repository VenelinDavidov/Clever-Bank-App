package app;


import app.customer.model.Country;
import app.customer.model.Customer;
import app.customer.model.Gender;
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
public class DepositedITest {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private PocketRepository pocketRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private PocketService pocketService;

    @Autowired
    private TransactionRepository transactionsRepository;



    @Test
    void givenValidDepositRequest_whenDeposit_thenBalanceUpdatedAndTransactionSaved() {

      // 1) Create customer
        String uniquePhone = "0895" + System.currentTimeMillis() % 10000000;
        Customer customer = customerService.register(RegisterRequest.builder()
                .username("KoKo" + System.currentTimeMillis())
                .password("Venelin1")
                .phoneNumber(uniquePhone)
                .country(Country.BULGARIA)
                .gender(Gender.MALE)
                .build());

        // 2) Create pocket
        Pocket pocket = new Pocket ();
        pocket.setCustomer(customer);
        pocket.setStatus(PocketStatus.ACTIVE);
        pocket.setBalance(new BigDecimal("50.00"));
        pocket.setCurrency(Currency.getInstance("USD"));
        pocket.setCreatedOn(LocalDateTime.now());

        pocketRepository.save (pocket);

        UUID pocketId = pocket.getId ();
        UUID customerId = customer.getId ();

        // 3) Create DepositRequest
        DepositRequest depositRequest = new DepositRequest();
        depositRequest.setAmount ( BigDecimal.valueOf (100.00));
        depositRequest.setIban ("BG1234567890");

        // 4) Call service method
        Transactions depositTransaction = pocketService.deposit (pocketId, depositRequest, customerId);

        // 5) Verify transaction status
        assertEquals (TransactionStatus.SUCCEEDED, depositTransaction.getStatus ());

        // 6) Verify balance updated
        Pocket updatedPocket = pocketRepository.findByIdAndCustomerId (pocketId, customerId).orElseThrow ();
        assertEquals (new BigDecimal ("150.00"), updatedPocket.getBalance ());
        assertEquals("USD", updatedPocket.getCurrency().getCurrencyCode());

        // 7) Verify transaction persisted
        List<Transactions> transactionsForPocket = transactionsRepository.findByPocketId(pocketId);
        assertEquals(1, transactionsForPocket.size());

        Transactions savedTransaction = transactionsForPocket.get(0);
        assertEquals(pocketId, savedTransaction.getPocket().getId());
        assertEquals(BigDecimal.valueOf(100.00), savedTransaction.getAmount());
        assertEquals(TransactionStatus.SUCCEEDED, savedTransaction.getStatus());


    }
}

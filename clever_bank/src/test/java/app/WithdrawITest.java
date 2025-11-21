package app;

import app.customer.model.Country;
import app.customer.model.Customer;
import app.customer.model.Gender;
import app.customer.model.UserRole;
import app.customer.repository.CustomerRepository;

import app.pocket.model.Pocket;
import app.pocket.model.PocketStatus;
import app.pocket.repository.PocketRepository;
import app.pocket.service.PocketService;

import app.transaction.model.TransactionStatus;
import app.transaction.model.TransactionType;
import app.transaction.model.Transactions;
import app.transaction.repository.TransactionRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;



import static org.junit.jupiter.api.Assertions.assertEquals;
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
public class WithdrawITest {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PocketRepository pocketRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private PocketService pocketService;




    @Test
    void givenInsufficientBalance_whenWithdraw_thenTransactionFailed() {


        Customer customer = Customer.builder()
                .username("user_fail_" + System.currentTimeMillis())
                .password("Password1")
                .phoneNumber("0897" + (System.nanoTime() % 100000))
                .country(Country.BULGARIA)
                .gender(Gender.MALE)
                .role(UserRole.USER)
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .isActive(true)
                .build();

        customer = customerRepository.save(customer);



        Pocket pocket = Pocket.builder()
                .balance(new BigDecimal("20.00"))
                .currency(Currency.getInstance("USD"))
                .status(PocketStatus.ACTIVE)
                .customer(customer)
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();

        pocket = pocketRepository.save(pocket);



        BigDecimal withdrawAmount = new BigDecimal("100.00");

        Transactions withdraw = pocketService.withdraw(
                customer,
                pocket.getId(),
                withdrawAmount,
                "Test insufficient balance"
        );

        assertEquals(TransactionStatus.FAILED, withdraw.getStatus());
        assertEquals("Insufficient balance", withdraw.getReasonFailed());

        Pocket updatedPocket = pocketRepository.findById(pocket.getId()).orElseThrow();
        assertEquals(new BigDecimal("20.00"), updatedPocket.getBalance());
    }

    @Test
    void givenInactivePocket_whenWithdraw_thenReturnInactivePocket(){

        Customer customer = Customer.builder()
                .username("user_fail_" + System.currentTimeMillis())
                .password("Password1")
                .phoneNumber("0897" + (System.nanoTime() % 100000))
                .country(Country.BULGARIA)
                .gender(Gender.MALE)
                .role(UserRole.USER)
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .isActive(true)
                .build();

        customer = customerRepository.save(customer);

        Pocket pocket = Pocket.builder()
                .balance(new BigDecimal("200.00"))
                .currency(Currency.getInstance("USD"))
                .status(PocketStatus.INACTIVE)
                .customer(customer)
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();

        pocket = pocketRepository.save(pocket);

        Transactions withdraw = pocketService.withdraw (customer, pocket.getId (), new BigDecimal ("100.00"), "Inactive pocket status");

        assertEquals(TransactionStatus.FAILED, withdraw.getStatus());
        assertEquals("Inactive pocket status", withdraw.getReasonFailed());

    }


    @Test
    void givenActivePockets_whenApplyMonthlyFees_thenBalanceReducedAndTransactionCreated() {

        Customer customer = Customer.builder()
                .username("user_" + System.currentTimeMillis())
                .password("Password1")
                .phoneNumber("0897" + (System.nanoTime() % 100000))
                .country(Country.BULGARIA)
                .gender(Gender.MALE)
                .role(UserRole.USER)
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .isActive(true)
                .build();
        customer = customerRepository.save(customer);


        Pocket pocket = Pocket.builder()
                .balance(new BigDecimal("100.00"))
                .currency(Currency.getInstance("USD"))
                .status(PocketStatus.ACTIVE)
                .customer(customer)
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();
        pocket = pocketRepository.save(pocket);


        pocketService.applyMonthlyFees();


        Pocket updatedPocket = pocketRepository.findById(pocket.getId()).orElseThrow();
        assertEquals(new BigDecimal("98.00"), updatedPocket.getBalance());


        List<Transactions> txs = transactionRepository.findByPocketId(pocket.getId());
        assertEquals(1, txs.size());
        Transactions tx = txs.get(0);
        assertEquals(TransactionStatus.SUCCEEDED, tx.getStatus());
        assertEquals(new BigDecimal("2.00"), tx.getAmount());
        assertEquals(TransactionType.WITHDRAWAL, tx.getType());
        assertEquals("Clever Bank", tx.getReceiver());
        assertEquals("Monthly fee applied", tx.getDescription());
    }

}
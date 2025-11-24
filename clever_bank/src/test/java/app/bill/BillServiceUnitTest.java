package app.bill;

import app.bills_utility.model.Bill;
import app.bills_utility.model.BillCategory;
import app.bills_utility.model.BillStatus;
import app.bills_utility.repository.BillRepository;
import app.bills_utility.service.BillService;
import app.customer.model.Country;
import app.customer.model.Customer;
import app.customer.model.Gender;
import app.customer.model.UserRole;
import app.customer.repository.CustomerRepository;
import app.pocket.model.Pocket;
import app.pocket.model.PocketStatus;
import app.pocket.service.PocketService;
import app.web.dto.BillsRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.text.html.Option;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BillServiceUnitTest {

    @Mock
    private BillRepository billRepository;

    @Mock
    private PocketService pocketService;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private BillService billService;

    private Customer customer;
    private Pocket activePocket;
    private Bill unpaidBill;
    private Bill paidBill;

    @BeforeEach
    void setUp() {
        customer = Customer.builder ()
                .id (UUID.randomUUID ())
                .username ("sender" + System.currentTimeMillis ())
                .password ("Password1")
                .phoneNumber ("0897" + (System.nanoTime () % 100000))
                .email ("sender" + System.currentTimeMillis () + "@gmail.com")
                .gender (Gender.FEMALE)
                .country (Country.BULGARIA)
                .role (UserRole.USER)
                .createdOn (LocalDateTime.now ())
                .updatedOn (LocalDateTime.now ())
                .build ();

        activePocket = Pocket.builder ()
                .id (UUID.randomUUID ())
                .balance (new BigDecimal ("200.00"))
                .currency (Currency.getInstance ("USD"))
                .status (PocketStatus.ACTIVE)
                .customer (customer)
                .createdOn (LocalDateTime.now ())
                .updatedOn (LocalDateTime.now ())
                .build ();

        customer.setWallets (List.of (activePocket));

        unpaidBill = Bill.builder ()
                .id (UUID.randomUUID ())
                .billNumber ("BG1231231231")
                .customer (customer)
                .amount (new BigDecimal ("150.00"))
                .status (BillStatus.PENDING)
                .category (BillCategory.ELECTRICITY)
                .description ("Electricity Bill")
                .createdOn (LocalDateTime.now ())
                .build ();

        paidBill = Bill.builder ()
                .id (UUID.randomUUID ())
                .billNumber ("BG1231231232")
                .customer (customer)
                .amount (new BigDecimal ("100.00"))
                .status (BillStatus.PAID)
                .category (BillCategory.ELECTRICITY)
                .description ("Water Bill")
                .createdOn (LocalDateTime.now ())
                .build ();

    }


    @Test
    void payBill_shouldMarkBillAsPaid_whenBalanceIsSufficient() {
        when (billRepository.findById (unpaidBill.getId ())).thenReturn (Optional.of (unpaidBill));

        Bill result = billService.payBill (unpaidBill.getId ());

        assertEquals (BillStatus.PAID, result.getStatus ());
        assertNotNull (result.getUpdatedOn ());

        verify (pocketService, times (1))
                .withdraw (customer, activePocket.getId (), unpaidBill.getAmount (), unpaidBill.getDescription ());
        verify (billRepository, times (1)).save (unpaidBill);
    }

    @Test
    void payBill_shouldThrowException_whenBillAlreadyPaid() {
        when (billRepository.findById (paidBill.getId ())).thenReturn (Optional.of (paidBill));

        RuntimeException exception = assertThrows (RuntimeException.class, () -> billService.payBill (paidBill.getId ()));

        assertEquals ("Bill is already paid", exception.getMessage ());

        verify (pocketService, never ()).withdraw (any (), any (), any (), any ());
        verify (billRepository, never ()).save (any ());

    }



    @Test
    void payBill_shouldCancelBill_whenBalanceIsInsufficient() {
        activePocket.setBalance (new BigDecimal ("50.00"));
        when(billRepository.findById (unpaidBill.getId ())).thenReturn (Optional.of (unpaidBill));

        Bill result = billService.payBill (unpaidBill.getId ());

        assertEquals (BillStatus.CANCELED, result.getStatus ());
        assertNotNull (result.getUpdatedOn ());

        verify (pocketService, times (1)).withdraw (customer, activePocket.getId (), unpaidBill.getAmount (), unpaidBill.getDescription ());
        verify (billRepository, times (1)).save (unpaidBill);

    }


   @Test
    void givenCreateBill_shouldReturnSavedBill() {

       BillsRequest billsRequest = new BillsRequest();
       billsRequest.setBillNumber("BG1231231233");
       billsRequest.setAmount(new BigDecimal("100.00"));
       billsRequest.setDescription("Electricity Bill");
       billsRequest.setBillCategory(BillCategory.ELECTRICITY);

       Bill saveBill = Bill.builder ()
               .billNumber (billsRequest.getBillNumber ())
               .amount (billsRequest.getAmount ())
               .description (billsRequest.getDescription ())
               .category (billsRequest.getBillCategory ())
               .status (BillStatus.PENDING)
               .customer (customer)
               .createdOn (LocalDateTime.now ())
               .build ();

       when(billRepository.save (any (Bill.class))).thenReturn (saveBill);

       Bill result = billService.createBill (billsRequest, customer);

       assertEquals (saveBill.getId (), result.getId ());
       assertEquals (saveBill.getBillNumber (), result.getBillNumber ());
       assertEquals (BillStatus.PENDING, result.getStatus ());

       verify (billRepository, times (1)).save (any (Bill.class));
   }
}

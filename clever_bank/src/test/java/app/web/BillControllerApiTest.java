package app.web;


import app.bills_utility.model.Bill;
import app.bills_utility.model.BillCategory;
import app.bills_utility.model.BillStatus;
import app.bills_utility.repository.BillRepository;
import app.bills_utility.service.BillService;
import app.customer.model.Customer;
import app.customer.model.UserRole;
import app.customer.service.CustomerService;
import app.security.AuthenticationMetadataDetails;
import app.transaction.service.TransactionService;
import app.web.dto.BillsRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BillController.class)
public class BillControllerApiTest {

    @MockitoBean
    private  BillService billService;
    @MockitoBean
    private  CustomerService customerService;
    @MockitoBean
    private TransactionService transactionService;
    @MockitoBean
    private BillRepository billRepository;

    @Autowired
    private MockMvc mockMvc;

    private static Customer customer;
    private static Bill bill;

    @BeforeAll
    static void setUp(){
        UUID customerId = UUID.randomUUID();

         customer = Customer.builder()
                .id(customerId)
                .firstName("Venko")
                .lastName("Davidov")
                .email("venko@abv.bg")
                .phoneNumber("0895121212")
                .role (UserRole.ADMIN)
                .profilePicture("https://example.com/profile.jpg")
                .address("Sofia")
                .build();

        bill = Bill.builder().id(UUID.randomUUID())
                .id (UUID.randomUUID())
                .billNumber("BG1231231231")
                .amount(new BigDecimal("10.00"))
                .description("Electricity Bill")
                .category(BillCategory.ELECTRICITY)
                .status(BillStatus.PENDING)
                .customer(customer)
                .createdOn(LocalDateTime.now())
                .build();
    }


    @Test
    void givenRequestToBillPage_whenGetBillPage_thenFetchBillPage() throws Exception {



        AuthenticationMetadataDetails authenticationMetadataDetails = new AuthenticationMetadataDetails (
                customer.getId(),
                "Venko123",
                "Venelin7",
                UserRole.ADMIN,
                true,
                LocalDateTime.now (),
                LocalDateTime.now ()
        );

        BillsRequest billsRequest = new BillsRequest ();
        billsRequest.setAmount (new BigDecimal ("10.00"));
        billsRequest.setBillNumber ("BG1231231233");
        billsRequest.setBillCategory (BillCategory.ELECTRICITY);
        billsRequest.setDescription ("Electricity Bill");


        when (customerService.getById (authenticationMetadataDetails.getCustomerId ())).thenReturn (customer);
        when (billService.getAllBillsByCustomer (customer)).thenReturn (List.of (bill));

        //when
        MockHttpServletRequestBuilder request = get ("/bills")
                .with (user (authenticationMetadataDetails))
                .with (csrf ());

        mockMvc.perform (request)
                .andExpect (status ().is2xxSuccessful ())
                .andExpect (view().name("bills"))
                .andExpect (model ().attributeExists ("customer"))
                .andExpect (model().attributeExists ("bills"))
                .andExpect (model().attributeExists ("billsRequest"));


        verify(billService, times(1)).getAllBillsByCustomer(customer);
        verify(customerService, times(1)).getById(authenticationMetadataDetails.getCustomerId ());
    }



    @Test
    void givenRequestToCreateBillPage_whenGetCreateBillPage_thenFetchCreateBillPage() throws Exception {

        AuthenticationMetadataDetails authenticationMetadataDetails = new AuthenticationMetadataDetails (
                customer.getId(),
                "Venko123",
                "Venelin7",
                UserRole.ADMIN,
                true,
                LocalDateTime.now (),
                LocalDateTime.now ()
        );

        BillsRequest billsRequest = new BillsRequest ();
        billsRequest.setAmount (new BigDecimal ("10.00"));
        billsRequest.setBillNumber ("BG12312312");
        billsRequest.setBillCategory (BillCategory.ELECTRICITY);
        billsRequest.setDescription ("Electricity Bill");

        when (customerService.getById (authenticationMetadataDetails.getCustomerId ())).thenReturn (customer);

        MockHttpServletRequestBuilder request = post ("/bills/add")
                .param ("amount", "10.00")
                .param ("billNumber", "BG12312312")
                .param ("billCategory", "ELECTRICITY")
                .param ("description", "Electricity Bill")
                .with (user (authenticationMetadataDetails))
                .with (csrf ());

        mockMvc.perform (request)
                .andExpect (status().is3xxRedirection ())
                .andExpect (redirectedUrl("/bills"))
                .andExpect (flash().attributeExists("success"));

        verify(billService, times(1)).createBill(any(BillsRequest.class), eq(customer));
        verify (customerService, times (1)).getById (customer.getId());

    }



    @Test
    void givenRequestToInvalidData_whenInvokeCreateBill_thenReturnBindingResult() throws Exception {

        AuthenticationMetadataDetails authenticationMetadataDetails = new AuthenticationMetadataDetails (
                customer.getId(),
                "Venko123",
                "Venelin7",
                UserRole.ADMIN,
                true,
                LocalDateTime.now (),
                LocalDateTime.now ()
        );

        BillsRequest billsRequest = new BillsRequest ();
        billsRequest.setAmount (new BigDecimal ("10.00"));
        billsRequest.setBillNumber ("BG12312312");
        billsRequest.setBillCategory (BillCategory.ELECTRICITY);
        billsRequest.setDescription ("Electricity Bill");

        when (customerService.getById (authenticationMetadataDetails.getCustomerId ())).thenReturn (customer);
        when (billService.getAllBillsByCustomer (customer)).thenReturn (List.of ());

        MockHttpServletRequestBuilder request = post ("/bills/add")
                .with (user (authenticationMetadataDetails))
                .with (csrf ())
                .param ("billNumber", "")
                .param ("amount", "")
                .param ("billCategory", "ELECTRICITY")
                .param ("description", "Electricity Bill");

        mockMvc.perform (request)
                .andExpect (status().isOk ())
                .andExpect (view ().name ("bills"))
                .andExpect (model().attributeExists("customer"))
                .andExpect (model().attributeExists("bills"))
                .andExpect (model().attributeExists("billsRequest"))
                .andExpect (model ().hasErrors ());



    }


    @Test
    void givenRequestToPayBill_whenPushPayBill_thenReturnSuccessfullPayBillAndRedirectToTransactionPage() throws Exception {



        AuthenticationMetadataDetails authenticationMetadataDetails = new AuthenticationMetadataDetails (
                customer.getId(),
                "Venko123",
                "Venelin7",
                UserRole.ADMIN,
                true,
                LocalDateTime.now (),
                LocalDateTime.now ()
        );

        when (customerService.getById (authenticationMetadataDetails.getCustomerId ())).thenReturn (customer);
        when (billService.payBill (bill.getId ())).thenReturn (bill);

        MockHttpServletRequestBuilder request = post("/bills/pay/{id}", bill.getId ())
                .with(user(authenticationMetadataDetails))
                .with(csrf());

        mockMvc.perform (request)
                .andExpect (status().is3xxRedirection ())
                .andExpect (redirectedUrl("/transactions"))
                .andExpect (flash().attributeExists("success"));

        verify (billService, times (1)).payBill (bill.getId ());
        verify (customerService, times (1)).getById (authenticationMetadataDetails.getCustomerId ());
    }



    @Test
    void givenRequestToDeleteBill_whenPushDeleteBill_thenSuccessfullDeleteBillAndRedirectToBillPage() throws Exception {

        AuthenticationMetadataDetails authenticationMetadataDetails = new AuthenticationMetadataDetails (
                customer.getId(),
                "Venko123",
                "Venelin7",
                UserRole.ADMIN,
                true,
                LocalDateTime.now (),
                LocalDateTime.now ()
        );

        when (customerService.getById (authenticationMetadataDetails.getCustomerId ())).thenReturn (customer);
        doNothing ().when (billService).deleteBill (bill.getId ());

        MockHttpServletRequestBuilder request = delete ("/bills/delete/{id}", bill.getId ())
                .with(user(authenticationMetadataDetails))
                .with(csrf());

        mockMvc.perform (request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/bills"))
                .andExpect (flash().attributeExists("successMessage"));

        verify (billService, times (1)).deleteBill (bill.getId ());
        verify (customerService, times (1)).getById (authenticationMetadataDetails.getCustomerId ());
    }

}

package app.web;

import app.bills_utility.model.Bill;
import app.bills_utility.repository.BillRepository;
import app.bills_utility.service.BillService;
import app.customer.model.Customer;
import app.customer.service.CustomerService;
import app.security.AuthenticationMetadataDetails;
import app.transaction.service.TransactionService;
import app.web.dto.BillsRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;


@Controller
@RequestMapping("/bills")
public class BillController {

    private final BillService billService;
    private final CustomerService customerService;
    private final TransactionService transactionService;
    private final BillRepository billRepository;


    @Autowired
    public BillController(BillService billService,
                          CustomerService customerService,
                          TransactionService transactionService, BillRepository billRepository) {
        this.billService = billService;
        this.customerService = customerService;
        this.transactionService = transactionService;
        this.billRepository = billRepository;
    }




    // Get bills page
    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ModelAndView fetchBillPage(@AuthenticationPrincipal AuthenticationMetadataDetails authenticationMetadataDetails) {

        Customer customer = customerService.getById(authenticationMetadataDetails.getCustomerId());
        List<Bill> bills = billService.getAllBillsByCustomer(customer);

        ModelAndView modelAndView = new ModelAndView ();
        modelAndView.addObject ("customer", customer);
        modelAndView.addObject ("bills", bills);
        modelAndView.addObject ("billsRequest", BillsRequest.builder ().build ());
        modelAndView.setViewName ("bills");

        return modelAndView;
    }




    //Create bill
    @PostMapping("/add")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ModelAndView createBill(@AuthenticationPrincipal AuthenticationMetadataDetails authenticationMetadataDetails,
                                   @Valid BillsRequest billsRequest,
                                   BindingResult bindingResult,
                                   RedirectAttributes redirectAttributes){

        Customer customer = customerService.getById (authenticationMetadataDetails.getCustomerId ());

        if (bindingResult.hasErrors ()){
            List <Bill> bills = billService.getAllBillsByCustomer (customer);

            ModelAndView modelAndView = new ModelAndView ();
            modelAndView.addObject ("customer", customer);
            modelAndView.addObject ("bills", bills);
            modelAndView.setViewName ("bills");
            modelAndView.addObject ("billsRequest", billsRequest);
            return modelAndView;
        }

        Bill bill = billService.createBill (billsRequest, customer);
        redirectAttributes.addFlashAttribute ("success", "Bill created successfully");

        return new ModelAndView ("redirect:/bills");
    }





    // Pay bill
    @PostMapping("/pay/{id}")
    public ModelAndView payBill(@PathVariable("id") UUID billId,
                                @AuthenticationPrincipal AuthenticationMetadataDetails authenticationMetadataDetails,
                                RedirectAttributes redirectAttributes) {

        customerService.getById (authenticationMetadataDetails.getCustomerId ());

        Bill bill = billService.payBill (billId);

        redirectAttributes.addFlashAttribute ("Success", "Bill paid successfully" + bill.getBillNumber ());

        return new ModelAndView ("redirect:/transactions");
    }






    // Delete bill
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ModelAndView deleteBill(@PathVariable("id") UUID billId,
                                   @AuthenticationPrincipal AuthenticationMetadataDetails authenticationMetadataDetails,
                                   RedirectAttributes redirectAttributes) {

        try {
            billService.deleteBill (billId);
            redirectAttributes.addFlashAttribute("successMessage", "Bill deleted successfully!");
        }catch (Exception e){
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete bill: " + e.getMessage());
        }

        return new ModelAndView("redirect:/bills");
    }
}

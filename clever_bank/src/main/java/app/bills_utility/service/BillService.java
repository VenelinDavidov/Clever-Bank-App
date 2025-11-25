package app.bills_utility.service;


import app.bills_utility.model.Bill;
import app.bills_utility.model.BillStatus;
import app.bills_utility.repository.BillRepository;
import app.customer.model.Customer;
import app.pocket.model.Pocket;
import app.pocket.model.PocketStatus;
import app.pocket.service.PocketService;
import app.transaction.service.TransactionService;
import app.web.dto.BillsRequest;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import java.util.List;
import java.util.UUID;




@Slf4j
@Service
public class BillService {

    private static final String CREATE_BILL_MESSAGE = "Successfully created bill %s with id %s";
    private static final String DELETE_BILL_MESSAGE = "Successfully deleted bill with id %s";
    private static final String UPDATE_BILL_STATUS_MESSAGE = "Successfully updated bill %s status to %s";
    private static final String CLEVER_BANK_LTD = "Clever Bank Service Ltd";

        private final BillRepository billRepository;
        private final TransactionService transactionService;
        private final PocketService pocketService;




    @Autowired
    public BillService(BillRepository billRepository, TransactionService transactionService, PocketService pocketService) {
        this.billRepository = billRepository;

        this.transactionService = transactionService;
        this.pocketService = pocketService;
    }



    // Create new bill
    public Bill createBill(BillsRequest billsRequest, Customer customer) {

        Bill bill = billRepository.save (createNewBill (billsRequest,customer));
        log.info (CREATE_BILL_MESSAGE.formatted (bill.getBillNumber (), bill.getId ()));
        return bill;
    }

    private Bill createNewBill(BillsRequest billsRequest, Customer customer) {
        return Bill.builder ()
                .billNumber (billsRequest.getBillNumber ())
                .amount (billsRequest.getAmount ())
                .description (billsRequest.getDescription ())
                .category (billsRequest.getBillCategory ())
                .status (BillStatus.PENDING)
                .customer (customer)
                .createdOn (LocalDateTime.now ())
                .build ();
    }


    public List <Bill> getAllBillsByCustomer(Customer customer) {
        return billRepository.findAllByCustomerOrderByCreatedOnDesc(customer);
    }




    public void deleteBill(UUID billId) {

        billRepository.deleteById (billId);
        log.info (DELETE_BILL_MESSAGE.formatted (billId));

    }


    @Transactional
    public Bill payBill(UUID billId) {

        Bill bill = billRepository.findById (billId)
                .orElseThrow (() -> new RuntimeException ("Bill not found"));



        if (bill.getStatus () == BillStatus.PAID){
           throw new RuntimeException ("Bill is already paid");
        }

        Customer customer = bill.getCustomer ();

        Pocket activePocket =
                 customer.getWallets ()
                .stream ()
                .filter (p -> p.getStatus () == PocketStatus.ACTIVE)
                .findFirst ()
                .orElseThrow (() -> new RuntimeException ("Active wallet not found"));

        if (activePocket.getBalance ().compareTo (bill.getAmount ()) < 0){

            bill.setStatus (BillStatus.CANCELED);
            bill.setUpdatedOn (LocalDateTime.now ());
            pocketService.withdraw (customer, activePocket.getId (), bill.getAmount (), bill.getDescription ());
            billRepository.save(bill);
            return bill;
        }


        pocketService.withdraw (customer, activePocket.getId (), bill.getAmount (), bill.getDescription ());

        bill.setStatus (BillStatus.PAID);
        bill.setUpdatedOn (LocalDateTime.now ());

        billRepository.save (bill);

        return bill;
    }


}

package app.bills_utility.service;


import app.bills_utility.model.Bill;
import app.bills_utility.model.BillStatus;
import app.bills_utility.repository.BillRepository;
import app.customer.model.Customer;
import app.web.dto.BillsRequest;

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

    private final BillRepository billRepository;



    @Autowired
    public BillService(BillRepository billRepository) {
        this.billRepository = billRepository;
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



    // Delete bill by id
    public void deleteBill(UUID billId) {

        billRepository.deleteById (billId);
        log.info (DELETE_BILL_MESSAGE.formatted (billId));
    }





}

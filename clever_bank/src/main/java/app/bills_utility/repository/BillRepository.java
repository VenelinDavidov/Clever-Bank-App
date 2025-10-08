package app.bills_utility.repository;

import app.bills_utility.model.Bill;

import app.customer.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.UUID;
@Repository
public interface BillRepository extends JpaRepository <Bill, UUID> {


    List<Bill> findAllByCustomerOrderByCreatedOnDesc(Customer customer);



}

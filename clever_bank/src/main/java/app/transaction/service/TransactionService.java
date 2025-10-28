package app.transaction.service;

import app.bills_utility.model.Bill;
import app.customer.model.Customer;
import app.exception.DomainException;
import app.pocket.model.Pocket;
import app.transaction.model.TransactionStatus;
import app.transaction.model.TransactionType;
import app.transaction.model.Transactions;
import app.transaction.repository.TransactionRepository;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;



@Slf4j
@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;


    @Autowired
    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }


    // Get last 7 transactions by pocket id
    public List <Transactions> getLastSevenTransactionsByPocketId(Pocket pocket) {

        List <Transactions> lastSevenTransaction = transactionRepository
                .findAllBySenderOrReceiverOrderByCreatedOnDesc (pocket.getId ().toString (), pocket.getId ().toString ())
                .stream ()
                .filter (tr -> tr.getCustomer ().getId () == (pocket.getCustomer ().getId ()))
                .filter (t -> t.getStatus () == TransactionStatus.SUCCEEDED)
                .limit (7)
                .collect (Collectors.toList ());

        return lastSevenTransaction;
    }




    public Transactions getById(UUID id) {

        return transactionRepository.findById (id)
                .orElseThrow (() -> new DomainException ("Transaction with id %s not found"
                        .formatted (id), HttpStatus.BAD_REQUEST));
    }





    public List <Transactions> getAllByCustomerId(UUID customerId) {
        return transactionRepository
                .findAllByCustomerIdOrderByCreatedOnDesc (customerId);

    }





    public Transactions initializeNextPocket(Customer customer,String iban, String cleverBankLtd, String string,
                                     BigDecimal amount, BigDecimal balanceLeft, Currency currency,
                                     TransactionType transactionType, TransactionStatus transactionStatus,
                                     String descriptionInformation, String inactivePocket) {



        Transactions transactions = Transactions.builder()
                .customer (customer)
                .iban (null)
                .sender (cleverBankLtd)
                .receiver (string)
                .amount (amount)
                .remainingBalance (balanceLeft)
                .currency (currency)
                .type (transactionType)
                .status (transactionStatus)
                .description (descriptionInformation)
                .reasonFailed (inactivePocket)
                .createdOn (LocalDateTime.now ())
                .build ();

        return transactionRepository.save (transactions);

    }




    public Transactions createNewTransaction(Customer customer, String sender,
                                             String receiver,  BigDecimal amount,
                                             BigDecimal balance, Currency currency,
                                             TransactionType transactionType,
                                             TransactionStatus transactionStatus,
                                             String descriptionInformation, String s) {

        Transactions transactions = Transactions.builder()
                .customer (customer)
                .iban (null)
                .sender (sender)
                .receiver (receiver)
                .amount (amount)
                .remainingBalance (balance)
                .currency (currency)
                .type (transactionType)
                .status (transactionStatus)
                .description (descriptionInformation)
                .reasonFailed (s)
                .createdOn (LocalDateTime.now ())
                .build ();

        return transactionRepository.save (transactions);
    }

}

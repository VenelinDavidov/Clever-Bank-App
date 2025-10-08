package app.transaction.service;

import app.pocket.model.Pocket;
import app.transaction.model.Transactions;
import app.transaction.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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


    public List <Transactions> getLastSevenTransactionsByPocketId(Pocket pocket) {

        List <Transactions> transactions = transactionRepository
                .findAllBySenderOrReceiverOrderByCreatedOnDesc (pocket.getId ().toString (), pocket.getId ().toString ())
                .stream ()
                .filter (tr -> tr.getCustomer ().getId () == (pocket.getCustomer ().getId ()))
                .limit (7)
                .collect (Collectors.toList ());

        return transactions;
    }




    public List<Transactions> getAllTransactionsByCustomerId(UUID customerId) {

        return transactionRepository.findAllByCustomerIdOrderByCreatedOnDesc (customerId);

    }
}

package app.pocket.service;

import app.customer.model.Customer;
import app.exception.DomainException;
import app.pocket.model.Pocket;
import app.pocket.model.PocketStatus;
import app.pocket.model.PocketType;
import app.pocket.repository.PocketRepository;
import app.transaction.model.Transactions;
import app.transaction.service.TransactionService;
import app.web.dto.DepositRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class PocketService {

    private static final String POKET_NOT_FOUND  ="Pocket with id %s not found and customer id %s not found";
    private static final String POCKET_ID_NOT_FOUND_WITH_CUSTOMER_ID = "Pocket with id %s not found and customer id %s not found";
    private static final String AMOUNT_NOT_VALID = "Amount must be greater than zero";
    private static final String DEPOSIT_SUCCESS = "Deposit of %s made to pocket with id %s and customer id %s";

    private final PocketRepository pocketRepository;
    private final TransactionService transactionService;


    @Autowired
    public PocketService(PocketRepository walletRepository,
                         TransactionService transactionService) {
        this.pocketRepository = walletRepository;
        this.transactionService = transactionService;
    }





    public Pocket createWallet(Customer customer) {

        List <Pocket> allByCustomerWallets =
                pocketRepository.findAllByCustomerUsername (customer.getUsername ());

        if (!allByCustomerWallets.isEmpty ()) {
            throw new DomainException ("Wallet with this username %s with id %s already has wallet!"
                    .formatted (customer.getUsername (), customer.getId ()), HttpStatus.BAD_REQUEST);
        }

        Pocket wallet = pocketRepository.save (createNewWallet (customer));

        log.info ("Wallet with username %s with id %s has been successfully created and hava balance %.2f"
                .formatted (wallet.getCustomer ().getUsername (),wallet.getId (), wallet.getBalance ()));

        return wallet;
    }



    private Pocket createNewWallet(Customer customer) {

      return  Pocket.builder ()
              .customer (customer)
              .status (PocketStatus.ACTIVE)
              .type (PocketType.BUSINESS)
              .balance (new BigDecimal ("40.00"))
              .currency (Currency.getInstance ("USD"))
              .createdOn (LocalDateTime.now ())
              .updatedOn (LocalDateTime.now ())
              .build ();
    }



    public Pocket getById(UUID customerId) {

        return pocketRepository.findById (customerId)
                               .orElseThrow (() -> new DomainException ("Pocket with id %s not found"
                               .formatted (customerId), HttpStatus.BAD_REQUEST));
    }






    public Map <UUID, List <Transactions>> getLastSevenTransactions(List <Pocket> pockets) {

          Map<UUID, List<Transactions>> pocketTransactions = new LinkedHashMap <> ();

          for (Pocket pocket : pockets) {

              List<Transactions> transactions = transactionService.getLastSevenTransactionsByPocketId (pocket);

              pocketTransactions.put (pocket.getId (), transactions);
          }
          return pocketTransactions;
    }





    public void switchStatusWallet(UUID pocketId, UUID customerId) {

        Optional <Pocket> pocketOptional = pocketRepository.findByIdAndCustomerId (pocketId, customerId);

        if (pocketOptional.isEmpty ()) {
            throw new DomainException (POKET_NOT_FOUND
                    .formatted (pocketId, customerId), HttpStatus.BAD_REQUEST);
        }

        Pocket pocket = pocketOptional.get ();

        if (pocket.getStatus () == PocketStatus.ACTIVE){
            pocket.setStatus (PocketStatus.INACTIVE);
        } else {
            pocket.setStatus (PocketStatus.ACTIVE);
        }

        pocketRepository.save (pocket);
    }




    @Transactional
    public void deposit(UUID pocketId,  DepositRequest depositRequest, UUID customerId) {

        Optional <Pocket> pocketOptional =
                         pocketRepository
                        .findByIdAndCustomerId (pocketId, customerId);

        if (pocketOptional.isEmpty ()) {
            throw new DomainException (POCKET_ID_NOT_FOUND_WITH_CUSTOMER_ID , HttpStatus.BAD_REQUEST);
        }

        if (depositRequest.getAmount () == null || depositRequest.getAmount ().compareTo (BigDecimal.ZERO) <= 0){
            throw new DomainException (AMOUNT_NOT_VALID, HttpStatus.BAD_REQUEST);
        }

        Pocket pocket = pocketOptional.get ();
        BigDecimal newBalance = pocket.getBalance ().add (depositRequest.getAmount ());

        pocket.setBalance (newBalance);

        pocketRepository.save (pocket);
        log.info (DEPOSIT_SUCCESS.formatted (depositRequest.getAmount (), pocketId, customerId));
    }
}

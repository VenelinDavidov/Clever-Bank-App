package app.pocket.service;

import app.customer.model.Customer;
import app.exception.DomainException;
import app.pocket.model.Pocket;
import app.pocket.model.PocketStatus;
import app.pocket.model.PocketType;
import app.pocket.repository.PocketRepository;
import app.transaction.model.TransactionStatus;
import app.transaction.model.TransactionType;
import app.transaction.model.Transactions;
import app.transaction.service.TransactionService;
import app.web.dto.DepositRequest;
import app.web.dto.TransferResultRequest;
import jakarta.transaction.Transactional;

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

    private static final String POKED_NOT_FOUND ="Pocket with id %s not found and customer id %s not found";
    private static final String POCKET_ID_NOT_FOUND_WITH_CUSTOMER_ID = "Pocket with id %s not found and customer id %s not found";
    private static final String AMOUNT_NOT_VALID = "Amount must be greater than zero";
    private static final String CLEVER_BANK_LTD = "Clever Bank Service Ltd";


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

        Pocket wallet = pocketRepository.save (createNewPocket (customer));

        log.info ("Wallet with username %s with id %s has been successfully created and hava balance %.2f"
                .formatted (wallet.getCustomer ().getUsername (),wallet.getId (), wallet.getBalance ()));

        return wallet;
    }



    private Pocket createNewPocket(Customer customer) {

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




     // get pocket by id
    public Pocket getPocketById(UUID customerId) {

        return pocketRepository.findById (customerId)
                               .orElseThrow (() -> new DomainException ("Pocket with id %s not found"
                               .formatted (customerId), HttpStatus.BAD_REQUEST));
    }






    public Map <UUID, List <Transactions>> getLastSevenTransactions(List <Pocket> pockets) {

          Map<UUID, List<Transactions>> pocketTransactions = new LinkedHashMap <> ();

          for (Pocket pocket : pockets) {

              List<Transactions> lastSevenTransactionsByPocketId = transactionService.getLastSevenTransactionsByPocketId (pocket);
              pocketTransactions.put (pocket.getId (), lastSevenTransactionsByPocketId);
          }
          return pocketTransactions;
    }





    public void switchStatusWallet(UUID pocketId, UUID customerId) {

        Optional <Pocket> pocketOptional = pocketRepository.findByIdAndCustomerId (pocketId, customerId);

        if (pocketOptional.isEmpty ()) {
            throw new DomainException (POKED_NOT_FOUND
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
    public Transactions deposit(UUID pocketId,  DepositRequest depositRequest, UUID customerId) {

        Optional <Pocket> pocketOptional = pocketRepository.findByIdAndCustomerId (pocketId, customerId);
        String descriptionInformation = "Your deposit up to %.2f USD".formatted (depositRequest.getAmount ());

        if (pocketOptional.isEmpty ()) {
            throw new DomainException (POCKET_ID_NOT_FOUND_WITH_CUSTOMER_ID , HttpStatus.BAD_REQUEST);
        }

        if (depositRequest.getAmount () == null || depositRequest.getAmount ().compareTo (BigDecimal.ZERO) <= 0){
            throw new DomainException (AMOUNT_NOT_VALID, HttpStatus.BAD_REQUEST);
        }


        Pocket pocket = pocketOptional.get ();

        if (pocket.getStatus () == PocketStatus.INACTIVE){

           return   transactionService.initializeNextPocket(
                    pocket.getCustomer (),
                    depositRequest.getIban (),
                    CLEVER_BANK_LTD,
                    pocketId.toString (),
                    depositRequest.getAmount (),
                    pocket.getBalance (),
                    pocket.getCurrency (),
                    TransactionType.DEPOSIT,
                    TransactionStatus.FAILED,
                    descriptionInformation,
                    "Inactive pocket");
        }

        BigDecimal newBalance = pocket.getBalance ().add (depositRequest.getAmount ());
        pocket.setBalance (newBalance);
        pocket.setUpdatedOn (LocalDateTime.now ());

        pocketRepository.save (pocket);

        return transactionService.initializeNextPocket(
                pocket.getCustomer (),
                depositRequest.getIban (),
                CLEVER_BANK_LTD,
                pocketId.toString (),
                depositRequest.getAmount (),
                pocket.getBalance (),
                pocket.getCurrency (),
                TransactionType.DEPOSIT,
                TransactionStatus.SUCCEEDED,
                descriptionInformation,
                "Success deposit!");
    }



    @Transactional
    public Transactions transfer( TransferResultRequest transferResultRequest, Customer customer) {

        Pocket pocketSender = getPocketById (transferResultRequest.getPocketId ());

        Optional <Pocket> pocketReceiver = pocketRepository.findAllByCustomerUsername (transferResultRequest.getUsername ())
                .stream ()
                .filter (pocket -> pocket.getStatus () == PocketStatus.ACTIVE)
                .findFirst ();

        String descriptionInformation = "Transfer from customer %s to customer %s with amount %s."
                .formatted (customer.getUsername (), transferResultRequest.getUsername (), transferResultRequest.getAmount ());


        if (pocketReceiver.isEmpty ()){
            return transactionService.createNewTransaction (
                    customer,
                    pocketSender.getId ().toString (),
                    transferResultRequest.getUsername (),
                    transferResultRequest.getAmount (),
                    pocketSender.getBalance (),
                    pocketSender.getCurrency (),
                    TransactionType.WITHDRAWAL,
                    TransactionStatus.FAILED,
                    descriptionInformation,
                    "Invalid transfer!"
                    );
        }

        Transactions retract = withdraw(customer, pocketSender.getId (), transferResultRequest.getAmount (), descriptionInformation);

        if (retract.getStatus () == TransactionStatus.FAILED){
            return retract;
        }


        Pocket receiverPocket = pocketReceiver.get ();
        receiverPocket.setBalance (receiverPocket.getBalance ().add (transferResultRequest.getAmount ()));
        receiverPocket.setUpdatedOn (LocalDateTime.now ());
        pocketRepository.save (receiverPocket);



//        Transactions senderTransaction = transactionService.createNewTransaction (
//                 customer,
//                 customer.getUsername (),
//                 transferResultRequest.getUsername (),
//                 transferResultRequest.getAmount (),
//                 pocketSender.getBalance (),
//                 pocketSender.getCurrency (),
//                 TransactionType.WITHDRAWAL,
//                 TransactionStatus.SUCCEEDED,
//                 descriptionInformation,
//                 "Success transfer!"
//        );


        Customer receiverCustomer = receiverPocket.getCustomer ();
        String descriptionReceiver = "Received from " + customer.getUsername () + " with amount " + transferResultRequest.getAmount ();

        transactionService.createNewTransaction (
                receiverCustomer,
                pocketSender.getId().toString(),
                receiverCustomer.getId ().toString (),
                transferResultRequest.getAmount (),
                receiverPocket.getBalance (),
                receiverPocket.getCurrency (),
                TransactionType.DEPOSIT,
                TransactionStatus.SUCCEEDED,
                descriptionReceiver,
                "Incoming transfer!"
        );

        return  retract;
    }





    @Transactional
    public Transactions withdraw(Customer customer, UUID pocketId,  BigDecimal amount,  String descriptionInformation) {

        Pocket pocket = getPocketById (pocketId);

        boolean isFaled = false;
        String reason = "";

        if (pocket.getStatus () == PocketStatus.INACTIVE){

            isFaled = true;
            reason = "Inactive pocket status";
        }


        if (pocket.getBalance ().compareTo (amount) <= 0){

            isFaled = true;
            reason = "Insufficient balance";

            return transactionService.createNewTransaction (
                    customer,
                    pocket.getId ().toString (),
                    CLEVER_BANK_LTD,
                    amount,
                    pocket.getBalance (),
                    pocket.getCurrency (),
                    TransactionType.WITHDRAWAL,
                    TransactionStatus.FAILED,
                    descriptionInformation,
                    reason
            );
        }


        if (isFaled){
            return transactionService.createNewTransaction (
                    customer,
                    pocket.getId ().toString (),
                    CLEVER_BANK_LTD,
                    amount,
                    pocket.getBalance (),
                    pocket.getCurrency (),
                    TransactionType.WITHDRAWAL,
                    TransactionStatus.FAILED,
                    descriptionInformation,
                    reason
            );
        }

        BigDecimal newBalancePocket = pocket.getBalance ().subtract (amount);
        pocket.setBalance (newBalancePocket);
        pocket.setUpdatedOn (LocalDateTime.now ());

        pocketRepository.save (pocket);

        return transactionService.createNewTransaction (
          customer,
          pocket.getId ().toString (),
          CLEVER_BANK_LTD,
          amount,
          pocket.getBalance (),
          pocket.getCurrency (),
          TransactionType.WITHDRAWAL,
          TransactionStatus.SUCCEEDED,
          descriptionInformation,
          "Success withdrawal!"
        );

    }


}

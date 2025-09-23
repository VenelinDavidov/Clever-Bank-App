package app.pocket.service;

import app.customer.model.Customer;
import app.exception.DomainException;
import app.pocket.model.Pocket;
import app.pocket.model.PocketStatus;
import app.pocket.model.PocketType;
import app.pocket.repository.PocketRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;
@Slf4j
@Service
public class PocketService {

    private final PocketRepository walletRepository;


    @Autowired
    public PocketService(PocketRepository walletRepository) {
        this.walletRepository = walletRepository;
    }





    public Pocket createWallet(Customer customer) {

        List <Pocket> allByCustomerWallets =
                walletRepository.findAllByCustomerUsername (customer.getUsername ());

        if (!allByCustomerWallets.isEmpty ()) {
            throw new DomainException ("Wallet with this username %s with id %s already has wallet!"
                    .formatted (customer.getUsername (), customer.getId ()), HttpStatus.BAD_REQUEST);
        }

        Pocket wallet = walletRepository.save (createNewWallet (customer));

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
}

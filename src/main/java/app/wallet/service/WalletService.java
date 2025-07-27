package app.wallet.service;

import app.customer.model.Customer;
import app.exception.DomainException;
import app.wallet.model.Wallet;
import app.wallet.model.WalletStatus;
import app.wallet.model.WalletType;
import app.wallet.repository.WalletRepository;
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
public class WalletService {

    private final WalletRepository walletRepository;


    @Autowired
    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }





    public Wallet createWallet(Customer customer) {

        List <Wallet> allByCustomerUsername =
                walletRepository.findAllByCustomerUsername (customer.getUsername ());

        if (!allByCustomerUsername.isEmpty ()) {
            throw new DomainException ("Wallet with this username %s with id %s already has wallet!"
                    .formatted (customer.getUsername (), customer.getId ()), HttpStatus.BAD_REQUEST);
        }

        Wallet wallet = walletRepository.save (createNewWallet (customer));

        log.info ("Wallet with username %s with id %s has been successfully created and hava balance %.2f"
                .formatted (wallet.getCustomer ().getUsername (),wallet.getId (), wallet.getBalance ()));

        return wallet;
    }

    private Wallet  createNewWallet(Customer customer) {

      return  Wallet.builder ()
              .customer (customer)
              .status (WalletStatus.ACTIVE)
              .type (WalletType.BUSINESS)
              .balance (new BigDecimal ("40.00"))
              .currency (Currency.getInstance ("USD"))
              .createdOn (LocalDateTime.now ())
              .updatedOn (LocalDateTime.now ())
              .build ();
    }
}

package app;

import app.cards.model.CardBrand;
import app.cards.model.CardLevel;
import app.cards.model.CardPeriod;
import app.cards.model.Cards;
import app.customer.model.Country;
import app.customer.model.Customer;
import app.customer.model.Gender;
import app.customer.model.UserRole;
import app.pocket.model.Pocket;
import app.pocket.model.PocketStatus;
import app.pocket.model.PocketType;
import app.subscription.model.Subscription;
import app.subscription.model.SubscriptionPeriod;
import app.subscription.model.SubscriptionStatus;
import app.subscription.model.SubscriptionType;
import lombok.experimental.UtilityClass;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;
import java.util.UUID;

@UtilityClass
public class TestBuilder {

    public static Customer aRandomCustomer() {

        Customer customer = Customer.builder ()
                .id (UUID.randomUUID ())
                .firstName ("Venelin")
                .lastName ("Davidov")
                .email ("venelin@abv.bg")
                .username ("Venko123")
                .password ("Venelin7")
                .role (UserRole.USER)
                .country (Country.BULGARIA)
                .gender (Gender.MALE)
                .address ("Sofia")
                .profilePicture ("pic.png")
                .phoneNumber ("0895121212")
                .createdOn (LocalDateTime.now ())
                .updatedOn (LocalDateTime.now ())
                .accountExpireAt (LocalDateTime.now ())
                .credentialsExpireAt (LocalDateTime.now ())
                .build ();

        Pocket pocket = Pocket.builder ()
                .id (UUID.randomUUID ())
                .customer (customer)
                .status (PocketStatus.ACTIVE)
                .type (PocketType.BUSINESS)
                .balance (new BigDecimal ("40.00"))
                .currency (Currency.getInstance ("USD"))
                .createdOn (LocalDateTime.now ())
                .updatedOn (LocalDateTime.now ())
                .build ();


        Subscription subscription = Subscription.builder ()
                .id (UUID.randomUUID ())
                .customer (customer)
                .status (SubscriptionStatus.ACTIVE)
                .type (SubscriptionType.DEFAULT)
                .serviceName ("Default")
                .period (SubscriptionPeriod.MONTHLY)
                .createdOn (LocalDateTime.now ())
                .completedOn (LocalDateTime.now ())
                .updateAllowed (true)
                .build ();



        Cards card = Cards.builder ()
                .id (UUID.randomUUID ())
                .customer (customer)
                .cardNumber ("1234567890123456")
                .cardBrand (CardBrand.MasterCard)
                .cardLevel (CardLevel.Gold)
                .isActive (true)
                .period (CardPeriod.SIX_MONTHLY)
                .totalLimit (1000)
                .availableAmount (1000)
                .updateAllowed (true)
                .completedOn (LocalDateTime.now ())
                .createdOn (LocalDateTime.now ())
                .build ();

        customer.setWallets(List.of(pocket));
        customer.setCards(List.of(card));
        customer.setSubscriptions(List.of(subscription));


        return customer;
    }
}

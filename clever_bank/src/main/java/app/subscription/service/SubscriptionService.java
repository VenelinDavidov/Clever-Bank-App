package app.subscription.service;

import app.customer.model.Customer;
import app.subscription.model.Subscription;
import app.subscription.model.SubscriptionPeriod;
import app.subscription.model.SubscriptionStatus;
import app.subscription.model.SubscriptionType;
import app.subscription.repository.SubscriptionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;


    @Autowired
    public SubscriptionService(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }




    public Subscription createDefaultSubscription(Customer customer) {

        Subscription subscription = subscriptionRepository.save (createNewSubscription (customer));
        log.info ("Successfully created default subscription for customer %s with id %s and type %s"
                .formatted (  customer.getUsername (), subscription.getId (), subscription.getType ()));

        return subscription;
    }




    private Subscription createNewSubscription(Customer customer) {

        LocalDateTime now = LocalDateTime.now ();

        return Subscription.builder ()
                .customer (customer)
                .serviceName ("Default Subscription")
                .description ("Create default subscription")
                .status (SubscriptionStatus.ACTIVE)
                .period (SubscriptionPeriod.MONTHLY)
                .type (SubscriptionType.DEFAULT)
                .charge (BigDecimal.ZERO)
                .updateAllowed (true)
                .createdOn (now)
                .completedOn (now.plusMonths (1))
                .build ();
    }
}

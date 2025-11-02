package app.event;

import app.event.payLoad.CustomerRegisteredEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CustomerRegisteredEventConsumer {


//    @KafkaListener(topics = "customer.registered.event.v1", groupId = "notification-ms")
//    public void consumeEvent (CustomerRegisteredEvent event) {
//        log.info("Received customer registered event: {}", event);
//    }

}

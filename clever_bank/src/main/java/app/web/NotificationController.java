package app.web;

import app.customer.model.Customer;
import app.customer.service.CustomerService;
import app.notification.client.dto.NotificationPreferenceResponse;
import app.notification.client.dto.NotificationResponse;
import app.notification.service.NotificationService;
import app.security.AuthenticationMetadataDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;


@Controller
@RequestMapping("/notifications")
public class NotificationController {


    private final CustomerService customerService;
    private final NotificationService notificationService;

    @Autowired
    public NotificationController(CustomerService customerService,
                                  NotificationService notificationService) {
        this.customerService = customerService;
        this.notificationService = notificationService;
    }





    @GetMapping
    public ModelAndView fetchNotificationPage(@AuthenticationPrincipal AuthenticationMetadataDetails authenticationMetadataPr) {

        Customer customer = customerService.getById (authenticationMetadataPr.getCustomerId ());

        NotificationPreferenceResponse notificationPreference = notificationService.getNotificationPreference (customer.getId ());
        List <NotificationResponse> notificationHistory = notificationService.getNotificationHistory (customer.getId ());
        long succeededNotificationsNumber = notificationHistory
                                                              .stream ()
                                                              .filter (n -> n.getStatus ().equals ("SUCCEEDED")).count ();
        long failedNotificationsNumber = notificationHistory
                                                              .stream ()
                                                              .filter (n -> n.getStatus ().equals ("FAILED")).count ();

        notificationHistory  = notificationHistory.stream ().limit (5).toList ();


        ModelAndView modelAndView = new ModelAndView ("notifications");
        modelAndView.addObject ("customer", customer);
        modelAndView.addObject ("notificationPreference",notificationPreference);
        modelAndView.addObject ("succeededNotificationsNumber", succeededNotificationsNumber);
        modelAndView.addObject ("failedNotificationsNumber", failedNotificationsNumber);
        modelAndView.addObject ("notificationHistory", notificationHistory);
        return modelAndView;
    }







    @PutMapping("/customer-preference")
    public String updateCustomerPreference(@RequestParam(name="enabled") boolean enabled, @AuthenticationPrincipal AuthenticationMetadataDetails authenticationMetadataPr){

     notificationService.updateNotificationPreference (authenticationMetadataPr.getCustomerId (), enabled);

     return "redirect:/notifications";
    }
}

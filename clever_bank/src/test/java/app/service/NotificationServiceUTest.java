package app.service;

import app.model.Notification;
import app.model.NotificationPreference;
import app.model.NotificationStatus;
import app.repository.NotificationPreferenceRepository;
import app.repository.NotificationRepository;
import app.web.dto.NotificationRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith (MockitoExtension.class)
public class NotificationServiceUTest {

    @Mock
    private NotificationPreferenceRepository notificationPreferenceRepository;
    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private MailSender mailSender;


    @InjectMocks
    private NotificationService notificationService;



    @Test
    void givenNotExistingNotificationPreference_whenChangeNotificationPreference_thenExpectException() {

        UUID customerId = UUID.randomUUID ();
        boolean notificationEnabled = true;
        when(notificationPreferenceRepository.findByCustomerId(customerId)).thenReturn(Optional.empty());

        assertThrows(NullPointerException.class, ()-> notificationService.changeNotificationPreference (customerId, notificationEnabled));
    }



    @Test
    void givenExistingNotificationPreference_whenChangeNotificationPreference_thenExpectSuccess() {

        UUID customerId = UUID.randomUUID ();
        NotificationPreference notificationPreference = NotificationPreference.builder()
                .customerId(customerId)
                .enabled(false)
                .build();
        when (notificationPreferenceRepository.findByCustomerId (customerId)).thenReturn (Optional.of (notificationPreference));

        notificationService.changeNotificationPreference (customerId, true);

        assertTrue (notificationPreference.isEnabled());
        verify(notificationPreferenceRepository, times(1)).save(notificationPreference);

    }



    @Test
    void givenNotEnabledSendNotification_whenExpectException(){

        UUID customerId = UUID.randomUUID ();
        NotificationPreference notificationPreference = NotificationPreference.builder()
                .customerId(customerId)
                .enabled(false)
                .build();
        when (notificationPreferenceRepository.findByCustomerId (customerId)).thenReturn (Optional.of (notificationPreference));

        assertThrows(IllegalArgumentException.class,()-> notificationService.sendNotification (NotificationRequest.builder().customerId(customerId).build()));
    }



    @Test
    void givenEnabledSendNotificationHappyPath_whenExpectSuccess(){
        UUID customerId = UUID.randomUUID ();
        //given
        NotificationPreference notificationPreference = NotificationPreference.builder()
                .customerId(customerId)
                .enabled(true)
                .contactInfo("test@example.com")
                .build();

        when (notificationPreferenceRepository.findByCustomerId (customerId)).thenReturn (Optional.of (notificationPreference));

        when (notificationRepository.save (any (Notification.class))).thenAnswer (invocation -> invocation.getArgument (0));


        NotificationRequest notificationRequest = NotificationRequest.builder()
                .customerId(customerId)
                .subject("subject")
                .body("body")
                .build();
        //when
        Notification result = notificationService.sendNotification (notificationRequest);

        //then
        assertEquals (NotificationStatus.SUCCEEDED, result.getStatus());
        assertEquals (customerId, result.getCustomerId());
        assertEquals ("subject", result.getSubject());


        verify (notificationRepository, times (1)).save (any (Notification.class));
        verify (mailSender, times (1)).send (any (SimpleMailMessage.class));

    }


    @Test
    void givenMailSenderThrowsException_whenSendNotification_thenStatusFailed(){

     //given
        UUID customerId = UUID.randomUUID ();

        NotificationPreference notificationPreference = NotificationPreference.builder()
                .customerId(customerId)
                .enabled(true)
                .contactInfo("test@example.com")
                .build();

        when (notificationPreferenceRepository.findByCustomerId (customerId)).thenReturn (Optional.of (notificationPreference));

        doThrow (new MailSendException ("SMTP error")).when (mailSender).send (any (SimpleMailMessage.class));
        when (notificationRepository.save (any (Notification.class))).thenAnswer (invocation -> invocation.getArgument (0));

        NotificationRequest notificationRequest = NotificationRequest.builder()
                .customerId(customerId)
                .subject("subject")
                .body("body")
                .build();

        Notification notification = notificationService.sendNotification (notificationRequest);

        assertEquals (NotificationStatus.FAILED, notification.getStatus());


        verify (mailSender, times (1)).send (any (SimpleMailMessage.class));
    }




    @Test
    void givenNotificationHistory_thenExpectSuccess(){

        UUID customerId = UUID.randomUUID ();
        NotificationPreference notificationPreference = NotificationPreference.builder()
                .customerId(customerId)
                .enabled(true)
                .contactInfo("test@example.com")
                .build();


        when (notificationRepository.findAllByCustomerIdAndDeletedIsFalseOrderByCreatedOnDesc (customerId)).thenReturn (List.of ());

        List<Notification> result = notificationService.getNotificationHistory (customerId);

        assertEquals (List.of (), result);
    }




    @Test
    void givenClearNotification_whenClearNotificationHistory_thenExpectSuccess(){

        UUID customerId = UUID.randomUUID ();
        Notification notification1 = Notification.builder()
                .id(UUID.randomUUID())
                .customerId(customerId)
                .deleted(false)
                .build();

        Notification notification2 = Notification.builder()
                .id(UUID.randomUUID())
                .customerId(customerId)
                .deleted(false)
                .build();


        List <Notification> notifications = List.of (notification1, notification2);

        when (notificationRepository.findAllByCustomerIdAndDeletedIsFalseOrderByCreatedOnDesc (customerId)).thenReturn (notifications);
        when (notificationRepository.save (any (Notification.class))).thenAnswer (invocation -> invocation.getArgument (0));

        //when
        notificationService.clearNotifications (customerId);

        //then
        assertTrue (notification1.isDeleted());
        assertTrue (notification2.isDeleted());

        verify (notificationRepository, times (2)).save (any (Notification.class));
        verify (notificationRepository, times (1)).findAllByCustomerIdAndDeletedIsFalseOrderByCreatedOnDesc (customerId);
    }



    @Test
    void givenNotExistRetryNotification_whenRetryNotificationFailed_thenExpectException(){

        UUID customerId = UUID.randomUUID ();
        NotificationPreference notificationPreference = NotificationPreference.builder()
                .customerId(customerId)
                .enabled(false)
                .contactInfo("test@example.com")
                .build();

        when (notificationPreferenceRepository.findByCustomerId (customerId)).thenReturn (Optional.of (notificationPreference));

        assertThrows(IllegalArgumentException.class,()-> notificationService.retryNotificationsFailed (customerId));
    }




    @Test
    void givenExistRetryNotification_whenRetryNotificationFailed_thenExpectSuccess(){

        UUID customerId = UUID.randomUUID ();
        NotificationPreference notificationPreference = NotificationPreference.builder()
                .customerId(customerId)
                .enabled(true)
                .contactInfo("test@example.com")
                .build();

        Notification failedNotification = Notification.builder()
                .id(UUID.randomUUID())
                .customerId(customerId)
                .subject("Test subject")
                .body("Test body")
                .status(NotificationStatus.FAILED)
                .deleted(false)
                .build();


        when (notificationPreferenceRepository.findByCustomerId (customerId)).thenReturn (Optional.of (notificationPreference));

        when (notificationRepository.findByCustomerIdAndStatus (customerId, NotificationStatus.FAILED)).thenReturn (List.of (failedNotification));

        when (notificationRepository.save (any (Notification.class))).thenAnswer (invocation -> invocation.getArgument (0));

        //when
        notificationService.retryNotificationsFailed (customerId);

        //then
        assertEquals (NotificationStatus.SUCCEEDED, failedNotification.getStatus());

        verify (notificationRepository, times (1)).findByCustomerIdAndStatus (customerId, NotificationStatus.FAILED);
        verify (notificationRepository, times (1)).save (any (Notification.class));
        verify (mailSender, times (1)).send (any (SimpleMailMessage.class));

    }


    @Test
    void givenMailSenderThrowsException_whenRetryNotificationsFailed_thenNotificationRemainsFailed(){
        //given
        UUID customerId = UUID.randomUUID ();
        NotificationPreference notificationPreference = NotificationPreference.builder()
                .customerId(customerId)
                .enabled(true)
                .contactInfo("test@example.com")
                .build();

        Notification failedNotification = Notification.builder()
                .id(UUID.randomUUID())
                .customerId(customerId)
                .subject("Test subject")
                .body("Test body")
                .status(NotificationStatus.FAILED)
                .deleted(false)
                .build();

        when (notificationPreferenceRepository.findByCustomerId (customerId)).thenReturn (Optional.of (notificationPreference));

        when (notificationRepository.findByCustomerIdAndStatus (customerId, NotificationStatus.FAILED)).thenReturn (List.of (failedNotification));

        doThrow (new MailSendException ("SMTP error")).when (mailSender).send (any (SimpleMailMessage.class));

        //when
        notificationService.retryNotificationsFailed (customerId);

        //then
        assertEquals (NotificationStatus.FAILED, failedNotification.getStatus());

        verify (notificationRepository, times (1)).findByCustomerIdAndStatus (customerId, NotificationStatus.FAILED);
        verify (notificationRepository, times (1)).save (any (Notification.class));
        verify (mailSender, times (1)).send (any (SimpleMailMessage.class));
    }
}

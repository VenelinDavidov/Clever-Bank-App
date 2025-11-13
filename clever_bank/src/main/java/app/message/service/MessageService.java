package app.message.service;

import app.message.model.Message;
import app.message.repository.MessageRepository;
import app.web.dto.SendMessageRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Slf4j
@Service
public class MessageService {

    private final MessageRepository messageRepository;



    @Autowired
    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }




    public void  saveMessageForService(SendMessageRequest sendMessageRequest){

        Message message = Message.builder ()
                .name (sendMessageRequest.getName ())
                .email (sendMessageRequest.getEmail ())
                .subject (sendMessageRequest.getSubject ())
                .message (sendMessageRequest.getMessage ())
                .build ();

        messageRepository.save (message);
    }



    private void   sendNotificationEmailFromMessage (Message message){
        log.info ("Notification email would be sent for message:" + message.getId ());
    }
}

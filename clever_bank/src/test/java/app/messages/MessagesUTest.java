package app.messages;

import app.message.model.Message;
import app.message.repository.MessageRepository;
import app.message.service.MessageService;
import app.web.dto.SendMessageRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.when;


@ExtendWith (MockitoExtension.class)
public class MessagesUTest {

    @InjectMocks
    private MessageService messageService;

    @Mock
    private MessageRepository messageRepository;

    @Test
    void givenRequestToThankYou_thenReturnThankYouView() throws Exception {

        SendMessageRequest message = new SendMessageRequest();
        message.setName("Venko");
        message.setEmail("venko@abv.bg");
        message.setSubject("Please Help me");
        message.setMessage("Hello");

        messageService.saveMessageForService (message);



    }
}

package app.web;

import app.cards.service.CardService;
import app.customer.service.CustomerService;
import app.message.service.MessageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


@WebMvcTest(IndexController.class)
public class IndexControllerAPITest {

    //Web Layer API Test

    @MockitoBean
    private CustomerService customerService;
    @MockitoBean
    private MessageService messageService;
    @MockitoBean
    private CardService cardService;


    @Autowired
    private MockMvc mockMvc;


   @Test
    void givenRequestToIndexPage_thenReturnIndexPage() throws Exception {

       MockHttpServletRequestBuilder request = get("/");
       mockMvc.perform(request)
               .andExpect(status().isOk())
               .andExpect(view().name("index"));
    }



}

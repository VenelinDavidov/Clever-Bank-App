package app.web;

import app.customer.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CustomerControllerApiTest.class)
public class CustomerControllerApiTest {


    @MockitoBean
    private CustomerService customerService;


    @Autowired
    private MockMvc mockMvc;

}

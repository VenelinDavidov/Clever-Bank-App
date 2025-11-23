package app;

import app.bills_utility.repository.BillRepository;
import app.bills_utility.service.BillService;
import app.customer.service.CustomerService;
import app.pocket.service.PocketService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
public class BillIntegrationTest {

    @Autowired
    private  BillRepository billRepository;
    @Autowired
    private  PocketService pocketService;
    @Autowired
    private  BillService billService;
    @Autowired
    private  CustomerService customerService;
}

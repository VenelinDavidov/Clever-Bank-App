package app.customer.service;

import app.cards.model.Cards;
import app.cards.service.CardService;
import app.customer.model.Customer;
import app.customer.model.Gender;
import app.customer.model.UserRole;
import app.customer.repository.CustomerRepository;
import app.exception.CustomerAlreadyExistException;
import app.exception.DomainException;
import app.security.AuthenticationMetadataDetails;
import app.subscription.model.Subscription;
import app.subscription.service.SubscriptionService;
import app.pocket.model.Pocket;
import app.pocket.service.PocketService;
import app.web.dto.CustomerEditRequest;
import app.web.dto.RegisterRequest;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class CustomerService implements UserDetailsService {


    private final static String CREATE_CUSTOMER_MESSAGE = "Successfully created customer %s with id %s";

    private final CustomerRepository customerRepository;
    private final PocketService pocketService;
    private final SubscriptionService subscriptionService;
    private final CardService cardService;
    private final PasswordEncoder passwordEncoder;




    @Autowired
    public CustomerService(CustomerRepository customerRepository,
                           PocketService pocketService,
                           SubscriptionService subscriptionService,
                           PasswordEncoder passwordEncoder,
                           CardService cardService) {
        this.customerRepository = customerRepository;
        this.pocketService = pocketService;
        this.subscriptionService = subscriptionService;
        this.passwordEncoder = passwordEncoder;
        this.cardService = cardService;
    }


    //Method register
    @Transactional
    public Customer register(RegisterRequest registerRequest) {

        Optional <Customer> optionalCustomer = customerRepository.findByUsername (registerRequest.getUsername ());

        if (optionalCustomer.isPresent ()) {
            throw new CustomerAlreadyExistException ("Customer with username [%s] already exist!".formatted (registerRequest.getUsername ()));
        }

        Customer customer = customerRepository.save (createNewCustomerAccount (registerRequest));

        Subscription defaultSubscription = subscriptionService.createDefaultSubscription (customer);
        customer.setSubscriptions (List.of (defaultSubscription));

        Pocket pocket = pocketService.createWallet (customer);
        customer.setWallets (List.of (pocket));

        Cards cards = cardService.createDefaultCard (customer);
        customer.setCards (List.of (cards));

        log.info (CREATE_CUSTOMER_MESSAGE.formatted (customer.getUsername (), customer.getId ()));

        return customer;
    }


    private Customer createNewCustomerAccount(RegisterRequest registerDto) {

        return Customer.builder ()
                .username (registerDto.getUsername ())
                .password (passwordEncoder.encode (registerDto.getPassword ()))
                .phoneNumber (registerDto.getPhoneNumber ())
                .country (registerDto.getCountry ())
                .isActive (true)
                .role (UserRole.USER)
                .gender (Gender.MALE)
                .createdOn (LocalDateTime.now ())
                .updatedOn (LocalDateTime.now ())
                .build ();
    }




    // Retrieve customer by id
    public Customer getById(UUID uuid) {

        return customerRepository.findById (uuid)
                .orElseThrow (() -> new DomainException ("Customer with id %s not found"
                        .formatted (uuid), HttpStatus.BAD_REQUEST));
    }




    // Retrieve all customers
    public List <Customer> getALLCustomers() {
        return customerRepository.findAll ();
    }




    //  everytime after login from user, spring security will call this method
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Customer customer = customerRepository.findByUsername (username)
                .orElseThrow (() -> new DomainException ("User with username=[%s] does not exist."
                        .formatted (username), HttpStatus.BAD_REQUEST));

        return new AuthenticationMetadataDetails (
                customer.getId (),
                customer.getUsername (),
                customer.getPassword (),
                customer.getRole (),
                customer.isActive ()
        );
    }





    // edit customer details by all information
    public void editCustomerDetails(UUID id, CustomerEditRequest customerEditRequest) {

        Customer customer = getById (id);

        customer.setFirstName (customerEditRequest.getFirstName ());
        customer.setLastName (customerEditRequest.getLastName ());
        customer.setProfilePicture (customerEditRequest.getProfilePicture ());
        customer.setPhoneNumber (customerEditRequest.getPhoneNumber ());
        customer.setEmail (customerEditRequest.getEmail ());
        customer.setAddress (customerEditRequest.getAddress ());

        customerRepository.save (customer);

    }


    public void switchCustomerStatus(UUID customerId) {

        Customer customer = getById (customerId);

        if (customer.isActive ()) {
            customer.setActive (false);
        } else {
            customer.setActive (true);
        }
        customerRepository.save (customer);
    }




    public void switchCustomerRole(UUID customerId) {

        Customer customer = getById (customerId);

        if (customer.getRole () == UserRole.USER){
            customer.setRole (UserRole.ADMIN);
        } else {
            customer.setRole (UserRole.USER);
        }

        customerRepository.save (customer);
    }
}



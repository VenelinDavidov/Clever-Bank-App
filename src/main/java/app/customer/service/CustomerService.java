package app.customer.service;

import app.customer.model.Customer;
import app.customer.model.Gender;
import app.customer.model.UserRole;
import app.customer.repository.CustomerRepository;
import app.exception.CustomerAlreadyExistException;
import app.exception.DomainException;
import app.security.AuthenticationMetadataDetails;
import app.subscription.model.Subscription;
import app.subscription.service.SubscriptionService;
import app.wallet.model.Wallet;
import app.wallet.service.WalletService;
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

    private final CustomerRepository customerRepository;
    private final WalletService walletService;
    private final SubscriptionService subscriptionService;
    private final PasswordEncoder passwordEncoder;


    @Autowired
    public CustomerService(CustomerRepository customerRepository,
                           WalletService walletService,
                           SubscriptionService subscriptionService,
                           PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.walletService = walletService;
        this.subscriptionService = subscriptionService;
        this.passwordEncoder = passwordEncoder;
    }




    //Method register
    @Transactional
    public Customer register(RegisterRequest registerRequest) {

        Optional <Customer> optionalCustomer = customerRepository.findByUsername (registerRequest.getUsername ());

        if (optionalCustomer.isPresent ()) {
            throw new CustomerAlreadyExistException ("Customer with username [%s] already exist!"
                    .formatted (registerRequest.getUsername ()));
        }

        Customer customer = customerRepository.save(createNewCustomerAccount (registerRequest));


        Subscription defaultSubscription = subscriptionService.createDefaultSubscription (customer);
        customer.setSubscriptions (List.of (defaultSubscription));

        Wallet wallet = walletService.createWallet (customer);
        customer.setWallets (List.of (wallet));



        log.info ("Successfully created customer %s with id %s".formatted (customer.getUsername (), customer.getId ()));

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

    return  customerRepository.findById (uuid)
                .orElseThrow (()-> new DomainException ("Customer with id %s not found"
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

}



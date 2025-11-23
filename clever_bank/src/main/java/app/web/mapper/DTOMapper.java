package app.web.mapper;

import app.customer.model.Customer;
import app.web.dto.CustomerEditRequest;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DTOMapper {

    public static CustomerEditRequest mapToCustomerEditRequest(Customer customer) {

        return CustomerEditRequest.builder ()
                .firstName (customer.getFirstName ())
                .lastName (customer.getLastName ())
                .profilePicture (customer.getProfilePicture ())
                .phoneNumber (customer.getPhoneNumber ())
                .email (customer.getEmail ())
                .address (customer.getAddress ())
                .build ();
    }
}
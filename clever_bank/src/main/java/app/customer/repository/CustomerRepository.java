package app.customer.repository;

import app.customer.model.Customer;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
@Repository
public interface CustomerRepository extends JpaRepository <Customer, UUID> {

    @Fetch(FetchMode.JOIN)
    Optional <Customer> findByUsername(String username);
}

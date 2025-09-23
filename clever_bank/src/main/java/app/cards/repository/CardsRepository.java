package app.cards.repository;

import app.cards.model.Cards;
import app.customer.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CardsRepository extends JpaRepository <Cards, UUID> {


    List<Cards> findAllByCustomerId(UUID customerId);

    int countByCustomer(Customer customer);


}

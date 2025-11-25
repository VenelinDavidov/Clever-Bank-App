package app.card;

import app.cards.model.Cards;
import app.cards.repository.CardsRepository;
import app.cards.service.CardService;
import app.customer.model.Customer;
import app.customer.service.CustomerService;
import app.exception.CardLimitExceededException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith (MockitoExtension.class)
public class CardServerUTest {

    @Mock
    private  CardsRepository cardsRepository;

    @Mock
    private Customer customer;

    @InjectMocks
    private CardService cardsService;



    @Test
    void createSecondaryCard_whenMaxLimitReached_shouldThrowException() {

        UUID customerId = UUID.randomUUID ();

        Customer customer = Customer.builder ()
                .id (customerId)
                .build ();
        Cards cards = new Cards ();
        cards.setCustomer (customer);

        CardService spy = spy (cardsService);
        doReturn (true).when (spy).hasReachedMaxCardLimit (customer);

        CardLimitExceededException exception = assertThrows (CardLimitExceededException.class, () -> spy.createSecondaryCard (customer));

        assertTrue (exception.getMessage ().contains ("Card limit has been reached for this customer."));

        verify (cardsRepository, never ()).save (any (Cards.class));

    }


    @Test
    void createSecondaryCard_whenUnderLimit_shouldSaveCard(){
        UUID customerId = UUID.randomUUID ();

        Customer customer = Customer.builder ()
                .id (customerId)
                .build ();
        Cards cards = new Cards ();
        cards.setCustomer (customer);

        CardService spy = spy (cardsService);
        doReturn (false).when (spy).hasReachedMaxCardLimit (customer);

        spy.createSecondaryCard (customer);

        verify (cardsRepository, times (1)).save (any (Cards.class));

    }


    @Test
    void switchStatusCard_whenCardIsInactive_shouldActivateCard(){

        UUID customerId = UUID.randomUUID ();
        UUID cardId = UUID.randomUUID();

        Customer customer = Customer.builder ()
                .id (customerId)
                .build ();

        Cards cards = new Cards ();
        cards.setId (cardId);
        cards.setCustomer (customer);
        cards.setActive (false);

        when (cardsRepository.findById (any (UUID.class))).thenReturn (Optional.of (cards));

        cardsService.switchStatusCard (cards.getId ());

        assertTrue (cards.isActive ());

        verify (cardsRepository, times (1)).save (any (Cards.class));

    }


    @Test
    void switchStatusCard_whenCardActive_shouldInActivateCard(){

        UUID customerId = UUID.randomUUID ();
        UUID cardId = UUID.randomUUID();

        Customer customer = Customer.builder ()
                .id (customerId)
                .build ();

        Cards cards = new Cards ();
        cards.setId (cardId);
        cards.setCustomer (customer);
        cards.setActive (true);

        when (cardsRepository.findById (any (UUID.class))).thenReturn (Optional.of (cards));

        cardsService.switchStatusCard (cards.getId ());

         assertFalse (cards.isActive ());

        verify (cardsRepository, times (1)).save (any (Cards.class));

    }


    @Test
    void deleteCard_whenCardExists_shouldDeleteCard(){

        UUID cardId = UUID.randomUUID();

        Cards cards = new Cards ();
        cards.setId (cardId);


        cardsService.deleteCard (cards.getId ());

        verify (cardsRepository, times (1)).deleteById (cardId);

    }


    @Test
    void allCardsByCustomer_whenCustomerExists_shouldReturnListCards(){

        UUID customerId = UUID.randomUUID ();

        Customer customer = Customer.builder ()
                .id (customerId)
                .build ();

        Cards cards = new Cards ();
        cards.setCustomer (customer);

        when (cardsRepository.findAllByCustomerId (customerId)).thenReturn (List.of (cards));

        List <Cards> result = cardsService.getAllCardsByCustomerId (customerId);

        assertEquals (List.of (cards), result);

        verify (cardsRepository, times (1)).findAllByCustomerId (customerId);

    }

}

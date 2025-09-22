package app.cards.service;

import app.cards.model.CardBrand;
import app.cards.model.CardLevel;
import app.cards.model.CardPeriod;
import app.cards.model.Cards;
import app.cards.repository.CardsRepository;
import app.customer.model.Customer;
import app.exception.DomainException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;


@Slf4j
@Service
public class CardService {


    public static final int  NEW_CARD_LIMIT = 1_000;
    private static final String CREATE_CARD_MESSAGE = "Successfully created card %s with id %s";
    private final CardsRepository cardsRepository;



    @Autowired
    public CardService(CardsRepository cardsRepository) {
        this.cardsRepository = cardsRepository;
    }


    public Cards createDefaultCard(Customer customer) {

        Cards card = cardsRepository.save (createNewCard (customer));
          log.info (CREATE_CARD_MESSAGE.formatted (card.getCardNumber (), card.getId ()));

        return card;
    }

    private Cards createNewCard(Customer customer) {

        LocalDateTime now = LocalDateTime.now();
        long randomCardNumber = 100000000000L + new Random ().nextInt(900000000);

        return  Cards.builder()
                .customer (customer)
                .cardNumber (String.valueOf (randomCardNumber))
                .cardBrand (CardBrand.Visa)
                .cardLevel (CardLevel.Silver)
                .isActive (true)
                .period (CardPeriod.SIX_MONTHLY)
                .totalLimit (NEW_CARD_LIMIT )
                .updateAllowed (true)
                .completedOn (now.plusMonths (6))
                .createdOn (now)
                .build ();

    }

 // Get all cards by customer id
    public List <Cards> getAllCardsByCustomerId(UUID customerId) {
        return cardsRepository.findAllByCustomerId (customerId);
    }


  // Count cards by customer
    public int countCardsByCustomer(Customer customer) {
        return cardsRepository.countByCustomer (customer);
    }

    // Delete card by id
    public void deleteCard(UUID cardId) {
        cardsRepository.deleteById (cardId);
    }

    // Get card by id
    public Cards getCardById(UUID cardId) {
        return cardsRepository.findById (cardId)
                .orElseThrow (() -> new DomainException ("Card with id %s not found".formatted (cardId), HttpStatus.BAD_REQUEST));
    }
}

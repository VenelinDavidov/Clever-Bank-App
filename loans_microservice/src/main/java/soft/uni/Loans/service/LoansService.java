package soft.uni.Loans.service;


import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import soft.uni.Loans.model.LoanStatus;
import soft.uni.Loans.model.Loans;
import soft.uni.Loans.repository.LoansRepository;
import soft.uni.Loans.web.dto.LoanRequest;
import soft.uni.Loans.web.dto.LoanResponse;
import soft.uni.Loans.web.mapper.LoansDtoMapper;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Slf4j
@Service
public class LoansService {

    private static final Logger logger = LoggerFactory.getLogger (LoansService.class);

    private final LoansRepository loansRepository;


    @Autowired
    public LoansService(LoansRepository loansRepository) {
        this.loansRepository = loansRepository;
    }


    @Transactional
    public LoanResponse createLoan(@Valid LoanRequest loanRequest) {

        log.info("Creating loan for customer: {} {}",loanRequest.getFirstName(), loanRequest.getLastName());

        Loans loan = Loans.builder()
                .customerId (loanRequest.getCustomerId())
                .firstName(loanRequest.getFirstName())
                .lastName(loanRequest.getLastName())
                .loanType(loanRequest.getLoanType())
                .amount(loanRequest.getAmount())
                .loanStatus (LoanStatus.PENDING)
                .build();

        Loans savedLoan = loansRepository.save (loan);
        log.info ("Loan created successfully with ID:  {}",savedLoan.getLoanId());
        return LoansDtoMapper.mapToResponse (savedLoan);
    }


    @Transactional
    public List <LoanResponse> getLoansByCustomerId(UUID customerId) {
        log.info("Fetching loans for customer ID: {}", customerId);
        List <Loans> loans = loansRepository.findByCustomerId (customerId);
          return loans
                .stream ()
                .map (LoansDtoMapper::mapToResponse)
                .collect (Collectors.toList ());
    }
}






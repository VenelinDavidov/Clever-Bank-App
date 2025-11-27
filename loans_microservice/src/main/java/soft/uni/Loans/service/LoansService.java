package soft.uni.Loans.service;


import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import soft.uni.Loans.exception.ResourceNotFoundException;
import soft.uni.Loans.model.LoanStatus;
import soft.uni.Loans.model.Loans;
import soft.uni.Loans.repository.LoansRepository;
import soft.uni.Loans.web.dto.LoanRequest;
import soft.uni.Loans.web.dto.LoanResponse;
import soft.uni.Loans.web.mapper.LoansDtoMapper;

import java.time.LocalDateTime;
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



    public LoanResponse createLoan(@Valid LoanRequest loanRequest) {

        logger.info("Creating loan for customer: {} {}",loanRequest.getFirstName(), loanRequest.getLastName());

        Loans loan = Loans.builder()
                .customerId (loanRequest.getCustomerId())
                .firstName(loanRequest.getFirstName())
                .lastName(loanRequest.getLastName())
                .loanType(loanRequest.getLoanType())
                .amount(loanRequest.getAmount())
                .loanStatus (LoanStatus.PENDING)
                .build();

        Loans savedLoan = loansRepository.save (loan);
        logger.info ("Loan created successfully with ID:  {}",savedLoan.getLoanId());
        return LoansDtoMapper.mapToResponse (savedLoan);
    }



    public List <LoanResponse> getLoansByCustomerId(UUID customerId) {

        logger.info("Fetching loans for customer ID: {}", customerId);
        List <Loans> loans = loansRepository.findByCustomerId (customerId);
          return loans
                .stream ()
                .map (LoansDtoMapper::mapToResponse)
                .collect (Collectors.toList ());
    }



    public LoanResponse getLoanById(UUID loanId) {

        logger.info ("Fetching loan by ID: {}", loanId);
        Loans loans = loansRepository.findById (loanId)
                                     .orElseThrow (() -> new ResourceNotFoundException ("Loan not fount with ID: " + loanId));
         return LoansDtoMapper.mapToResponse (loans);
    }




    public LoanResponse updateLoan(UUID loanId, LoanRequest loanRequest) {

        logger.info ("Updating loan with ID: {}", loanId);
        Loans loans = loansRepository
                                     .findById (loanId)
                                     .orElseThrow (() -> new ResourceNotFoundException ("Loan not found with ID: " + loanId));

        loans.setFirstName (loanRequest.getFirstName ());
        loans.setLastName (loanRequest.getLastName ());
        loans.setLoanType (loanRequest.getLoanType ());
        loans.setAmount (loanRequest.getAmount ());
        loans.setUpdatedOn (LocalDateTime.now ());

        Loans updateLoan = loansRepository.save (loans);
        logger.info ("Loan update successfully with ID: {}", updateLoan.getLoanId());

        return LoansDtoMapper.mapToResponse (updateLoan);
    }



    public void deleteLoan(UUID loanId) {

        logger.info ("Deleting loan with ID: {}", loanId);
        Loans loans = loansRepository.findById (loanId)
                                     .orElseThrow (() -> new ResourceNotFoundException ("Loan nod fount with ID: " + loanId));
        loansRepository.delete (loans);
        log.info ("Loan deleted successfully with ID: {}", loanId);
    }





    public LoanResponse updateLoanStatus(UUID loanId,  LoanStatus status) {

        logger.info ("Updating loan status for ID: {} to {}", loanId, status);

        Loans loans = loansRepository.findById (loanId)
                                      .orElseThrow (() -> new ResourceNotFoundException ("Loan not found with ID: " + loanId));
        loans.setLoanStatus (status);
        loans.setUpdatedOn (LocalDateTime.now ());

        loansRepository.save (loans);
        logger.info ("Loan status updated successfully with ID: {}", loanId);

        return LoansDtoMapper.mapToResponse (loans);
    }
}






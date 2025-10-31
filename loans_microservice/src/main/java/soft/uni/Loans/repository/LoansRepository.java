package soft.uni.Loans.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import soft.uni.Loans.model.Loans;


import java.util.List;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LoansRepository extends JpaRepository <Loans, UUID> {


    List <Loans> findByCustomerId(UUID customerId);


    Optional <Loans> findByLoanIdAndCustomerId(UUID loanId, UUID customerId);
}

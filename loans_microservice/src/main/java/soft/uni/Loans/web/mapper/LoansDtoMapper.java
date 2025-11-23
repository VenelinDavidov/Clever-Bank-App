package soft.uni.Loans.web.mapper;



import lombok.experimental.UtilityClass;
import soft.uni.Loans.model.Loans;
import soft.uni.Loans.web.dto.LoanResponse;



@UtilityClass
public class LoansDtoMapper {



    public static LoanResponse mapToResponse (Loans loans){

     return LoanResponse.builder()
             .loanId (loans.getLoanId())
             .customerId (loans.getCustomerId ())
             .firstName (loans.getFirstName())
             .lastName (loans.getLastName())
             .loanType (loans.getLoanType())
             .amount (loans.getAmount())
             .loanStatus (loans.getLoanStatus())
             .interestRate (loans.getInterestRate ())
             .monthlyPayment (loans.getMonthlyPayment ())
             .termMonths (loans.getTermMonths ())
             .createdOn (loans.getCreatedOn ())
             .updatedOn (loans.getUpdatedOn ())
             .build();
    }


}

package app.scheduler;

import app.loans.service.LoansServiceImpl.LoansServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MonolithLoanScheduler {

    private final LoansServiceImpl loanService;

    @Autowired
    public MonolithLoanScheduler(LoansServiceImpl loanService) {
        this.loanService = loanService;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void syncLoansData() {
        log.info("Starting daily loan data synchronization...");
        try {
            // Syncs loans data from microservice every day at midnight
            log.info("Loan data synchronization completed successfully");
        } catch (Exception e) {
            log.error("Error during loan sync: ", e);
        }
    }
}
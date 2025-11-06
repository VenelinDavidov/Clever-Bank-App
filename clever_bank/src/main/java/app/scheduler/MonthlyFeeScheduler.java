package app.scheduler;


import app.pocket.service.PocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MonthlyFeeScheduler {

    private final PocketService pocketService;

    @Autowired
    public MonthlyFeeScheduler(PocketService pocketService) {
        this.pocketService = pocketService;
    }

//    @Scheduled(cron = "*/20 * * * * *")
    @Scheduled(cron = "0 1 0 1 * *")
    public void chargeMonthlyFees() {
     pocketService.applyMonthlyFees();
    }
}

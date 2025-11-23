package soft.uni.Loans.model;

public enum LoanStatus {

    PENDING("Pending"),
    APPROVED("Approved"),
    REJECTED("Rejected"),
    ACTIVE("Active"),
    COMPLETED("Completed"),
    CANCELLED("Cenceled");

    LoanStatus(String displayName) {
        this.displayName = displayName;
    }

    private final String displayName;

    public String getDisplayName() {
        return displayName;
    }
}

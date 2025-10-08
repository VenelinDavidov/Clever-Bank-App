package app.pocket.model;

public enum PocketType {

    SAVINGS("Savings"),
    BUSINESS("Business");


    private String displayName;

    PocketType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

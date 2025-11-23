package app.pocket.model;

public enum PocketStatus {

    ACTIVE ("Active"),
    INACTIVE ("Inactive");

    private String displayName;

    PocketStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

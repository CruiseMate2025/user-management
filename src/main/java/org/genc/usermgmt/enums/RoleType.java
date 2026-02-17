package org.genc.usermgmt.enums;

public enum RoleType {
    ADMIN("Administrator Role"),
    PASSENGER("Passenger of cruise mate"),
    CREW("crew of cruise mate");


    private final String description;

    RoleType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return this.name();
    }
}

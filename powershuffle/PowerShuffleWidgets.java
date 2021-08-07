package org.texastorque.torquelib.powershuffle;

public enum PowerShuffleWidgets {
    // These names should match up with TYPE_NAME
    PIDManager("PIDManager");

    private final String identifier;

    PowerShuffleWidgets(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }
}

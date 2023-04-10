package josscoder.easteregghunt.mongodb.indentifier;

public enum Fields {
    XUID,
    EASTER_EGGS_FOUND,
    CLAIMED;

    public String id() {
        return name().toLowerCase();
    }
}

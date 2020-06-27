package pl.dawid.kaszyca.config;

public enum StateEnum {

    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE"),
    ALL("ALL");

    String value;

    StateEnum(String type) {
        value = type;
    }
}

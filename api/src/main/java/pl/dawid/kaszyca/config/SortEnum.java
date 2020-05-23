package pl.dawid.kaszyca.config;

public enum SortEnum {

    ASC("ASC"),
    DESC("DESC"),
    NONE("NONE");

    String value;

    SortEnum(String type) {
        value = type;
    }
}

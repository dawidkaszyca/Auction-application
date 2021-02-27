package pl.dawid.kaszyca.config;

public enum DecisionConfig {

    APPROVE("APPROVE"),
    CANCEL("CANCEL");

    String value;

    DecisionConfig(String type) {
        value = type;
    }
}

package pl.dawid.kaszyca.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "jwt.app")
@Getter
@Setter
public class JsonWebTokenProperties {

    String secretKey;

    String base64Secret;

    int tokenValidityInSeconds;

    int tokenValidityInSecondsForRememberMe;
}


package pl.dawid.kaszyca.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class UserDTO {

    public static final int PASSWORD_MIN_LENGTH = 4;

    public static final int PASSWORD_MAX_LENGTH = 100;

    private Long id;

    private String login;

    private String firstName;

    private String lastName;

    private String email;

    private Set<String> authorities;

    @JsonIgnore
    private boolean activated = false;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
}

package pl.dawid.kaszyca.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Pattern;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AuctionDTO extends AuctionBaseDTO {

    private String description;

    @Pattern(regexp = "[0-9]{9}")
    private String phone;

    private Long userId;

    private String userFirstName;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<CategoryAttributesDTO> attributes;
}

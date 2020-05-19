package pl.dawid.kaszyca.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Pattern;

@Getter
@Setter
@NoArgsConstructor
public class AuctionDTO extends AuctionBaseDTO {

    private String description;

    @Pattern(regexp = "[0-9]{9}")
    private String phone;
}

package pl.dawid.kaszyca.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.dawid.kaszyca.model.auction.AuctionDetails;

import javax.validation.constraints.Pattern;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AuctionDTO extends AuctionBaseDTO {

    @JsonIgnore
    private List<AuctionDetails> auctionDetails;

    private String description;

    @Pattern(regexp = "[0-9]{9}")
    private String phone;
}

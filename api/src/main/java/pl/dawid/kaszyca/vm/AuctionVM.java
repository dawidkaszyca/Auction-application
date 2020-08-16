package pl.dawid.kaszyca.vm;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.dawid.kaszyca.dto.AuctionBaseDTO;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AuctionVM {

    List<AuctionBaseDTO> auctionListBase;

    Long numberOfAuctionByProvidedFilters;
}

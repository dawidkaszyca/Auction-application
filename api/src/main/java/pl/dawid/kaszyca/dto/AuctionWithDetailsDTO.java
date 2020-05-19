package pl.dawid.kaszyca.dto;

import lombok.Data;

import java.util.List;

@Data
public class AuctionWithDetailsDTO extends AuctionDTO {

    private List<AuctionDetailsDTO> auctionDetails;
}

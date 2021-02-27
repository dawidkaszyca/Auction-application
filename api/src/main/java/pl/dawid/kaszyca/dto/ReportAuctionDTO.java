package pl.dawid.kaszyca.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportAuctionDTO {

    private Long id;

    private Long auctionId;

    private String email;

    private String reason;

    private String description;

    private String substantiation;

    private String decision;
}

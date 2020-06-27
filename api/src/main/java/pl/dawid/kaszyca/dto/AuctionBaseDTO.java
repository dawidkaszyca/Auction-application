package pl.dawid.kaszyca.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
public class AuctionBaseDTO {

    private Long id;

    private String price;

    private String category;

    private CityDTO city;

    private String condition;

    private String title;

    private Instant createdDate;

    private Instant expiredDate;

    private int viewers;


}

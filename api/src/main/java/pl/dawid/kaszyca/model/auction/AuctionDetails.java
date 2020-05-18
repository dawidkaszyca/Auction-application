package pl.dawid.kaszyca.model.auction;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
public class AuctionDetails implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Auction auction;

    private String categoryAttributes;

    private String attributeValues;
}

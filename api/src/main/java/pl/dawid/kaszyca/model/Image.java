package pl.dawid.kaszyca.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import pl.dawid.kaszyca.model.auction.Auction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Data
@NoArgsConstructor
public class Image implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @NotNull
    private Attachment attachment;

    @ManyToOne
    private Auction auction;

    @NotNull
    private Boolean isMainAuctionPhoto = false;

}

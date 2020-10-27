package pl.dawid.kaszyca.model.auction;

import lombok.Data;
import pl.dawid.kaszyca.model.User;
import pl.dawid.kaszyca.model.auction.Auction;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
public class ReportedAuction implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Auction auction;

    @ManyToOne
    private User user;

    private String description;
}

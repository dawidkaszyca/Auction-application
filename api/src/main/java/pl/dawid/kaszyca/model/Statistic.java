package pl.dawid.kaszyca.model;

import lombok.Data;
import pl.dawid.kaszyca.model.auction.Auction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Data
public class Statistic implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String enumKey;

    @Temporal(TemporalType.DATE)
    private Date date;

    private Long value;

    @ManyToOne
    private Auction auction;
}

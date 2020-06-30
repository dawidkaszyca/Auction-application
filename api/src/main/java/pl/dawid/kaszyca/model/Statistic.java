package pl.dawid.kaszyca.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import pl.dawid.kaszyca.config.StatisticKeyEnum;
import pl.dawid.kaszyca.model.auction.Auction;

import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
public class Statistic implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private StatisticKeyEnum key;

    @Temporal(TemporalType.DATE)
    @NotNull
    private Date day;

    @NotNull
    private int value;

    @ManyToOne
    @Nullable
    private Auction auction;

}

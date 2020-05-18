package pl.dawid.kaszyca.model.auction;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.annotations.Cascade;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.lang.NonNull;
import pl.dawid.kaszyca.model.*;

import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Auction implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = {CascadeType.MERGE})
    @NonNull
    private Category category;

    @ManyToOne(cascade = {CascadeType.ALL})
    @NonNull
    private City city;

    @Column(name = "condition_name")
    private String condition;

    @ManyToOne
    @NonNull
    private User user;

    @OneToMany(mappedBy = "auction", cascade = {CascadeType.ALL})
    @Nullable
    private List<AuctionDetails> auctionDetails;

    @Column(precision = 8, scale = 2)
    @Nullable
    private float price;

    @NonNull
    private String title;

    @NonNull
    private String description;

    @Pattern(regexp = "[0-9]{9}")
    @NonNull
    private String phone;

    @OneToMany(mappedBy = "id", cascade = {CascadeType.ALL})
    @Nullable
    private List<Image> images;

    private int viewers;

    @CreatedDate
    @Column(name = "created_date", updatable = false)
    private Instant createdDate = Instant.now();

    @CreatedDate
    @Column(name = "expired_date")
    private Instant expiredDate = getCurrentDatePlusOneMonth();

    private Instant getCurrentDatePlusOneMonth() {
        return DateUtils.addMonths(new Date(), 1).toInstant();
    }

}

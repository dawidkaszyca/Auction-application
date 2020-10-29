package pl.dawid.kaszyca.model.auction;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Data
public class ReportedAuction implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @NotNull
    private Auction auction;

    @Email
    @NotNull
    private String email;

    @NotNull
    private String reason;

    @NotNull
    private String description;

    private String decision;

    private boolean active = true;
}

package pl.dawid.kaszyca.model.auction;

import lombok.Data;
import pl.dawid.kaszyca.model.User;

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

    private String substantiation;

    @ManyToOne
    private User decisivePerson;

    private boolean active = true;
}

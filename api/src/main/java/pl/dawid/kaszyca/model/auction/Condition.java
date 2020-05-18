package pl.dawid.kaszyca.model.auction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "auction_condition")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Condition implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", unique = true, columnDefinition = "VARCHAR(64)")
    private String condition;
}

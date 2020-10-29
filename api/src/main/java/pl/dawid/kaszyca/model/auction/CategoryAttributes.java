package pl.dawid.kaszyca.model.auction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NonNull;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Data
public class CategoryAttributes implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(columnDefinition = "VARCHAR(64)")
    private String attribute;

    @OneToMany(mappedBy = "categoryAttributes", cascade = {CascadeType.ALL})
    private List<AttributeValues> attributeValues;

    @ManyToOne
    @JsonIgnore
    private Category category;

    @NonNull
    private Boolean isSingleSelect = true;
}

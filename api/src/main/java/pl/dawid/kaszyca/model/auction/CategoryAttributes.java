package pl.dawid.kaszyca.model.auction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Data
@Getter
@Setter
public class CategoryAttributes implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(unique = true, columnDefinition = "VARCHAR(64)")
    private String id;

    @OneToMany(mappedBy = "categoryAttributes", cascade ={CascadeType.ALL})
    private List<AttributeValues> attributeValues;

    @ManyToOne
    @JsonIgnore
    private Category category;

    @NonNull
    private Boolean isSingleSelect = true;
}

package pl.dawid.kaszyca.model.auction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;
import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Category implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "name", unique = true, columnDefinition = "VARCHAR(64)")
    private String category;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    @Nullable
    private List<CategoryAttributes> categoryAttributes;

    public Category(String category) {
        this.category = category;
    }
}

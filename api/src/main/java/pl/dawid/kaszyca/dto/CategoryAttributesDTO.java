package pl.dawid.kaszyca.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CategoryAttributesDTO {

    private String attribute;

    private List<AttributeValuesDTO> attributeValues;

    @JsonIgnore
    private CategoryDTO category;

    private Boolean isSingleSelect = true;
}

package pl.dawid.kaszyca.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AttributeValuesDTO {

    private String id;

    private String value;

    @JsonIgnore
    private CategoryAttributesDTO categoryAttributes;

}

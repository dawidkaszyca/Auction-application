package pl.dawid.kaszyca.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CategoryDTO {

    private String name;

    private List<CategoryAttributesDTO> categoryAttributes;

}

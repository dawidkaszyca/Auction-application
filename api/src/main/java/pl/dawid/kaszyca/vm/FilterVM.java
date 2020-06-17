package pl.dawid.kaszyca.vm;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.dawid.kaszyca.config.SortEnum;
import pl.dawid.kaszyca.dto.CategoryAttributesDTO;
import pl.dawid.kaszyca.dto.CityDTO;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class FilterVM {

    String category;

    String sortByFieldName;

    Integer pageSize;

    Integer page;

    SortEnum sort;

    Long userId;

    /**
     * Map for filters fields single select attributes
     * key - filter field name
     * value - list of filters value (OR)
     */
    List<CategoryAttributesDTO> filters;

    List<String> searchWords;

    CityDTO city;

    String condition;

    boolean priceFilter;

    Float minPrice;

    Float maxPrice;

    int kilometers;

}

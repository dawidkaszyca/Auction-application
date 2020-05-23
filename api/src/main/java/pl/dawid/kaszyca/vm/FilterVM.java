package pl.dawid.kaszyca.vm;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.dawid.kaszyca.config.SortEnum;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class FilterVM {

    String sortByFieldName;

    Integer pageSize;

    Integer page;

    SortEnum sort;

    /**
     * Map for filters fields single select attributes
     * key - filter field name
     * value - list of filters value (OR)
     */
    Map<String, List<String>> filters;

    List<String> searchWords;

    String city;

    String condition;

    boolean priceFilter;

    Float minPrice;

    Float maxPrice;
}

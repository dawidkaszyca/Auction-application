package pl.dawid.kaszyca.util;

import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import pl.dawid.kaszyca.dto.AttributeValuesDTO;
import pl.dawid.kaszyca.dto.CategoryAttributesDTO;
import pl.dawid.kaszyca.dto.CategoryDTO;
import pl.dawid.kaszyca.model.auction.AttributeValues;
import pl.dawid.kaszyca.model.auction.Category;
import pl.dawid.kaszyca.model.auction.CategoryAttributes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
@NoArgsConstructor
public class MapperUtils {

    private static ModelMapper modelMapper = new ModelMapper();

    /**
     * <p>Note: outClass object must have default constructor with no arguments</p>
     *
     * @param <D>      type of result object.
     * @param <T>      type of source object to map from.
     * @param entity   entity that needs to be mapped.
     * @param outClass class of result object.
     * @return new object of <code>outClass</code> type.
     */
    public static <D, T> D map(final T entity, Class<D> outClass) {
        return modelMapper.map(entity, outClass);
    }

    /**
     * <p>Note: outClass object must have default constructor with no arguments</p>
     *
     * @param entityList list of entities that needs to be mapped
     * @param outCLass   class of result list element
     * @param <D>        type of objects in result list
     * @param <T>        type of entity in <code>entityList</code>
     * @return list of mapped object with <code><D></code> type.
     */
    public static <D, T> List<D> mapAll(final Collection<T> entityList, Class<D> outCLass) {
        return entityList.stream()
                .map(entity -> map(entity, outCLass))
                .collect(Collectors.toList());
    }

    /**
     * Maps {@code source} to {@code destination}.
     *
     * @param source      object to map from
     * @param destination object to map to
     */
    public static <S, D> D map(final S source, D destination) {
        modelMapper.map(source, destination);
        return destination;
    }

    public static CategoryDTO mapCategoryToDTO(Category category) {
        CategoryDTO categoryDTO = map(category, CategoryDTO.class);
        List<CategoryAttributesDTO> categoryAttributesDTOS = new ArrayList<>();
        for (CategoryAttributes categoryAttributes : category.getCategoryAttributes()) {
            CategoryAttributesDTO categoryAttributesDTO = map(categoryAttributes, CategoryAttributesDTO.class);
            List<AttributeValuesDTO> attributeValuesDTOS = mapAll(categoryAttributes.getAttributeValues(), AttributeValuesDTO.class);
            categoryAttributesDTO.setAttributeValues(attributeValuesDTOS);
            categoryAttributesDTOS.add(categoryAttributesDTO);
        }
        categoryDTO.setCategoryAttributes(categoryAttributesDTOS);
        return categoryDTO;
    }

    public static Category mapDTOTOCategory(CategoryDTO categoryDTO) {
        Category category = map(categoryDTO, Category.class);
        List<CategoryAttributes> categoryAttributesList = new ArrayList<>();
        for (CategoryAttributesDTO categoryAttributesDTO : categoryDTO.getCategoryAttributes()) {
            CategoryAttributes categoryAttributes = map(categoryAttributesDTO, CategoryAttributes.class);
            List<AttributeValues> attributeValues = mapAll(categoryAttributes.getAttributeValues(), AttributeValues.class);
            categoryAttributes.setAttributeValues(attributeValues);
            categoryAttributesList.add(categoryAttributes);
        }
        category.setCategoryAttributes(categoryAttributesList);
        return category;
    }
}

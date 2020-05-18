package pl.dawid.kaszyca;


import org.junit.Assert;
import org.junit.jupiter.api.Test;
import pl.dawid.kaszyca.dto.CategoryDTO;
import pl.dawid.kaszyca.model.auction.AttributeValues;
import pl.dawid.kaszyca.model.auction.Category;
import pl.dawid.kaszyca.model.auction.CategoryAttributes;
import pl.dawid.kaszyca.util.MapperUtils;

import java.util.ArrayList;
import java.util.List;

public class MapperUtilsTest {

    private Category createCategory() {
        Category category = new Category();
        category.setCategory("category1");
        category.setCategoryAttributes(createCategoryAttributes(category));
        return category;
    }

    private List<CategoryAttributes> createCategoryAttributes(Category category) {
        List<CategoryAttributes> categoryAttributesList = new ArrayList();
        for(int i=0; i<2 ; i++) {
            CategoryAttributes categoryAttributes = new CategoryAttributes();
            categoryAttributes.setId("attribute" + i);
            categoryAttributes.setCategory(category);
            categoryAttributes.setAttributeValues(createAttributeValues(categoryAttributes));
            categoryAttributesList.add(categoryAttributes);
        }
        return categoryAttributesList;
    }

    private List<AttributeValues> createAttributeValues(CategoryAttributes categoryAttributes) {
        List<AttributeValues> attributeValuesList = new ArrayList<>();
        for(int i=0; i<3 ; i++) {
            AttributeValues attributeValues = new AttributeValues();
            attributeValues.setValue("values" + i);
            //attributeValues.setCategoryAttributes(categoryAttributes);
            attributeValuesList.add(attributeValues);
        }
        return attributeValuesList;
    }

    @Test
    public void mapCategoryToDTO() {
        Category category = createCategory();
        CategoryDTO categoryDTO = MapperUtils.map(category, CategoryDTO.class);
        Category category1 = MapperUtils.map(categoryDTO, Category.class);
        String expected = category.getCategoryAttributes().get(0).getId();
        String actual = category1.getCategoryAttributes().get(0).getId();
        Assert.assertEquals(expected, actual);
        expected = category.getCategoryAttributes().get(1).getId();
        actual = category1.getCategoryAttributes().get(1).getId();
        Assert.assertEquals(expected, actual);
    }
}

package pl.dawid.kaszyca.service;

import org.springframework.stereotype.Service;
import pl.dawid.kaszyca.dto.CategoryDTO;
import pl.dawid.kaszyca.model.auction.Category;
import pl.dawid.kaszyca.repository.CategoryRepository;
import pl.dawid.kaszyca.util.MapperUtils;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<String> getCategoriesName() {
        return categoryRepository.getCategories();
    }

    public void saveCategory(CategoryDTO categoryDTO) {
        Category category = MapperUtils.map(categoryDTO, Category.class);
        categoryRepository.save(category);
    }

    public CategoryDTO getCategoryDTOById(String id) {
        Optional<Category> category = categoryRepository.findFirstByCategory(id);
        return category.map(value -> MapperUtils.map(value, CategoryDTO.class)).orElse(null);
    }

    public Category getCategoryById(String id) {
        Optional<Category> category = categoryRepository.findFirstByCategory(id);
        return category.orElse(null);
    }

    public void updateCategoryById(CategoryDTO categoryDTO, String name) {
        Category category = MapperUtils.map(categoryDTO, Category.class);
        category.setCategory(name);
        categoryRepository.save(category);
    }
}

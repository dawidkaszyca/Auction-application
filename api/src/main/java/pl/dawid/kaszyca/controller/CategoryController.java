package pl.dawid.kaszyca.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.dawid.kaszyca.dto.CategoryDTO;
import pl.dawid.kaszyca.service.CategoryService;

import java.util.List;

@RestController
@CrossOrigin
@Slf4j
@RequestMapping("api")
public class CategoryController {

    CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/categories")
    public ResponseEntity getAllCategoryName() {
        try {
            List<String> categories = categoryService.getCategoriesName();
            return categories.isEmpty() ?
                    new ResponseEntity(HttpStatus.NO_CONTENT) : new ResponseEntity(categories, HttpStatus.OK);
        } catch (Exception e) {
            log.info("Something went wrong during getting  categories name");
            return new ResponseEntity(e.getMessage(), HttpStatus.valueOf(500));
        }
    }

    @PostMapping("/categories")
    public ResponseEntity saveCategory(@RequestBody CategoryDTO categoryDTO) {
        try {
            categoryService.saveCategory(categoryDTO);
            return new ResponseEntity(HttpStatus.CREATED);
        } catch (Exception e) {
            log.info("Something went wrong during saving new Category Object");
            return new ResponseEntity(e.getMessage(), HttpStatus.valueOf(500));
        }
    }

    @GetMapping("/categories/{id}")
    public ResponseEntity getCategoryById(@PathVariable String id) {
        try {
            CategoryDTO category = categoryService.getCategoryDTOById(id);
            return category == null ?
                    new ResponseEntity(HttpStatus.NO_CONTENT) : new ResponseEntity(category, HttpStatus.OK);
        } catch (Exception e) {
            log.info("Something went wrong during getting category by id");
            return new ResponseEntity(e.getMessage(), HttpStatus.valueOf(500));
        }
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity updateCategory(@RequestBody CategoryDTO categoryDTO, @PathVariable String id) {
        try {
            categoryService.updateCategoryById(categoryDTO, id);
            return new ResponseEntity(HttpStatus.OK);
        } catch (Exception e) {
            log.info("Something went wrong during saving new Category Object");
            return new ResponseEntity(e.getMessage(), HttpStatus.valueOf(500));
        }
    }
}

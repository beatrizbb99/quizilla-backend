package de.hsrm.quiz_gateway.controller;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.hsrm.quiz_gateway.entities.Category;
import de.hsrm.quiz_gateway.services.category.CategoryService;

@RestController
@RequestMapping("api/categories")
public class CategoryController {

    public CategoryService catService;

    public CategoryController(CategoryService catService) {
        this.catService = catService;
    }

    @PostMapping("/create")
    public String createCategory(@RequestBody Category category) throws InterruptedException, ExecutionException {
        return catService.createCategory(category);
    }

    @GetMapping("/{category_id}")
    public Category getCategory(@PathVariable("category_id") String cat_id) throws InterruptedException, ExecutionException {
        return catService.getCategory(cat_id);
    }

    @GetMapping
    public List<Category> getAllCategories() throws InterruptedException, ExecutionException {
        return catService.getAllCategories();
    }

    @PutMapping("/update/{category_id}")
    public String updateCategory(@PathVariable("category_id") String cat_id, @RequestBody Category category) throws InterruptedException, ExecutionException {
        return catService.updateCategory(cat_id, category);
    }

    @DeleteMapping("/delete/{category_id}")
    public String deleteCategory(@PathVariable("category_id") String cat_id) throws InterruptedException, ExecutionException {
        return catService.deleteCategory(cat_id);
    }

    
}

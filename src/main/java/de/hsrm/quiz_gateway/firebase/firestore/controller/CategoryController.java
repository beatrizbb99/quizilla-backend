package de.hsrm.quiz_gateway.firebase.firestore.controller;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.hsrm.quiz_gateway.entities.Category;
import de.hsrm.quiz_gateway.firebase.firestore.services.CategoryService;

@RestController
@RequestMapping("api/categories")
@CrossOrigin(origins = "*")
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
    public Category getCategory(@PathVariable String category_id) throws InterruptedException, ExecutionException {
        return catService.getCategory(category_id);
    }

    @GetMapping
    public List<Category> getAllCategories() throws InterruptedException, ExecutionException {
        return catService.getAllCategories();
    }

    @PutMapping("/update/{category_id}")
    public String updateCategory(@PathVariable String category_id, @RequestBody Category category) throws InterruptedException, ExecutionException {
        return catService.updateCategory(category_id, category);
    }

    @DeleteMapping("/delete/{category_id}/{category_name}")
    public String deleteCategory(@PathVariable String category_id, @PathVariable String category_name) throws InterruptedException, ExecutionException {
        return catService.deleteCategory(category_id, category_name);
    }

    
}

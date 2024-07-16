package ru.practicum.ewm.categories.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.categories.dto.CategoryDto;
import ru.practicum.ewm.categories.service.CategoryService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping("/admin/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto create(@RequestBody @Valid CategoryDto category) {
        log.info("Creating new category={}", category);

        return categoryService.create(category);
    }

    @DeleteMapping("/admin/categories/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategoryById(@PathVariable("catId") Long categoryId) {
        log.info("Deleting category id={}", categoryId);

        categoryService.deleteCategoryById(categoryId);
    }

    @PatchMapping("/admin/categories/{catId}")
    public CategoryDto edit(@PathVariable("catId") Long categoryId, @RequestBody @Valid CategoryDto category) {
        log.info("Editing category id={}, new name={}", categoryId, category);

        return categoryService.edit(categoryId, category);
    }

    @GetMapping("/categories")
    public List<CategoryDto> getAllCategories(@RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                              @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Getting all categories, page starts from={}, size={}", from, size);

        return categoryService.getAllCategories(from, size);
    }

    @GetMapping("/categories/{catId}")
    public CategoryDto getCategoryById(@PathVariable("catId") Long categoryId) {
        log.info("Getting category id={}", categoryId);

        return categoryService.getCategoryById(categoryId);
    }
}

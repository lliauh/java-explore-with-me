package ru.practicum.ewm.categories.service;

import ru.practicum.ewm.categories.dto.CategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto create(CategoryDto category);

    void deleteCategoryById(Long categoryId);

    CategoryDto edit(Long categoryId, CategoryDto category);

    List<CategoryDto> getAllCategories(Integer from, Integer size);

    CategoryDto getCategoryById(Long categoryId);

    void checkIfCategoryExists(Long categoryId);
}

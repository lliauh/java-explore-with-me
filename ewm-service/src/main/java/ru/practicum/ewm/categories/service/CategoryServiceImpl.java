package ru.practicum.ewm.categories.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.categories.dto.CategoryDto;
import ru.practicum.ewm.categories.dto.CategoryMapper;
import ru.practicum.ewm.categories.model.Category;
import ru.practicum.ewm.categories.repository.CategoryRepository;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public CategoryDto create(CategoryDto categoryDto) {
        Category category = CategoryMapper.toCategory(categoryDto);

        return CategoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Override
    public void deleteCategoryById(Long categoryId) {
        checkIfCategoryExists(categoryId);

        categoryRepository.deleteById(categoryId);
    }

    @Override
    public CategoryDto edit(Long categoryId, CategoryDto categoryDto) {
        checkIfCategoryExists(categoryId);

        Category updatedCategory = CategoryMapper.toCategory(categoryDto);
        updatedCategory.setId(categoryId);

        return CategoryMapper.toCategoryDto(categoryRepository.save(updatedCategory));
    }

    @Override
    public List<CategoryDto> getAllCategories(Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);

        return categoryRepository.findAll(pageRequest).stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategoryById(Long categoryId) {
        checkIfCategoryExists(categoryId);

        return CategoryMapper.toCategoryDto(categoryRepository.getReferenceById(categoryId));
    }

    @Override
    public void checkIfCategoryExists(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new NotFoundException(String.format("Category with id=%d was not found", categoryId));
        }
    }
}

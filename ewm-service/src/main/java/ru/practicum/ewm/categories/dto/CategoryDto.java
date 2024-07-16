package ru.practicum.ewm.categories.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto {
    private Long id;

    @NotBlank
    @Size(min = 1, message = "Name length cannot be less than 1 symbol.")
    @Size(max = 50, message = "Name length cannot be greater than 50 symbols.")
    private String name;
}

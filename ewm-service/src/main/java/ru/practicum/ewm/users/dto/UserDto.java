package ru.practicum.ewm.users.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;

    @Size(min = 2, message = "Name length cannot be less than 2 symbols.")
    @Size(max = 250, message = "Name length cannot be greater than 250 symbols.")
    private String name;

    @Email
    @Size(min = 6, message = "Email length cannot be less than 6 symbols.")
    @Size(max = 254, message = "Email length cannot be greater than 254 symbols.")
    private String email;
}

package ru.practicum.ewm.compilations.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.compilations.model.Compilation;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CompilationMapper {
    public static Compilation toCompilation(NewCompilationDto newCompilation) {
        Compilation compilation = new Compilation();

        compilation.setTitle(newCompilation.getTitle());
        if (newCompilation.getPinned() == null) {
            compilation.setPinned(false);
        } else {
            compilation.setPinned(newCompilation.getPinned());
        }

        return compilation;
    }

    public static CompilationDto toCompilationDto(Compilation compilation) {
        CompilationDto compilationDto = new CompilationDto();
        compilationDto.setId(compilation.getId());
        compilationDto.setPinned(compilation.getPinned());
        compilationDto.setTitle(compilation.getTitle());

        return compilationDto;
    }
}

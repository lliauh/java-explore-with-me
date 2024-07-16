package ru.practicum.ewm.compilations.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.compilations.dto.CompilationDto;
import ru.practicum.ewm.compilations.dto.CompilationMapper;
import ru.practicum.ewm.compilations.dto.NewCompilationDto;
import ru.practicum.ewm.compilations.dto.UpdateCompilationRequest;
import ru.practicum.ewm.compilations.model.Compilation;
import ru.practicum.ewm.compilations.repository.CompilationRepository;
import ru.practicum.ewm.events.dto.EventMapper;
import ru.practicum.ewm.events.dto.EventShortDto;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.repository.EventRepository;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);

        List<Compilation> compilations = compilationRepository.getAllCompilations(pinned, pageRequest);

        List<CompilationDto> compilationsDto = new ArrayList<>();

        for (Compilation compilation : compilations)  {
            CompilationDto compilationDto = CompilationMapper.toCompilationDto(compilation);
            addEventsToDto(compilation, compilationDto);
            compilationsDto.add(compilationDto);
        }

        return compilationsDto;
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {
        checkIfCompilationExists(compId);

        Compilation compilation = compilationRepository.getReferenceById(compId);
        CompilationDto compilationDto = CompilationMapper.toCompilationDto(compilation);
        addEventsToDto(compilation, compilationDto);

        return compilationDto;
    }

    @Override
    public CompilationDto createCompilation(NewCompilationDto newCompilation) {
        Compilation compilation = CompilationMapper.toCompilation(newCompilation);

        if (newCompilation.getEvents() != null) {
            Set<Event> eventsSet = getEventsFromDto(newCompilation.getEvents());

            compilation.setEvents(eventsSet);
        }

        Compilation result = compilationRepository.save(compilation);
        CompilationDto resultDto = CompilationMapper.toCompilationDto(result);
        if (compilation.getEvents() != null) {
            addEventsToDto(result, resultDto);
        }

        return resultDto;
    }

    @Override
    public void deleteCompilation(Long compId) {
        checkIfCompilationExists(compId);

        compilationRepository.deleteById(compId);
    }

    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updatedCompilation) {
        checkIfCompilationExists(compId);

        Compilation compilation = compilationRepository.getReferenceById(compId);

        if (updatedCompilation.getTitle() != null && !updatedCompilation.getTitle().isBlank()) {
            compilation.setTitle(updatedCompilation.getTitle());
        }

        if (updatedCompilation.getPinned() != null) {
            compilation.setPinned(updatedCompilation.getPinned());
        }

        if (updatedCompilation.getEvents() != null) {
            Set<Event> eventsSet = getEventsFromDto(updatedCompilation.getEvents());

            compilation.setEvents(eventsSet);
        }

        Compilation savedCompilation = compilationRepository.save(compilation);

        CompilationDto resultDto = CompilationMapper.toCompilationDto(savedCompilation);
        addEventsToDto(savedCompilation, resultDto);

        return resultDto;
    }

    private void checkIfCompilationExists(Long compId) {
        if (!compilationRepository.existsById(compId)) {
            throw new NotFoundException(String.format("Compilation with id=%d was not found", compId));
        }
    }

    private void addEventsToDto(Compilation compilation, CompilationDto compilationDto) {
        Set<EventShortDto> eventShortDtoSet = new HashSet<>();
        for (Event event : compilation.getEvents()) {
            eventShortDtoSet.add(EventMapper.toEventShortDto(event));
        }
        compilationDto.setEvents(eventShortDtoSet);
    }

    private Set<Event> getEventsFromDto(Set<Long> eventsSet) {
        List<Event> foundEventsList = eventRepository.getEventsByEventsIds(
                new ArrayList<>(eventsSet));

        Set<Long> foundEventsIds = foundEventsList.stream()
                .map(Event::getId)
                .collect(Collectors.toSet());

        for (Long eventId : eventsSet) {
            if (!foundEventsIds.contains(eventId)) {
                throw new NotFoundException(String.format("Event with id=%d was not found", eventId));
            }
        }

        return new HashSet<>(foundEventsList);
    }
}

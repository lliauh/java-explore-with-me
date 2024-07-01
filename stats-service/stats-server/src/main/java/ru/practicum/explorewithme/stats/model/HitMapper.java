package ru.practicum.explorewithme.stats.model;

import ru.practicum.explorewithme.stats.dto.HitDto;

public class HitMapper {
    public static Hit toHit(HitDto hitDto) {
        return new Hit(hitDto.getId(), hitDto.getApp(), hitDto.getUri(), hitDto.getIp(), hitDto.getTimestamp());
    }

    public static HitDto toHitDto(Hit hit) {
        return new HitDto(hit.getId(), hit.getApp(), hit.getUri(), hit.getIp(), hit.getTimestamp());
    }
}

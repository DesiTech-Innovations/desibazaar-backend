package com.desitech.desibazaar.changelog.mapper;

import com.desitech.desibazaar.changelog.dto.ChangeLogDto;
import com.desitech.desibazaar.changelog.entity.ChangeLog;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ChangeLogMapper {
    ChangeLog toEntity(ChangeLogDto dto);
    ChangeLogDto toDto(ChangeLog entity);
}
package com.desitech.vyaparsathi.changelog.mapper;

import com.desitech.vyaparsathi.changelog.dto.ChangeLogDto;
import com.desitech.vyaparsathi.changelog.entity.ChangeLog;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-19T13:17:59+0000",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.16 (Eclipse Adoptium)"
)
@Component
public class ChangeLogMapperImpl implements ChangeLogMapper {

    @Override
    public ChangeLog toEntity(ChangeLogDto dto) {
        if ( dto == null ) {
            return null;
        }

        ChangeLog changeLog = new ChangeLog();

        changeLog.setId( dto.getId() );
        changeLog.setEntityType( dto.getEntityType() );
        changeLog.setEntityId( dto.getEntityId() );
        changeLog.setOperation( dto.getOperation() );
        changeLog.setPayloadJson( dto.getPayloadJson() );
        changeLog.setDeviceId( dto.getDeviceId() );
        changeLog.setSeqNo( dto.getSeqNo() );
        changeLog.setCreatedAt( dto.getCreatedAt() );

        return changeLog;
    }

    @Override
    public ChangeLogDto toDto(ChangeLog entity) {
        if ( entity == null ) {
            return null;
        }

        ChangeLogDto changeLogDto = new ChangeLogDto();

        changeLogDto.setId( entity.getId() );
        changeLogDto.setEntityType( entity.getEntityType() );
        changeLogDto.setEntityId( entity.getEntityId() );
        changeLogDto.setOperation( entity.getOperation() );
        changeLogDto.setPayloadJson( entity.getPayloadJson() );
        changeLogDto.setDeviceId( entity.getDeviceId() );
        changeLogDto.setSeqNo( entity.getSeqNo() );
        changeLogDto.setCreatedAt( entity.getCreatedAt() );

        return changeLogDto;
    }

    @Override
    public List<ChangeLogDto> toDtoList(List<ChangeLog> entities) {
        if ( entities == null ) {
            return null;
        }

        List<ChangeLogDto> list = new ArrayList<ChangeLogDto>( entities.size() );
        for ( ChangeLog changeLog : entities ) {
            list.add( toDto( changeLog ) );
        }

        return list;
    }
}

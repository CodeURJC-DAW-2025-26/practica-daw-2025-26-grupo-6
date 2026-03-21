package com.grupo6daw.lcdd_daw.dto;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.grupo6daw.lcdd_daw.model.New;

@Mapper(componentModel = "spring")
public interface NewMapper {

    NewBasicDTO toDTO(New n);

    List<NewBasicDTO> toDTOs(Collection<New> news);

    @Mapping(target = "newDescription", ignore = true)
    @Mapping(target = "newImage", ignore = true)
    @Mapping(target = "newTag", ignore = true)
    @Mapping(target = "newEvents", ignore = true)
    @Mapping(target = "newCreator", ignore = true)
    @Mapping(target = "validated", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    New toDomain(NewBasicDTO newDTO);
}
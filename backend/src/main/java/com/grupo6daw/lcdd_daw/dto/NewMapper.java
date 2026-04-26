package com.grupo6daw.lcdd_daw.dto;

import java.util.Collection;
import java.util.List;

import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.grupo6daw.lcdd_daw.model.New;

@Mapper(componentModel = "spring", uses = { ImageMapper.class })
public interface NewMapper {

    // BASIC DTO
    @Named("toBasicDTO")
    NewBasicDTO toBasicDTO(New n);

    @IterableMapping(qualifiedByName = "toBasicDTO")
    List<NewBasicDTO> toBasicDTOs(Collection<New> news);

    @Mapping(target = "newDescription", ignore = true)
    @Mapping(target = "newImage", ignore = true)
    @Mapping(target = "newTag", ignore = true)
    @Mapping(target = "newEvents", ignore = true)
    @Mapping(target = "newCreator", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    New toDomainFromBasicDTO(NewBasicDTO newDTO);

    // FULL DTO
    @Named("toFullDTO")
    NewDTO toFullDTO(New n);

    @IterableMapping(qualifiedByName = "toFullDTO")
    List<NewDTO> toFullDTOs(Collection<New> news);

    New toDomainFromFullDTO(NewDTO newDTO);
}
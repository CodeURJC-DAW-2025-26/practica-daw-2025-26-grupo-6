package com.grupo6daw.lcdd_daw.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.grupo6daw.lcdd_daw.model.Image;

@Mapper(componentModel = "spring")
public interface ImageMapper {

    ImageDTO toDTO(Image image);

    @Mapping(target = "imageFile", ignore = true)
    Image toDomain(ImageDTO imageDTO);
}

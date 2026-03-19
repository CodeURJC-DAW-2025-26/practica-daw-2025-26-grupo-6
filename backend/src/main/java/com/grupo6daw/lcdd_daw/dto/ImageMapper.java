package com.grupo6daw.lcdd_daw.dto;

import org.mapstruct.Mapper;

import com.grupo6daw.lcdd_daw.model.Image;

@Mapper(componentModel = "spring")
public interface ImageMapper {

    ImageDTO toDTO(Image image);

    Image toDomain(ImageDTO imageDTO);
}

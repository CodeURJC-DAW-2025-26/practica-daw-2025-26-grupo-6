package com.grupo6daw.lcdd_daw.dto;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;

import com.grupo6daw.lcdd_daw.model.Error;

@Mapper(componentModel = "spring")
public interface ErrorMapper {

    ErrorDTO toDTO(Error error);

    List<ErrorDTO> toDTOs(Collection<Error> errors);

    Error toDomain(ErrorDTO errorDTO);
}
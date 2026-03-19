package com.grupo6daw.lcdd_daw.dto;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;

import com.grupo6daw.lcdd_daw.model.Game;

@Mapper(componentModel = "spring")
public interface GameMapper {

    GameDTO toDTO(Game game);

    List<GameDTO> toDTOs(Collection<Game> games);

    Game toDomain(GameDTO gameDTO);
}

package com.grupo6daw.lcdd_daw.dto;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.grupo6daw.lcdd_daw.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserBasicDTO toDTO(User user);

    List<UserBasicDTO> toDTOs(Collection<User> users);

    @Mapping(target = "userName", ignore = true)
    @Mapping(target = "userSurname", ignore = true)
    @Mapping(target = "userInterests", ignore = true)
    @Mapping(target = "userEncodedPassword", ignore = true)
    @Mapping(target = "userFavGames", ignore = true)
    @Mapping(target = "userOwnEvents", ignore = true)
    @Mapping(target = "userRegisteredEvents", ignore = true)
    @Mapping(target = "userNews", ignore = true)
    @Mapping(target = "userImage", ignore = true)
    @Mapping(target = "userRoles", ignore = true)
    User toDomain(UserBasicDTO userDTO);
}

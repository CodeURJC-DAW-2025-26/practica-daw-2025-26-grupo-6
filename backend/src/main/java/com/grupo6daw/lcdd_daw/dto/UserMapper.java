package com.grupo6daw.lcdd_daw.dto;

import java.util.Collection;
import java.util.List;

import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.grupo6daw.lcdd_daw.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // BASIC DTO
    @Named("toBasicDTO")
    UserBasicDTO toBasicDTO(User user);

    @IterableMapping(qualifiedByName = "toBasicDTO")
    List<UserBasicDTO> toBasicDTOs(Collection<User> users);

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
    User toDomainFromBasicDTO(UserBasicDTO userDTO);

    // FULL DTO
    @Named("toFullDTO")
    UserDTO toFullDTO(User user);

    @IterableMapping(qualifiedByName = "toFullDTO")
    List<UserDTO> toFullDTOs(Collection<User> users);

    @Mapping(target = "userEncodedPassword", ignore = true)
    User toDomainFromFullDTO(UserDTO userDTO);
}

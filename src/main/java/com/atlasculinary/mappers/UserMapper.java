package com.atlasculinary.mappers;

import com.atlasculinary.dtos.AdminDto;
import com.atlasculinary.dtos.UserDto;
import com.atlasculinary.entities.UserProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(source = "accountId", target = "accountId")
    @Mapping(source = "account.email", target = "email")
    @Mapping(source = "account.fullName", target = "fullName")
    @Mapping(source = "account.status", target = "status")
    @Mapping(source = "account.avatarUrl", target = "avatarUrl")
    UserDto toDto(UserProfile user);
    List<UserDto> toDtoList(List<UserProfile> userList);
}

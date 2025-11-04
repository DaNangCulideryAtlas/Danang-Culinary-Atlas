package com.atlasculinary.mappers;

import com.atlasculinary.dtos.UserDto;
import com.atlasculinary.entities.Account;
import com.atlasculinary.entities.UserProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "accountId", target = "accountId")
    @Mapping(source = "account.email", target = "email")
    @Mapping(
            target = "fullName",
            expression = "java(" +
                    "userProfile.getAccount() != null && " +
                    "userProfile.getAccount().getFullName() != null && " +
                    "!userProfile.getAccount().getFullName().trim().isEmpty() " +
                    "? userProfile.getAccount().getFullName() " +
                    ": com.atlasculinary.utils.NameUtil.getNameFromEmail(" +
                    "userProfile.getAccount() != null ? userProfile.getAccount().getEmail() : null)" +
                    ")"
    )
    @Mapping(source = "account.status", target = "status")
    @Mapping(source = "account.avatarUrl", target = "avatarUrl")
    UserDto toDto(UserProfile userProfile);

    List<UserDto> toDtoList(List<UserProfile> userList);


}

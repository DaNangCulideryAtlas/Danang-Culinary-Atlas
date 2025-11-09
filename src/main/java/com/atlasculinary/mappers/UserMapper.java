package com.atlasculinary.mappers;

import com.atlasculinary.dtos.UpdateReviewRequest;
import com.atlasculinary.dtos.UserDto;
import com.atlasculinary.dtos.profile.UserProfileUpdateDto;
import com.atlasculinary.dtos.profile.VendorProfileUpdateDto;
import com.atlasculinary.entities.Account;
import com.atlasculinary.entities.Review;
import com.atlasculinary.entities.UserProfile;
import com.atlasculinary.entities.VendorProfile;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "accountId", target = "accountId")
    @Mapping(source = "account.email", target = "email")
    @Mapping(
            target = "fullName",
            expression = "java(" +
                    "userProfile != null && userProfile.getAccount() != null " +
                    "? com.atlasculinary.utils.NameUtil.resolveName(" +
                    "userProfile.getAccount().getFullName(), " +
                    "userProfile.getAccount().getEmail()) " +
                    ": \"Anonymous\"" +
                    ")"
    )
    @Mapping(source = "account.status", target = "status")
    @Mapping(source = "account.avatarUrl", target = "avatarUrl")
    @Mapping(source= "gender", target = "gender")
    UserDto toDto(UserProfile userProfile);

    List<UserDto> toDtoList(List<UserProfile> userList);

    @Mapping(source = "avatarUrl", target = "account.avatarUrl")
    @Mapping(source = "fullName", target = "account.fullName")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(UserProfileUpdateDto request, @MappingTarget UserProfile targetEntity);


}

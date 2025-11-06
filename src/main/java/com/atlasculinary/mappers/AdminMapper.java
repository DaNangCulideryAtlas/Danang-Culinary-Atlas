package com.atlasculinary.mappers;

import com.atlasculinary.dtos.AdminDto;
import com.atlasculinary.dtos.profile.AdminProfileUpdateDto;
import com.atlasculinary.dtos.profile.VendorProfileUpdateDto;
import com.atlasculinary.entities.Account; // Cần import Account
import com.atlasculinary.entities.AdminProfile;
import com.atlasculinary.entities.VendorProfile;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AdminMapper {
    @Mapping(source = "accountId", target = "accountId")
    @Mapping(source = "account.email", target = "email")
    // Sử dụng logic tùy chỉnh để kiểm tra null/rỗng và tạo tên từ email
    @Mapping(
            target = "fullName",
            expression = "java(" +
                    "adminProfile.getAccount() != null && " +
                    "adminProfile.getAccount().getFullName() != null && " +
                    "!adminProfile.getAccount().getFullName().trim().isEmpty() " +
                    "? adminProfile.getAccount().getFullName() " +
                    ": com.atlasculinary.utils.NameUtil.getNameFromEmail(" +
                    "adminProfile.getAccount() != null ? adminProfile.getAccount().getEmail() : null)" +
                    ")"
    )
    @Mapping(source = "account.status", target = "status")
    @Mapping(source = "account.avatarUrl", target = "avatarUrl")
    @Mapping(source = "gender", target= "gender")
    AdminDto toDto(AdminProfile adminProfile);

    List<AdminDto> toDtoList(List<AdminProfile> adminList);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(AdminProfileUpdateDto request, @MappingTarget AdminProfile targetEntity);
}

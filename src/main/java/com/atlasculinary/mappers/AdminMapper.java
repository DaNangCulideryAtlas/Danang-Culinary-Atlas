package com.atlasculinary.mappers;

import com.atlasculinary.dtos.AdminDto;
import com.atlasculinary.entities.Account; // Cần import Account
import com.atlasculinary.entities.AdminProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named; // Cần import Named

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
    AdminDto toDto(AdminProfile adminProfile);

    List<AdminDto> toDtoList(List<AdminProfile> adminList);


}

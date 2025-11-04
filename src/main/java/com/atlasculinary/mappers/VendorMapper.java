package com.atlasculinary.mappers;

import com.atlasculinary.utils.NameUtil;
import com.atlasculinary.dtos.VendorDto;
import com.atlasculinary.entities.Account; // Cần import Account để sử dụng trong logic tùy chỉnh
import com.atlasculinary.entities.VendorProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VendorMapper {

    @Mapping(source = "accountId", target = "accountId")
    @Mapping(source = "account.email", target = "email")
    @Mapping(
            target = "fullName",
            expression = "java(" +
                    "vendorProfile.getAccount() != null && " +
                    "vendorProfile.getAccount().getFullName() != null && " +
                    "!vendorProfile.getAccount().getFullName().trim().isEmpty() " +
                    "? vendorProfile.getAccount().getFullName() " +
                    ": com.atlasculinary.utils.NameUtil.getNameFromEmail(" +
                    "vendorProfile.getAccount() != null ? vendorProfile.getAccount().getEmail() : null)" +
                    ")"
    )
    @Mapping(source = "account.status", target = "status")
    @Mapping(source = "account.avatarUrl", target = "avatarUrl")

    VendorDto toDto(VendorProfile vendorProfile);

}
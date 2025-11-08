package com.atlasculinary.mappers;

import com.atlasculinary.dtos.VendorDto;
import com.atlasculinary.dtos.profile.VendorProfileUpdateDto;
import com.atlasculinary.entities.VendorProfile;
import org.mapstruct.*;

import java.util.List;

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
    @Mapping(source = "gender", target= "gender")
    VendorDto toDto(VendorProfile vendorProfile);

    List<VendorDto> toDtoList(List<VendorDto> vendorDtoList);

    @Mapping(source = "avatarUrl", target = "account.avatarUrl")
    @Mapping(source = "fullName", target = "account.fullName")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(VendorProfileUpdateDto request, @MappingTarget VendorProfile targetEntity);

}
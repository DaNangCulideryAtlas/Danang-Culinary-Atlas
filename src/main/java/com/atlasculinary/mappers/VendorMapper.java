package com.atlasculinary.mappers;

import com.atlasculinary.dtos.VendorDto;
import com.atlasculinary.entities.VendorProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VendorMapper {
    @Mapping(source = "accountId", target = "accountId")
    @Mapping(source = "account.email", target = "email")
    @Mapping(source = "account.fullName", target = "fullName")
    @Mapping(source = "account.status", target = "status")
    @Mapping(source = "account.avatarUrl", target = "avatarUrl")
    VendorDto toDto(VendorProfile vendor);

    List<VendorDto> toDtoList(List<VendorProfile> vendorList);
}

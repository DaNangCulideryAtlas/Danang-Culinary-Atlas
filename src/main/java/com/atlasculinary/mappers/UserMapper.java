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
    @Mapping(source = "account", target = "fullName", qualifiedByName = "mapFullNameOrGenerate")
    @Mapping(source = "account.status", target = "status")
    @Mapping(source = "account.avatarUrl", target = "avatarUrl")
    UserDto toDto(UserProfile user);

    List<UserDto> toDtoList(List<UserProfile> userList);

    @Named("mapFullNameOrGenerate")
    default String mapFullNameOrGenerate(Account user) {
        String fullName = user.getFullName();
        String email = user.getEmail();

        // 1. Kiểm tra fullName có null hoặc rỗng (sau khi cắt khoảng trắng)
        if (fullName == null || fullName.trim().isEmpty()) {
            // 2. Nếu rỗng/null, gọi hàm getName(email)
            return getNameFromEmail(email);
        }

        // 3. Nếu hợp lệ, trả về fullName
        return fullName;
    }

    default String getNameFromEmail(String email) {
        if (email == null) return "Unknown User";
        // Lấy phần trước @ và viết hoa chữ cái đầu
        String username = email.split("@")[0];
        return username.substring(0, 1).toUpperCase() + username.substring(1);
    }
}

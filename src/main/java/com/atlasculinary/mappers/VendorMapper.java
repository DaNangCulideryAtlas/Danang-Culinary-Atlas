package com.atlasculinary.mappers;

import com.atlasculinary.dtos.VendorDto;
import com.atlasculinary.entities.Account; // Cần import Account để sử dụng trong logic tùy chỉnh
import com.atlasculinary.entities.VendorProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named; // Cần import Named

import java.util.List;

@Mapper(componentModel = "spring")
public interface VendorMapper {
    @Mapping(source = "accountId", target = "accountId")
    @Mapping(source = "account.email", target = "email")
    // Sử dụng logic tùy chỉnh để kiểm tra null/rỗng và tạo tên từ email
    @Mapping(source = "account", target = "fullName", qualifiedByName = "mapFullNameOrGenerate")
    @Mapping(source = "account.status", target = "status")
    @Mapping(source = "account.avatarUrl", target = "avatarUrl")
    VendorDto toDto(VendorProfile vendor);

    List<VendorDto> toDtoList(List<VendorProfile> vendorList);

    /**
     * Phương thức mặc định để xử lý logic: Nếu fullName rỗng/null, tạo tên từ email.
     */
    @Named("mapFullNameOrGenerate")
    default String mapFullNameOrGenerate(Account account) {
        String fullName = account.getFullName();
        String email = account.getEmail();

        // 1. Kiểm tra fullName có null hoặc rỗng (sau khi cắt khoảng trắng)
        if (fullName == null || fullName.trim().isEmpty()) {
            // 2. Nếu rỗng/null, gọi hàm getName(email)
            return getNameFromEmail(email);
        }

        // 3. Nếu hợp lệ, trả về fullName
        return fullName;
    }

    /**
     * Hàm giả định để tạo tên từ email (ví dụ: "dngphclng@..." -> "Dngphclng")
     */
    default String getNameFromEmail(String email) {
        if (email == null) return "Unknown Vendor"; // Đổi tên dự phòng
        // Lấy phần trước @ và viết hoa chữ cái đầu
        String username = email.split("@")[0];
        return username.substring(0, 1).toUpperCase() + username.substring(1);
    }
}

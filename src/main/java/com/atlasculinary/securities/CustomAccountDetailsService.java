package com.atlasculinary.securities;

import com.atlasculinary.entities.Account;
import com.atlasculinary.repositories.AccountRepository;
import com.atlasculinary.repositories.RoleActionMapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service tùy chỉnh để tải thông tin người dùng (UserDetails) từ cơ sở dữ liệu.
 * Lớp này trả về CustomAccountDetails với action-based permissions.
 */
@Service
@RequiredArgsConstructor
public class CustomAccountDetailsService implements UserDetailsService {

    private final AccountRepository accountRepository;
    private final RoleActionMapRepository roleActionMapRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy tài khoản với email: " + email));

        // Load all action codes for this account's roles
        List<String> actionCodes = account.getAccountRoleMapSet().stream()
                .flatMap(roleMap -> roleActionMapRepository.findActionCodesByRoleId(roleMap.getRoleId()).stream())
                .distinct()
                .collect(Collectors.toList());

        return new CustomAccountDetails(account, actionCodes);
    }
}

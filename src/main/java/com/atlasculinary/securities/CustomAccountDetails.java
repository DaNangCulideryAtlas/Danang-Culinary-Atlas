package com.atlasculinary.securities;

import com.atlasculinary.entities.Account;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
public class CustomAccountDetails implements UserDetails {

    private final UUID accountId;
    private final String email;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;
    private final boolean accountNonLocked;
    private final boolean enabled;

    public CustomAccountDetails(Account account) {
        this(account, null);
    }

    public CustomAccountDetails(Account account, List<String> actionCodes) {
        this.accountId = account.getAccountId();
        this.email = account.getEmail();
        this.password = account.getPassword();

        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        
        // Add role-based authorities (for backward compatibility)
        account.getAccountRoleMapSet().stream()
                .map(roleMap -> new SimpleGrantedAuthority(roleMap.getRole().getRoleName()))
                .forEach(grantedAuthorities::add);

        // Add action-based authorities if provided
        if (actionCodes != null && !actionCodes.isEmpty()) {
            actionCodes.stream()
                    .map(SimpleGrantedAuthority::new)
                    .forEach(grantedAuthorities::add);
        }

        this.authorities = grantedAuthorities;

        // Status checks
        this.accountNonLocked = !account.getStatus().name().equals("BLOCKED");
        this.enabled = !account.getStatus().name().equals("DELETED");
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}

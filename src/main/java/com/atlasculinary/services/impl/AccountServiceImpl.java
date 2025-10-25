package com.atlasculinary.services.impl;

import com.atlasculinary.dtos.AccountDto;
import com.atlasculinary.entities.Account;
import com.atlasculinary.entities.AccountRoleMap;
import com.atlasculinary.exceptions.ResourceNotFoundException;
import com.atlasculinary.mappers.AccountMapper;
import com.atlasculinary.repositories.AccountRepository;
import com.atlasculinary.repositories.AccountRoleMapRepository;
import com.atlasculinary.services.AccountService;
import org.springframework.stereotype.Service;

import java.util.UUID;
@Service
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    public AccountServiceImpl(AccountMapper accountMapper,
                              AccountRepository accountRepository) {
        this.accountMapper = accountMapper;
        this.accountRepository = accountRepository;
    }

    @Override
    public Account getAccountById(UUID accountId) {
        var account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with ID: " + accountId));
        return account;

    }
    @Override
    public Boolean isExist(UUID accountId) {
        return accountRepository.existsById(accountId);
    }

    @Override
    public Boolean isAdmin(UUID accountId) {
        var account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with ID: " + accountId));

        return account.getAccountRoleMapSet().stream()
                .map(AccountRoleMap::getRole)
                .anyMatch(role -> "ADMIN".equals(role.getRoleName()));
    }
    @Override
    public Boolean isVendor(UUID accountId) {
        var account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with ID: " + accountId));

        return account.getAccountRoleMapSet().stream()
                .map(AccountRoleMap::getRole)
                .anyMatch(role -> "VENDOR".equals(role.getRoleName()));
    }
}

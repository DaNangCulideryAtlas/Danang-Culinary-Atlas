package com.atlasculinary.repositories;

import com.atlasculinary.entities.Report;
import com.atlasculinary.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReportRepository extends JpaRepository<Report, UUID> {
    List<Report> findByReporterAccount(Account account);
}

package com.atlasculinary.entities;

import com.atlasculinary.enums.ApprovalStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "business_license")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusinessLicense {

    @Id
    @GeneratedValue(generator = "uuid2")
    @UuidGenerator
    @Column(name = "license_id", columnDefinition = "UUID")
    private UUID licenseId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_account_id", unique = true, nullable = false)
    private Account ownerAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by_account_id")
    private Account approvedByAccount;

    @Column(name = "license_number", nullable = false, unique = true, length = 50)
    private String licenseNumber;

    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    @Column(name = "expire_date", nullable = false)
    private LocalDate expireDate;

    @Column(name = "document_url", nullable = false)
    private String documentUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "approval_status", nullable = false, length = 20)
    private ApprovalStatus approvalStatus = ApprovalStatus.PENDING;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;
}
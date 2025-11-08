package com.atlasculinary.services.impl;

import com.atlasculinary.dtos.*;
import com.atlasculinary.entities.Account;
import com.atlasculinary.entities.Notification;
import com.atlasculinary.entities.Restaurant;
import com.atlasculinary.entities.Review;
import com.atlasculinary.exceptions.ResourceNotFoundException;
import com.atlasculinary.mappers.NotificationMapper;
import com.atlasculinary.repositories.ReviewRepository;
import com.atlasculinary.services.*;
import com.atlasculinary.utils.NameUtil;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import com.atlasculinary.enums.ApprovalStatus;
import com.atlasculinary.enums.NotificationType;
import com.atlasculinary.repositories.NotificationRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@Service
public class NotificationServiceImpl implements NotificationService {
    private static final Logger LOGGER = Logger.getLogger(NotificationServiceImpl.class.getName());
    private final NotificationRepository notificationRepository;
    private final AccountService accountService;
    private final AdminService adminService;
    private final VendorService vendorService;
    private final JavaMailSender mailSender;
    private final NotificationMapper notificationMapper;
    private final ReviewRepository reviewRepository;

    public NotificationServiceImpl(
            NotificationRepository notificationRepository,
            NotificationMapper notificationMapper,
            AccountService accountService,
            AdminService adminService,
            VendorService vendorService,
            JavaMailSender mailSender,
            ReviewRepository reviewRepository
    ) {
        this.notificationRepository = notificationRepository;
        this.notificationMapper = notificationMapper;
        this.accountService = accountService;
        this.adminService = adminService;
        this.vendorService = vendorService;
        this.mailSender = mailSender;
        this.reviewRepository = reviewRepository;
    }
    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void sendWelcomeNotification(UUID accountId) {
        var account = accountService.getAccountById(accountId);
        String recipientEmail = account.getEmail();
        try {
            String subject = "Chào mừng đến với Atlas Culinary!";
            String content = buildWelcomeEmailContent(recipientEmail);
            sendEmail(recipientEmail, subject, content);
        } catch (MessagingException e) {
            LOGGER.severe("Lỗi gửi email chào mừng tới " + recipientEmail + ": " + e.getMessage());
        }
    }

    @Override
    public void sendPasswordResetRequest(PasswordResetRequest passwordResetRequest) {
        var accountDto = accountService.getAccountById(passwordResetRequest.getAccountId());
        String recipientEmail = accountDto.getEmail();
        try {
            String subject = "Yêu cầu Đặt lại Mật khẩu";
            String content = buildPasswordResetContent(passwordResetRequest.getResetToken());
            sendEmail(recipientEmail, subject, content);
        } catch (MessagingException e) {
            LOGGER.severe("Lỗi gửi email đặt lại mật khẩu tới " + recipientEmail + ": " + e.getMessage());
        }
    }

    @Override
    @Async
    public void notifyAdminNewRestaurantSubmission(UUID restaurantId) {
        try {
            // 1. Gửi Email cho Admin
            String subject = "[CẦN XÉT DUYỆT] Nhà hàng mới: " + restaurantId;
            String content = buildAdminSubmissionContent(restaurantId);
            List<AdminDto> adminDtoList = adminService.getAllAdmins();
            for (var adminDto: adminDtoList) {
                String adminEmail = adminDto.getEmail();
                UUID adminId = adminDto.getAccountId();
                UUID adminAccountId = adminDto.getAccountId();

                sendEmail(adminEmail, subject, content);

                AddNotificationRequest addNotificationRequest = new AddNotificationRequest(
                        adminAccountId,
                        "Nhà hàng mới cần duyệt",
                        "Một nhà hàng mới đã được gửi lên. ID: " + restaurantId,
                        NotificationType.RESTAURANT_SUBMISSION,
                        "/admin/review/" + restaurantId);

                createInAppNotification(addNotificationRequest);
            }
        } catch (MessagingException e) {
            LOGGER.severe("Lỗi gửi email thông báo xét duyệt tới Admin: " + e.getMessage());
        }
    }

    @Override
    @Async
    public void notifyVendorRestaurantStatusUpdate(RestaurantStatusUpdateRequest request) {
        var vendorDto = vendorService.getVendorById(request.getVendorId());
        String vendorEmail = vendorDto.getEmail();
        UUID vendorAccountId = vendorDto.getAccountId();

        try {
            String subject = "Cập nhật Trạng thái Nhà hàng: " + request.getRestaurantName();
            String content = buildVendorStatusUpdateContent(request.getRestaurantName(), request.getNewStatus(), request.getRejectionReason());
            sendEmail(vendorEmail, subject, content);
            LOGGER.severe("Gui Thanh Cong");
            String title = request.getNewStatus() == ApprovalStatus.APPROVED ? "Nhà hàng được phê duyệt" : "Nhà hàng bị từ chối";
            String message = request.getNewStatus() == ApprovalStatus.APPROVED ?
                    request.getRestaurantName() + " của bạn đã được Admin phê duyệt." :
                    request.getRestaurantName() + " của bạn đã bị từ chối. Lý do: " + request.getRejectionReason();
            NotificationType type = request.getNewStatus() == ApprovalStatus.APPROVED ? NotificationType.RESTAURANT_APPROVED: NotificationType.RESTAURANT_REJECTED;

            AddNotificationRequest addNotificationRequest = new AddNotificationRequest(
                    vendorAccountId,
                    title,
                    message,
                    type,
                    "/vendor/restaurant/" + request.getRestaurantName()
            );
            createInAppNotification(addNotificationRequest);

        } catch (MessagingException e) {
            LOGGER.severe("Lỗi gửi email cập nhật trạng thái tới Vendor " + vendorEmail + ": " + e.getMessage());
        }
    }

    @Override
    public void notifySystemError(SystemErrorRequest request) {
        try {
            String errorTitle = request.getErrorTitle();
            String errorMessage = request.getErrorMessage();
            // Lấy danh sách tất cả Admin
            List<AdminDto> adminDtoList = adminService.getAllAdmins();

            String subject = "[KHẨN CẤP] Lỗi Hệ Thống: " + errorTitle;
            String content = buildSystemErrorContent(errorTitle, errorMessage);

            for (var admin : adminDtoList) {
                // 1. Gửi Email cho từng Admin
                sendEmail(admin.getEmail(), subject, content);

                // 2. Tạo In-App Notification cho từng Admin
                AddNotificationRequest addNotificationRequest = new AddNotificationRequest(
                        admin.getAccountId(),
                        "Cảnh báo Lỗi Hệ thống",
                        errorTitle + ". Chi tiết: " + errorMessage,
                        NotificationType.SYSTEM_ALERT,
                        "/admin/system-logs");

                createInAppNotification(addNotificationRequest);
            }
        } catch (MessagingException e) {
            LOGGER.severe("Lỗi gửi email cảnh báo lỗi hệ thống: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void createInAppNotification(AddNotificationRequest request) {

        Account recipientAccount = accountService.getAccountById(request.getAccountId());

        Notification notification = notificationMapper.toEntity(request);
        notification.setAccount(recipientAccount);
        notification.setIsRead(false);

        notificationRepository.save(notification);
    }

    @Override
    public Page<NotificationDto> getNotificationsByRecipientId(UUID accountId, int page, int size, String sortBy, String sortDirection) {

        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ?
                Sort.Direction.DESC :
                Sort.Direction.ASC;

        Sort sort = Sort.by(direction, sortBy);

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Notification> notificationPage = notificationRepository.findByAccount_AccountId(accountId, pageable);


        return notificationPage.map(notificationMapper::toDto);
    }

    @Override
    public long getUnreadCount(UUID accountId) {
        return notificationRepository.countByAccount_AccountIdAndIsReadFalse(accountId);
    }

    @Override
    public List<NotificationDto> getTop10Unread(UUID accountId) {

        List<Notification> notifications = notificationRepository
                .findTop10ByAccount_AccountIdAndIsReadFalseOrderByCreatedAtDesc(accountId);

        return notificationMapper.toDtoList(notifications);
    }

    @Override
    @Transactional
    public void markAsRead(UUID accessAccountId, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with ID: " + notificationId));

        // ********** KIỂM TRA QUYỀN SỞ HỮU **********
        if (!accountService.isAdmin(accessAccountId) && !notification.getAccount().getAccountId().equals(accessAccountId)) {
            throw new AccessDeniedException("You do not have permission to modify this notification.");
        }

        if (!notification.getIsRead()) {
            notification.setIsRead(true);
            notificationRepository.save(notification);
        }
    }

    @Override
    @Transactional
    public void markAllAsRead(UUID accountId) {
        notificationRepository.markAllAsReadByAccountId(accountId);

    }

    @Override
    @Transactional
    public void deleteNotification(Long notificationId) {

        var notification = notificationRepository.findById(notificationId)
                        .orElseThrow(()-> new ResourceNotFoundException("Notification not found with ID: " + notificationId));

        notificationRepository.delete(notification);
    }

    @Override
    @Transactional
    public void notifyVendorNewUserReview(UUID reviewId) {
        try {
            // 1. Lấy thông tin cần thiết: Review, Restaurant, Vendor
            Review review = reviewRepository.findById(reviewId)
                    .orElseThrow(() -> new ResourceNotFoundException("Review not found with ID: " + reviewId));

            Restaurant restaurant = review.getRestaurant();
            Account owerRestaurantAccount = restaurant.getOwnerAccount();
            UUID vendorAccountId = owerRestaurantAccount.getAccountId();
            String restaurantName = restaurant.getName();
            String emailReviewer = review.getReviewerAccount().getEmail();
            String reviewerName = review.getReviewerAccount().getFullName();
            if (reviewerName == null || reviewerName.isEmpty()) {
                reviewerName = NameUtil.getNameFromEmail(emailReviewer);
            }
            String reviewTitle = review.getComment();

            String vendorEmail = owerRestaurantAccount.getEmail();
            String emailSubject = "Bạn có Đánh giá mới cho nhà hàng " + restaurantName;
            // Sử dụng hàm build đã được đơn giản hóa
            String emailContent = buildVendorNewReviewContent(restaurantName, reviewerName);

            sendEmail(vendorEmail, emailSubject, emailContent);

            String title = "Đánh giá mới cho " + restaurantName;
            String message = reviewerName + " đã gửi đánh giá: \"" + reviewTitle + "\"";

            // --- ĐIỀU CHỈNH TARGET URL CHO VENDOR ---
            String restaurantIdString = restaurant.getRestaurantId().toString();
            String reviewIdString = reviewId.toString();

            // FE Route dẫn đến trang chi tiết review trong khu vực quản lý của Vendor
            String targetUrl = "/vendor/restaurants/" + restaurantIdString + "/reviews/" + reviewIdString;
            // ---------------------------------------

            AddNotificationRequest addNotificationRequest = new AddNotificationRequest(
                    vendorAccountId,
                    title,
                    message,
                    NotificationType.NEW_REVIEW,
                    targetUrl
            );
            createInAppNotification(addNotificationRequest);

        } catch (ResourceNotFoundException e) {
            LOGGER.warning("Không tìm thấy Review hoặc thông tin liên quan với ID: " + reviewId);
        } catch (MessagingException e) {
            LOGGER.severe("Lỗi gửi email thông báo Review mới: " + e.getMessage());
        }
    }

    private void sendEmail(String to, String subject, String content) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, true);

        mailSender.send(message);
    }

    private String buildWelcomeEmailContent(String username) {
        return "<html><body style='font-family: Arial, sans-serif;'><h2>Chào mừng, " + username + "!</h2><p>Tài khoản của bạn đã được tạo thành công.</p><a href='http://app.link/login'>Đăng nhập ngay</a></body></html>";
    }

    private String buildPasswordResetContent(String resetToken) {
        return "<html><body style='font-family: Arial, sans-serif;'><h2>Yêu cầu đặt lại mật khẩu</h2><p>Vui lòng nhấp vào liên kết sau để đặt lại mật khẩu:</p><a href='http://app.link/reset?token=" + resetToken + "'>Đặt lại mật khẩu</a></body></html>";
    }

    private String buildAdminSubmissionContent(UUID restaurantId) {
        return "<html><body style='font-family: Arial, sans-serif;'><h2>Nhà hàng mới cần xét duyệt</h2><p>Một Vendor đã gửi một nhà hàng mới có ID: " + restaurantId + ". Vui lòng kiểm tra trang quản trị.</p></body></html>";
    }

    private String buildVendorStatusUpdateContent(String restaurantName, ApprovalStatus newStatus, String rejectionReason) {
        String statusText = newStatus == ApprovalStatus.APPROVED ?
                "đã được phê duyệt!" :
                "đã bị từ chối. Lý do: " + rejectionReason;

        return "<html><body style='font-family: Arial, sans-serif;'><h2>Trạng thái Nhà hàng được Cập nhật</h2><p>Nhà hàng <b>" + restaurantName + "</b> của bạn " + statusText + "</p></body></html>";
    }

    private String buildSystemErrorContent(String errorTitle, String errorMessage) {
        return "<html><body style='font-family: Arial, sans-serif; color: red;'><h2>CẢNH BÁO LỖI HỆ THỐNG</h2><h3>" + errorTitle + "</h3><p>Chi tiết:</p><pre>" + errorMessage + "</pre></body></html>";
    }

    private String buildVendorNewReviewContent(String restaurantName, String reviewerName) {
        return "<html>" +
                "<body style='font-family: Arial, sans-serif;'>" +
                "<h2>🔔 Đánh Giá Mới Cho Nhà Hàng</h2>" +
                "<p>Nhà hàng <b>" + restaurantName + "</b> của bạn vừa nhận được một đánh giá mới.</p>" +
                "<p>Từ: <b>" + reviewerName + "</b></p>" +
                "<p>Vui lòng đăng nhập vào trang quản lý để xem chi tiết và phản hồi khách hàng.</p>" +
                "</body></html>";
    }

}

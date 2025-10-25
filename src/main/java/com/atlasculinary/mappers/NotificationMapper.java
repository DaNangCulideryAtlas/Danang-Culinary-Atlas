package com.atlasculinary.mappers;

import com.atlasculinary.dtos.AddNotificationRequest;
import com.atlasculinary.dtos.NotificationDto;
import com.atlasculinary.entities.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    @Mapping(source = "notificationId", target = "notificationId")
    @Mapping(source = "isRead", target = "isRead")
    NotificationDto toDto(Notification notification);
    List<NotificationDto> toDtoList(List<Notification> notificationList);

    Notification toEntity(AddNotificationRequest notificationRequest);
}

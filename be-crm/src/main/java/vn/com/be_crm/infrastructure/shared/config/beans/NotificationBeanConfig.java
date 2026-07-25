package vn.com.be_crm.infrastructure.shared.config.beans;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.com.be_crm.application.notification.command.CreateNotificationUseCase;
import vn.com.be_crm.application.notification.command.DeleteNotificationsUseCase;
import vn.com.be_crm.application.notification.command.MarkNotificationReadUseCase;
import vn.com.be_crm.application.notification.query.CountUnreadNotificationsUseCase;
import vn.com.be_crm.application.notification.query.ListMyNotificationsUseCase;
import vn.com.be_crm.application.shared.notify.NotifyAssignmentUseCase;
import vn.com.be_crm.application.shared.security.ICurrentUser;
import vn.com.be_crm.domain.notification.repository.INotificationRepository;

/**
 * Wire các UseCase của module Notification qua @Bean.
 */
@Configuration
public class NotificationBeanConfig {

    /** @return CreateNotificationUseCase */
    @Bean public CreateNotificationUseCase createNotificationUseCase(INotificationRepository r) { return new CreateNotificationUseCase(r); }
    /** @return ListMyNotificationsUseCase */
    @Bean public ListMyNotificationsUseCase listMyNotificationsUseCase(INotificationRepository r) { return new ListMyNotificationsUseCase(r); }
    /** @return CountUnreadNotificationsUseCase */
    @Bean public CountUnreadNotificationsUseCase countUnreadNotificationsUseCase(INotificationRepository r) { return new CountUnreadNotificationsUseCase(r); }
    /** @return MarkNotificationReadUseCase */
    @Bean public MarkNotificationReadUseCase markNotificationReadUseCase(INotificationRepository r) { return new MarkNotificationReadUseCase(r); }
    /** @return DeleteNotificationsUseCase */
    @Bean public DeleteNotificationsUseCase deleteNotificationsUseCase(INotificationRepository r) { return new DeleteNotificationsUseCase(r); }
    /** @return NotifyAssignmentUseCase */
    @Bean public NotifyAssignmentUseCase notifyAssignmentUseCase(CreateNotificationUseCase n, ICurrentUser cu) { return new NotifyAssignmentUseCase(n, cu); }
}

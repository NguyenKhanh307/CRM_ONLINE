package vn.com.be_crm.infrastructure.shared.config.beans;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.com.be_crm.application.auditlog.query.ListAuditLogUseCase;
import vn.com.be_crm.domain.auditlog.repository.IAuditLogRepository;

/**
 * Wire UseCase của module Nhật ký hệ thống (ADMIN, đọc-only) qua @Bean.
 */
@Configuration
public class AuditLogBeanConfig {

    /** @param repo port đọc nhật ký (gộp từ các bảng đã có sẵn) @return use case liệt kê nhật ký */
    @Bean
    public ListAuditLogUseCase listAuditLogUseCase(IAuditLogRepository repo) {
        return new ListAuditLogUseCase(repo);
    }
}

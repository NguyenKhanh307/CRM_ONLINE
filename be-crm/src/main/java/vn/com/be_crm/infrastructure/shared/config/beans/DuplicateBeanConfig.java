package vn.com.be_crm.infrastructure.shared.config.beans;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.com.be_crm.application.duplicate.query.CheckDuplicateUseCase;
import vn.com.be_crm.domain.duplicate.repository.IDuplicateRepository;

/**
 * Wire UseCase của module Duplicate (cảnh báo trùng email/SĐT/MST) qua @Bean.
 */
@Configuration
public class DuplicateBeanConfig {

    /** @param r port dò trùng @return CheckDuplicateUseCase */
    @Bean
    public CheckDuplicateUseCase checkDuplicateUseCase(IDuplicateRepository r) {
        return new CheckDuplicateUseCase(r);
    }
}

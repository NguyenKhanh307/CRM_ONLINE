package vn.com.be_crm.infrastructure.shared.config.beans;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.com.be_crm.application.related.query.GetCustomerRelatedUseCase;
import vn.com.be_crm.application.related.query.GetOpportunityRelatedUseCase;
import vn.com.be_crm.domain.customer.repository.ICustomerRepository;
import vn.com.be_crm.domain.opportunity.repository.IOpportunityRepository;
import vn.com.be_crm.domain.related.repository.IRelatedRepository;

/**
 * Wire các UseCase của module Related (trang chi tiết 360°) qua @Bean.
 */
@Configuration
public class RelatedBeanConfig {

    /** @param customerRepo port khách hàng @param relatedRepo port bản ghi liên quan @return use case 360° khách hàng */
    @Bean
    public GetCustomerRelatedUseCase getCustomerRelatedUseCase(ICustomerRepository customerRepo, IRelatedRepository relatedRepo) {
        return new GetCustomerRelatedUseCase(customerRepo, relatedRepo);
    }

    /** @param opportunityRepo port cơ hội @param relatedRepo port bản ghi liên quan @return use case 360° cơ hội */
    @Bean
    public GetOpportunityRelatedUseCase getOpportunityRelatedUseCase(IOpportunityRepository opportunityRepo, IRelatedRepository relatedRepo) {
        return new GetOpportunityRelatedUseCase(opportunityRepo, relatedRepo);
    }
}

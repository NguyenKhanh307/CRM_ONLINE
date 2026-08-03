package vn.com.be_crm.core.config.beans;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.com.be_crm.application.related.query.GetCampaignRelatedUseCase;
import vn.com.be_crm.application.related.query.GetContactRelatedUseCase;
import vn.com.be_crm.application.related.query.GetCustomerRelatedUseCase;
import vn.com.be_crm.application.related.query.GetInvoiceRelatedUseCase;
import vn.com.be_crm.application.related.query.GetLeadRelatedUseCase;
import vn.com.be_crm.application.related.query.GetOpportunityRelatedUseCase;
import vn.com.be_crm.application.related.query.GetOrderRelatedUseCase;
import vn.com.be_crm.application.related.query.GetQuotationRelatedUseCase;
import vn.com.be_crm.domain.campaign.repository.ICampaignRepository;
import vn.com.be_crm.domain.contact.repository.IContactRepository;
import vn.com.be_crm.domain.customer.repository.ICustomerRepository;
import vn.com.be_crm.domain.invoice.repository.IInvoiceRepository;
import vn.com.be_crm.domain.lead.repository.ILeadRepository;
import vn.com.be_crm.domain.opportunity.repository.IOpportunityRepository;
import vn.com.be_crm.domain.order.repository.IOrderRepository;
import vn.com.be_crm.domain.quotation.repository.IQuotationRepository;
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

    /** @param leadRepo port tiềm năng @param relatedRepo port bản ghi liên quan @return use case 360° tiềm năng */
    @Bean
    public GetLeadRelatedUseCase getLeadRelatedUseCase(ILeadRepository leadRepo, IRelatedRepository relatedRepo) {
        return new GetLeadRelatedUseCase(leadRepo, relatedRepo);
    }

    /** @param contactRepo port liên hệ @param relatedRepo port bản ghi liên quan @return use case 360° liên hệ */
    @Bean
    public GetContactRelatedUseCase getContactRelatedUseCase(IContactRepository contactRepo, IRelatedRepository relatedRepo) {
        return new GetContactRelatedUseCase(contactRepo, relatedRepo);
    }

    /** @param quotationRepo port báo giá @param relatedRepo port bản ghi liên quan @return use case 360° báo giá */
    @Bean
    public GetQuotationRelatedUseCase getQuotationRelatedUseCase(IQuotationRepository quotationRepo, IRelatedRepository relatedRepo) {
        return new GetQuotationRelatedUseCase(quotationRepo, relatedRepo);
    }

    /** @param orderRepo port đơn hàng @param relatedRepo port bản ghi liên quan @return use case 360° đơn hàng */
    @Bean
    public GetOrderRelatedUseCase getOrderRelatedUseCase(IOrderRepository orderRepo, IRelatedRepository relatedRepo) {
        return new GetOrderRelatedUseCase(orderRepo, relatedRepo);
    }

    /** @param invoiceRepo port hóa đơn @param relatedRepo port bản ghi liên quan @return use case 360° hóa đơn */
    @Bean
    public GetInvoiceRelatedUseCase getInvoiceRelatedUseCase(IInvoiceRepository invoiceRepo, IRelatedRepository relatedRepo) {
        return new GetInvoiceRelatedUseCase(invoiceRepo, relatedRepo);
    }

    /** @param campaignRepo port chiến dịch @param relatedRepo port bản ghi liên quan @return use case bản ghi quy về chiến dịch */
    @Bean
    public GetCampaignRelatedUseCase getCampaignRelatedUseCase(ICampaignRepository campaignRepo, IRelatedRepository relatedRepo) {
        return new GetCampaignRelatedUseCase(campaignRepo, relatedRepo);
    }
}

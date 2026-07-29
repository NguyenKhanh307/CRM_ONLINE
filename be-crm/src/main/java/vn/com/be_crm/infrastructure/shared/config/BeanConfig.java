package vn.com.be_crm.infrastructure.shared.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import vn.com.be_crm.infrastructure.shared.config.beans.ActivityBeanConfig;
import vn.com.be_crm.infrastructure.shared.config.beans.AuditLogBeanConfig;
import vn.com.be_crm.infrastructure.shared.config.beans.AuthBeanConfig;
import vn.com.be_crm.infrastructure.shared.config.beans.CampaignBeanConfig;
import vn.com.be_crm.infrastructure.shared.config.beans.ContactBeanConfig;
import vn.com.be_crm.infrastructure.shared.config.beans.CopilotBeanConfig;
import vn.com.be_crm.infrastructure.shared.config.beans.CustomerBeanConfig;
import vn.com.be_crm.infrastructure.shared.config.beans.DashboardBeanConfig;
import vn.com.be_crm.infrastructure.shared.config.beans.DuplicateBeanConfig;
import vn.com.be_crm.infrastructure.shared.config.beans.HandoverBeanConfig;
import vn.com.be_crm.infrastructure.shared.config.beans.LeadBeanConfig;
import vn.com.be_crm.infrastructure.shared.config.beans.NotificationBeanConfig;
import vn.com.be_crm.infrastructure.shared.config.beans.OpportunityBeanConfig;
import vn.com.be_crm.infrastructure.shared.config.beans.InvoiceBeanConfig;
import vn.com.be_crm.infrastructure.shared.config.beans.OrderBeanConfig;
import vn.com.be_crm.infrastructure.shared.config.beans.PricingBeanConfig;
import vn.com.be_crm.infrastructure.shared.config.beans.ProductBeanConfig;
import vn.com.be_crm.infrastructure.shared.config.beans.QuotationBeanConfig;
import vn.com.be_crm.infrastructure.shared.config.beans.RelatedBeanConfig;
import vn.com.be_crm.infrastructure.shared.config.beans.ServiceBeanConfig;

/**
 * Điểm gom cấu hình wire UseCase — mỗi module tách ra một @Configuration riêng trong package
 * {@code config.beans} để giữ mỗi file dưới 400 dòng. Thêm module mới: tạo *BeanConfig rồi khai báo ở @Import.
 */
@Configuration
@Import({
        AuthBeanConfig.class,
        CampaignBeanConfig.class,
        ActivityBeanConfig.class,
        ProductBeanConfig.class,
        OpportunityBeanConfig.class,
        CustomerBeanConfig.class,
        ContactBeanConfig.class,
        LeadBeanConfig.class,
        QuotationBeanConfig.class,
        ServiceBeanConfig.class,
        InvoiceBeanConfig.class,
        OrderBeanConfig.class,
        PricingBeanConfig.class,
        HandoverBeanConfig.class,
        NotificationBeanConfig.class,
        DashboardBeanConfig.class,
        CopilotBeanConfig.class,
        RelatedBeanConfig.class,
        DuplicateBeanConfig.class,
        AuditLogBeanConfig.class
})
public class BeanConfig {
}

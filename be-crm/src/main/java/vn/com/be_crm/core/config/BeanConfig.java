package vn.com.be_crm.core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import vn.com.be_crm.core.config.beans.ActivityBeanConfig;
import vn.com.be_crm.core.config.beans.AuditLogBeanConfig;
import vn.com.be_crm.core.config.beans.AuthBeanConfig;
import vn.com.be_crm.core.config.beans.CampaignBeanConfig;
import vn.com.be_crm.core.config.beans.ContactBeanConfig;
import vn.com.be_crm.core.config.beans.CopilotBeanConfig;
import vn.com.be_crm.core.config.beans.CustomerBeanConfig;
import vn.com.be_crm.core.config.beans.DashboardBeanConfig;
import vn.com.be_crm.core.config.beans.DuplicateBeanConfig;
import vn.com.be_crm.core.config.beans.HandoverBeanConfig;
import vn.com.be_crm.core.config.beans.LeadBeanConfig;
import vn.com.be_crm.core.config.beans.NotificationBeanConfig;
import vn.com.be_crm.core.config.beans.OpportunityBeanConfig;
import vn.com.be_crm.core.config.beans.InvoiceBeanConfig;
import vn.com.be_crm.core.config.beans.OrderBeanConfig;
import vn.com.be_crm.core.config.beans.PricingBeanConfig;
import vn.com.be_crm.core.config.beans.ProductBeanConfig;
import vn.com.be_crm.core.config.beans.QuotationBeanConfig;
import vn.com.be_crm.core.config.beans.RelatedBeanConfig;
import vn.com.be_crm.core.config.beans.ServiceBeanConfig;

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

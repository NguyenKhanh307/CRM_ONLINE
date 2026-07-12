package vn.com.be_crm.infrastructure.shared.config;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.hibernate.LocalSessionFactoryBuilder;
import vn.com.be_crm.infrastructure.shared.audit.AuditInterceptor;

import javax.sql.DataSource;

@Configuration
public class HibernateConfig {

    @Value("${hibernate.dialect:org.hibernate.dialect.MySQLDialect}")
    private String dialect;

    @Value("${hibernate.show_sql:false}")
    private String showSql;

    @Value("${hibernate.format_sql:false}")
    private String formatSql;

    @Value("${hibernate.hbm2ddl.auto:update}")
    private String hbm2ddl;

    /**
     * Tạo SessionFactory từ DataSource.
     * Scan toàn bộ package infrastructure để tìm @Entity.
     * Bean trung tâm của Hibernate Core — inject vào mọi RepositoryImpl.
     *
     * @param dataSource DataSource được Spring Boot tự cấu hình từ application.properties
     * @return SessionFactory dùng chung cho toàn bộ infrastructure layer
     */
    @Bean
    public SessionFactory sessionFactory(DataSource dataSource) {
        LocalSessionFactoryBuilder builder = new LocalSessionFactoryBuilder(dataSource);
        builder.scanPackages("vn.com.be_crm.infrastructure");
        builder.setProperty("hibernate.dialect", dialect);
        builder.setProperty("hibernate.show_sql", showSql);
        builder.setProperty("hibernate.format_sql", formatSql);
        builder.setProperty("hibernate.hbm2ddl.auto", hbm2ddl);
        builder.setProperty("hibernate.jdbc.time_zone", "Asia/Ho_Chi_Minh");
        // Đóng dấu created_by/updated_by tự động cho mọi IAuditable entity (không phải sửa 68 use case)
        builder.setInterceptor(new AuditInterceptor());
        return builder.buildSessionFactory();
    }
}

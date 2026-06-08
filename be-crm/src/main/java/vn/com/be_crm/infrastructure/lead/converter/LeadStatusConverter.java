package vn.com.be_crm.infrastructure.lead.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import vn.com.be_crm.domain.lead.enums.LeadStatus;

/**
 * Converter ánh xạ LeadStatus.new_ ↔ DB value "new" (Java keyword conflict).
 */
@Converter
public class LeadStatusConverter implements AttributeConverter<LeadStatus, String> {

    /** {@inheritDoc} */
    @Override
    public String convertToDatabaseColumn(LeadStatus attr) {
        if (attr == null) return null;
        return attr == LeadStatus.new_ ? "new" : attr.name();
    }

    /** {@inheritDoc} */
    @Override
    public LeadStatus convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        return LeadStatus.fromDb(dbData);
    }
}

package vn.com.be_crm.infrastructure.lead.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import vn.com.be_crm.domain.lead.enums.LeadStatus;

// ánh xạ LeadStatus.new_ <-> DB value "new" (new là từ khóa Java)
@Converter
public class LeadStatusConverter implements AttributeConverter<LeadStatus, String> {

    @Override
    public String convertToDatabaseColumn(LeadStatus attr) {
        if (attr == null) return null;
        return attr == LeadStatus.new_ ? "new" : attr.name();
    }

    @Override
    public LeadStatus convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        return LeadStatus.fromDb(dbData);
    }
}

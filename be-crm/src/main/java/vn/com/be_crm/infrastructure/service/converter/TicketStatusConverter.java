package vn.com.be_crm.infrastructure.service.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import vn.com.be_crm.domain.service.enums.TicketStatus;

/**
 * Converter ánh xạ TicketStatus.new_ ↔ DB value "new" (Java keyword conflict).
 */
@Converter
public class TicketStatusConverter implements AttributeConverter<TicketStatus, String> {

    /** {@inheritDoc} */
    @Override
    public String convertToDatabaseColumn(TicketStatus attr) {
        if (attr == null) return null;
        return attr == TicketStatus.new_ ? "new" : attr.name();
    }

    /** {@inheritDoc} */
    @Override
    public TicketStatus convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        return TicketStatus.fromDb(dbData);
    }
}

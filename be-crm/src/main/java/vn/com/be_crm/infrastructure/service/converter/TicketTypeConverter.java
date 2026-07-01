package vn.com.be_crm.infrastructure.service.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import vn.com.be_crm.domain.service.enums.TicketType;

/**
 * Converter ánh xạ TicketType.return_ ↔ DB value "return" (Java keyword conflict).
 */
@Converter
public class TicketTypeConverter implements AttributeConverter<TicketType, String> {

    /** {@inheritDoc} */
    @Override
    public String convertToDatabaseColumn(TicketType attr) {
        if (attr == null) return null;
        return attr == TicketType.return_ ? "return" : attr.name();
    }

    /** {@inheritDoc} */
    @Override
    public TicketType convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        return TicketType.fromDb(dbData);
    }
}

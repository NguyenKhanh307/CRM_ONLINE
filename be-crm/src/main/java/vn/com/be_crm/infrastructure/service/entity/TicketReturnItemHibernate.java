package vn.com.be_crm.infrastructure.service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.com.be_crm.domain.service.enums.ReturnReason;

import java.math.BigDecimal;

// ánh xạ bảng ticket_return_items
@Entity
@Table(name = "ticket_return_items")
@Getter @Setter @NoArgsConstructor
public class TicketReturnItemHibernate {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "ticket_id", nullable = false) private Long ticketId;
    @Column(name = "invoice_item_id") private Long invoiceItemId;
    @Column(name = "quantity", precision = 18, scale = 3) private BigDecimal quantity;
    @Enumerated(EnumType.STRING) @Column(name = "reason", length = 20) private ReturnReason reason;
    @Column(name = "condition_note", length = 255) private String conditionNote;
}

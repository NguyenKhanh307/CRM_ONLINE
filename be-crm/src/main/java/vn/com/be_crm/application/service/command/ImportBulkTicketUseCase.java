package vn.com.be_crm.application.service.command;

import vn.com.be_crm.application.service.dto.ImportBulkTicketCommand;
import vn.com.be_crm.application.service.dto.ImportTicketRowCommand;
import vn.com.be_crm.core.dto.import_.ImportBulkResult;
import vn.com.be_crm.core.dto.import_.ImportRowError;
import vn.com.be_crm.domain.service.entity.Ticket;
import vn.com.be_crm.domain.service.enums.ReturnReason;
import vn.com.be_crm.domain.service.enums.TicketChannel;
import vn.com.be_crm.domain.service.enums.TicketPriority;
import vn.com.be_crm.domain.service.enums.TicketStatus;
import vn.com.be_crm.domain.service.enums.TicketType;
import vn.com.be_crm.domain.service.repository.ITicketRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Use case nhập hàng loạt Ticket từ file Excel/CSV — chỉ ghi field header,
 * không đụng status/SLA/CSAT (các trường này chỉ đổi qua hành động workflow).
 */
public class ImportBulkTicketUseCase {
    private final ITicketRepository repo;

    /** @param repo port lưu trữ */
    public ImportBulkTicketUseCase(ITicketRepository repo) { this.repo = repo; }

    /**
     * Xử lý nhập hàng loạt Ticket.
     * @param cmd dữ liệu import @return kết quả nhập
     */
    public ImportBulkResult execute(ImportBulkTicketCommand cmd) {
        List<ImportRowError> errors = new ArrayList<>();
        int success = 0;
        for (int i = 0; i < cmd.rows().size(); i++) {
            int rowNum = i + 2;
            ImportTicketRowCommand row = cmd.rows().get(i);
            try {
                if (row.subject() == null || row.subject().isBlank()) {
                    errors.add(new ImportRowError(rowNum, "Trường 'Tiêu đề' là bắt buộc"));
                    continue;
                }
                Long assignedUserId = "SPECIFIC".equals(cmd.ownerMode()) ? cmd.specificOwnerId() : null;
                TicketType type = parseType(row.type());
                TicketChannel channel = parseEnum(TicketChannel.class, row.channel());
                TicketPriority priority = parseEnum(TicketPriority.class, row.priority());
                ReturnReason reason = parseEnum(ReturnReason.class, row.reason());

                boolean isUpdate = "UPDATE".equals(cmd.importType()) || "BOTH".equals(cmd.importType());
                boolean isCreate = "CREATE".equals(cmd.importType()) || "BOTH".equals(cmd.importType());

                Optional<Ticket> existing = Optional.empty();
                if (isUpdate && row.code() != null && !row.code().isBlank())
                    existing = repo.findByCode(row.code());

                if (existing.isPresent()) {
                    Ticket e = existing.get();
                    repo.save(e.toBuilder()
                            .type(type != null ? type : e.getType())
                            .subject(row.subject())
                            .description(row.description() != null ? row.description() : e.getDescription())
                            .customerId(row.customerId() != null ? row.customerId() : e.getCustomerId())
                            .contactId(row.contactId() != null ? row.contactId() : e.getContactId())
                            .invoiceId(row.invoiceId() != null ? row.invoiceId() : e.getInvoiceId())
                            .productId(row.productId() != null ? row.productId() : e.getProductId())
                            .channel(channel != null ? channel : e.getChannel())
                            .priority(priority != null ? priority : e.getPriority())
                            .reason(reason != null ? reason : e.getReason())
                            .assignedUserId(assignedUserId != null ? assignedUserId : e.getAssignedUserId())
                            .build());
                    success++;
                } else if (isCreate) {
                    String code = (row.code() != null && !row.code().isBlank())
                            ? row.code() : "PH-" + System.currentTimeMillis() + "-" + rowNum;
                    repo.save(Ticket.builder()
                            .code(code)
                            .type(type != null ? type : TicketType.support)
                            .subject(row.subject())
                            .description(row.description())
                            .customerId(row.customerId()).contactId(row.contactId())
                            .invoiceId(row.invoiceId()).productId(row.productId())
                            .channel(channel != null ? channel : TicketChannel.web)
                            .priority(priority != null ? priority : TicketPriority.medium)
                            .status(TicketStatus.new_)
                            .reason(reason)
                            .assignedUserId(assignedUserId)
                            .build());
                    success++;
                }
            } catch (Exception ex) {
                errors.add(new ImportRowError(rowNum, ex.getMessage() != null ? ex.getMessage() : "Lỗi không xác định"));
            }
        }
        return new ImportBulkResult(success, errors.size(), errors);
    }

    private TicketType parseType(String s) {
        if (s == null || s.isBlank()) return null;
        try { return TicketType.fromDb(s.trim().toLowerCase()); }
        catch (Exception e) { return null; }
    }

    private <E extends Enum<E>> E parseEnum(Class<E> cls, String val) {
        if (val == null || val.isBlank()) return null;
        try { return Enum.valueOf(cls, val.trim().toLowerCase()); }
        catch (Exception e) { return null; }
    }
}

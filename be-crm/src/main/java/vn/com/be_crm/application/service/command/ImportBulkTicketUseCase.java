package vn.com.be_crm.application.service.command;

import vn.com.be_crm.application.service.dto.ImportBulkTicketCommand;
import vn.com.be_crm.application.service.dto.ImportTicketRowCommand;
import vn.com.be_crm.core.dto.import_.ImportBulkResult;
import vn.com.be_crm.core.dto.import_.ImportRowError;
import vn.com.be_crm.core.tx.port.ITransactionRunner;
import vn.com.be_crm.domain.service.entity.Ticket;
import vn.com.be_crm.domain.service.enums.ReturnReason;
import vn.com.be_crm.domain.service.enums.TicketChannel;
import vn.com.be_crm.domain.service.enums.TicketPriority;
import vn.com.be_crm.domain.service.enums.TicketStatus;
import vn.com.be_crm.domain.service.enums.TicketType;
import vn.com.be_crm.domain.service.repository.ITicketRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Use case nhập hàng loạt Ticket từ file Excel/CSV — chỉ ghi field header,
 * không đụng status/SLA/CSAT (các trường này chỉ đổi qua hành động workflow). Validate TOÀN BỘ
 * trước (pass 1, không đụng DB), chỉ khi sạch hết mới lưu TOÀN BỘ trong 1 transaction (pass 2).
 */
public class ImportBulkTicketUseCase {
    private final ITicketRepository repo;
    private final ITransactionRunner txRunner;

    /** @param repo port lưu trữ @param txRunner chạy pass 2 trong 1 transaction */
    public ImportBulkTicketUseCase(ITicketRepository repo, ITransactionRunner txRunner) {
        this.repo = repo;
        this.txRunner = txRunner;
    }

    /**
     * Xử lý nhập hàng loạt Ticket.
     * @param cmd dữ liệu import @return kết quả nhập
     */
    public ImportBulkResult execute(ImportBulkTicketCommand cmd) {
        List<ImportRowError> errors = new ArrayList<>();
        Set<String> seenCodes = new HashSet<>();

        // ----- Pass 1: validate mọi dòng, không chạm DB (trừ check trùng mã đọc-thôi bên dưới) -----
        for (int i = 0; i < cmd.rows().size(); i++) {
            int rowNum = i + 2;
            ImportTicketRowCommand row = cmd.rows().get(i);
            if (row.subject() == null || row.subject().isBlank()) {
                errors.add(new ImportRowError(rowNum, "Trường 'Tiêu đề' là bắt buộc"));
                continue;
            }
            String err = null;
            if (row.type() != null && !row.type().isBlank() && parseType(row.type()) == null) {
                err = "Trường \"Loại phiếu\" giá trị không hợp lệ: \"" + row.type() + "\"";
            }
            if (err == null && row.channel() != null && !row.channel().isBlank() && parseEnum(TicketChannel.class, row.channel()) == null) {
                err = "Trường \"Kênh\" giá trị không hợp lệ: \"" + row.channel() + "\"";
            }
            if (err == null && row.priority() != null && !row.priority().isBlank() && parseEnum(TicketPriority.class, row.priority()) == null) {
                err = "Trường \"Độ ưu tiên\" giá trị không hợp lệ: \"" + row.priority() + "\"";
            }
            if (err == null && row.reason() != null && !row.reason().isBlank() && parseEnum(ReturnReason.class, row.reason()) == null) {
                err = "Trường \"Lý do\" giá trị không hợp lệ: \"" + row.reason() + "\"";
            }
            if (err != null) errors.add(new ImportRowError(rowNum, err));

            // check trùng mã khi tạo mới thuần — tránh crash cả file do vi phạm unique constraint ở Pass 2
            if ("CREATE".equals(cmd.importType()) && row.code() != null && !row.code().isBlank()) {
                String code = row.code().trim();
                if (!seenCodes.add(code)) {
                    errors.add(new ImportRowError(rowNum, "Mã \"" + code + "\" bị trùng với dòng khác trong file"));
                } else if (repo.findByCode(code).isPresent()) {
                    errors.add(new ImportRowError(rowNum, "Mã \"" + code + "\" đã tồn tại, vui lòng dùng mã khác hoặc chọn chế độ Cập nhật"));
                }
            }
        }
        if (!errors.isEmpty()) return new ImportBulkResult(0, errors.size(), errors);

        // ----- Pass 2: mọi dòng đã sạch — lưu toàn bộ trong 1 transaction -----
        int[] success = {0};
        try {
            txRunner.run(() -> {
                for (int i = 0; i < cmd.rows().size(); i++) {
                    int rowNum = i + 2;
                    ImportTicketRowCommand row = cmd.rows().get(i);
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
                                .orderId(row.orderId() != null ? row.orderId() : e.getOrderId())
                                .channel(channel != null ? channel : e.getChannel())
                                .priority(priority != null ? priority : e.getPriority())
                                .reason(reason != null ? reason : e.getReason())
                                .assignedUserId(assignedUserId != null ? assignedUserId : e.getAssignedUserId())
                                .build());
                        success[0]++;
                    } else if (isCreate) {
                        String code = (row.code() != null && !row.code().isBlank())
                                ? row.code() : "PH-" + System.currentTimeMillis() + "-" + rowNum;
                        repo.save(Ticket.builder()
                                .code(code)
                                .type(type != null ? type : TicketType.support)
                                .subject(row.subject())
                                .description(row.description())
                                .orderId(row.orderId())
                                .channel(channel != null ? channel : TicketChannel.web)
                                .priority(priority != null ? priority : TicketPriority.medium)
                                .status(TicketStatus.new_)
                                .reason(reason)
                                .assignedUserId(assignedUserId)
                                .build());
                        success[0]++;
                    }
                }
            });
        } catch (Exception ex) {
            return new ImportBulkResult(0, 1, List.of(new ImportRowError(0,
                    ex.getMessage() != null ? ex.getMessage() : "Lỗi không xác định khi lưu dữ liệu")));
        }
        return new ImportBulkResult(success[0], 0, List.of());
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

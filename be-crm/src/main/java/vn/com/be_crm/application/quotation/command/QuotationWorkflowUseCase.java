package vn.com.be_crm.application.quotation.command;

import vn.com.be_crm.application.notification.command.CreateNotificationUseCase;
import vn.com.be_crm.application.quotation.dto.QuotationEmailDraft;
import vn.com.be_crm.application.quotation.dto.QuotationResult;
import vn.com.be_crm.application.quotation.dto.SendQuotationCommand;
import vn.com.be_crm.application.quotation.email.QuotationEmailComposer;
import vn.com.be_crm.application.quotation.mapper.QuotationCommandMapper;
import vn.com.be_crm.application.quotation.pdf.QuotationPdfDataBuilder;
import vn.com.be_crm.core.email.port.IEmailService;
import vn.com.be_crm.core.pdf.port.IQuotationPdfService;
import vn.com.be_crm.core.notify.port.IManagerResolver;
import vn.com.be_crm.domain.contact.repository.IContactRepository;
import vn.com.be_crm.domain.quotation.entity.Quotation;
import vn.com.be_crm.domain.quotation.entity.QuotationApproval;
import vn.com.be_crm.domain.quotation.enums.QuotationApprovalStatus;
import vn.com.be_crm.domain.quotation.enums.QuotationStatus;
import vn.com.be_crm.domain.quotation.repository.IQuotationApprovalRepository;
import vn.com.be_crm.domain.quotation.repository.IQuotationRepository;
import vn.com.be_crm.core.error.frontend.DomainException;
import vn.com.be_crm.core.error.frontend.NotFoundException;
import vn.com.be_crm.core.tx.port.ITransactionRunner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// điều phối luồng trạng thái báo giá (theo hành động, không sửa tay):
// draft -> (gửi duyệt) pending -> (duyệt) approved / (từ chối) draft -> (gửi mail) sent
public class QuotationWorkflowUseCase {

    private final IQuotationRepository quotationRepo;
    private final IQuotationApprovalRepository approvalRepo;
    private final CreateNotificationUseCase createNotificationUC;
    private final IManagerResolver managerResolver;
    private final IEmailService emailService;
    private final IContactRepository contactRepo;
    private final IQuotationPdfService pdfService;
    private final QuotationPdfDataBuilder pdfDataBuilder;
    private final QuotationEmailComposer emailComposer;
    private final String frontendBaseUrl;
    private final ITransactionRunner tx;

    public QuotationWorkflowUseCase(IQuotationRepository quotationRepo, IQuotationApprovalRepository approvalRepo,
                                    CreateNotificationUseCase createNotificationUC, IManagerResolver managerResolver,
                                    IEmailService emailService, IContactRepository contactRepo,
                                    IQuotationPdfService pdfService, QuotationPdfDataBuilder pdfDataBuilder,
                                    QuotationEmailComposer emailComposer, String frontendBaseUrl,
                                    ITransactionRunner tx) {
        this.quotationRepo = quotationRepo;
        this.approvalRepo = approvalRepo;
        this.createNotificationUC = createNotificationUC;
        this.managerResolver = managerResolver;
        this.emailService = emailService;
        this.contactRepo = contactRepo;
        this.pdfService = pdfService;
        this.pdfDataBuilder = pdfDataBuilder;
        this.emailComposer = emailComposer;
        this.frontendBaseUrl = frontendBaseUrl;
        this.tx = tx;
    }

    // nhân viên gửi báo giá lên quản lý duyệt: draft -> pending, tạo bước duyệt + thông báo quản
    // lý. Đổi trạng thái + bước duyệt + thông báo chạy trong MỘT transaction
    public QuotationResult submit(Long quotationId, Long userId) {
        return tx.call(() -> {
            Quotation q = load(quotationId);
            q.getStatus().ensureCanTransitionTo(QuotationStatus.pending);
            Quotation saved = quotationRepo.save(q.toBuilder().status(QuotationStatus.pending).build());

            approvalRepo.save(QuotationApproval.builder()
                    .quotationId(quotationId).status(QuotationApprovalStatus.pending).build());

            // chỉ báo cho quản lý trực tiếp của người phụ trách báo giá (không broadcast mọi ADMIN/quản lý)
            List<Long> recipients = new ArrayList<>(managerResolver.managersOf(saved.getOwnerId()));
            createNotificationUC.execute(recipients, "quotation_pending",
                    "Báo giá chờ duyệt: " + saved.getCode(),
                    "Báo giá " + saved.getCode() + " đang chờ bạn phê duyệt.", "quotation", saved.getId());
            return QuotationCommandMapper.toResult(saved);
        });
    }

    // quản lý duyệt báo giá: pending -> approved, ghi nhận bước duyệt + thông báo người phụ trách
    public QuotationResult approve(Long quotationId, Long approverId, String comment) {
        return tx.call(() -> {
            Quotation q = load(quotationId);
            q.getStatus().ensureCanTransitionTo(QuotationStatus.approved);
            Quotation saved = quotationRepo.save(q.toBuilder().status(QuotationStatus.approved).build());

            finalizePendingApproval(quotationId, approverId, QuotationApprovalStatus.approved, comment);
            notifyOwner(saved, "quotation_approved", "Báo giá đã được duyệt: " + saved.getCode(),
                    "Báo giá " + saved.getCode() + " đã được phê duyệt. Bạn có thể gửi cho khách hàng.");
            return QuotationCommandMapper.toResult(saved);
        });
    }

    // quản lý từ chối báo giá: pending -> draft (quay lại nháp để sửa), ghi lý do + thông báo
    // người phụ trách
    public QuotationResult reject(Long quotationId, Long approverId, String reason) {
        return tx.call(() -> {
            Quotation q = load(quotationId);
            q.getStatus().ensureCanTransitionTo(QuotationStatus.draft);
            Quotation saved = quotationRepo.save(q.toBuilder().status(QuotationStatus.draft).build());

            finalizePendingApproval(quotationId, approverId, QuotationApprovalStatus.rejected, reason);
            notifyOwner(saved, "quotation_rejected", "Báo giá bị từ chối: " + saved.getCode(),
                    "Báo giá " + saved.getCode() + " bị từ chối. Lý do: " + (reason != null ? reason : "(không có)"));
            return QuotationCommandMapper.toResult(saved);
        });
    }

    // nhân viên gửi email báo giá cho khách hàng: approved -> sent. Dựng PDF bảng báo giá đính
    // kèm + link 3 nút phản hồi công khai (dùng thẳng mã báo giá, không còn token bí mật). Tiêu
    // đề/nội dung do người dùng soạn; để trống thì dùng mặc định từ QuotationEmailComposer.
    // CỐ Ý không bọc transaction: có I/O ngoài (render PDF + gửi SMTP) chậm, không nên giữ
    // transaction DB mở; hơn nữa chỉ có MỘT lệnh ghi (save ở cuối) nên vốn đã nguyên tử.
    public QuotationResult send(SendQuotationCommand cmd) {
        Quotation q = load(cmd.getId());
        q.getStatus().ensureCanTransitionTo(QuotationStatus.sent);

        QuotationEmailDraft draft = emailComposer.draft(q);

        // người nhận: ưu tiên địa chỉ người dùng sửa tay, không có thì lấy email của liên hệ/khách hàng
        String to = (cmd.getTo() != null && !cmd.getTo().isBlank()) ? cmd.getTo().trim() : draft.toEmail();
        if (to == null || to.isBlank()) {
            throw new DomainException("Báo giá chưa có email khách hàng/liên hệ để gửi");
        }
        requireValidEmails(to);

        String finalSubject = (cmd.getSubject() != null && !cmd.getSubject().isBlank()) ? cmd.getSubject() : draft.subject();
        String finalBody = (cmd.getBody() != null && !cmd.getBody().isBlank()) ? cmd.getBody() : draft.body();

        String responseLink = trimTrailingSlash(frontendBaseUrl) + "/bao-gia-phan-hoi/" + q.getCode();
        byte[] pdf = pdfService.render(pdfDataBuilder.build(q, draft.recipientName()));

        emailService.sendQuotationEmail(to, finalSubject, finalBody,
                responseLink, pdf, "BaoGia-" + q.getCode() + ".pdf");

        Quotation saved = quotationRepo.save(q.toBuilder().status(QuotationStatus.sent).build());
        // trả kèm email đã gửi để FE hiển thị xác nhận "đã gửi tới <email>"
        return QuotationCommandMapper.toResult(saved).toBuilder().sentToEmail(to).build();
    }

    // đánh dấu báo giá đã gửi mà KHÔNG gửi email: approved -> sent. Dành cho trường hợp gửi ngoài
    // hệ thống (Zalo, in giấy, gặp trực tiếp) — trước đây báo giá kẹt vĩnh viễn ở approved vì lối
    // thoát duy nhất là gửi SMTP.
    public QuotationResult markSent(Long quotationId) {
        Quotation q = load(quotationId);
        q.getStatus().ensureCanTransitionTo(QuotationStatus.sent);
        Quotation saved = quotationRepo.save(q.toBuilder().status(QuotationStatus.sent).build());
        return QuotationCommandMapper.toResult(saved);
    }

    // kiểm định dạng một địa chỉ email, sai thì ném DomainException (HTTP 400)
    private void requireValidEmails(String email) {
        if (!email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new DomainException("Email không hợp lệ: " + email);
        }
    }

    // bỏ dấu '/' cuối URL để ghép path
    private String trimTrailingSlash(String url) {
        if (url == null) return "";
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    // khách hàng chấp nhận báo giá: sent -> accepted. KHÔNG tự sinh Đơn hàng (đã tách riêng
    // 2026-08 — muốn tạo đơn phải bấm "Chuyển sang đơn hàng" thủ công qua convertToOrder)
    public QuotationResult accept(Long quotationId) {
        return tx.call(() -> {
            Quotation q = load(quotationId);
            q.getStatus().ensureCanTransitionTo(QuotationStatus.accepted);
            Quotation saved = quotationRepo.save(q.toBuilder().status(QuotationStatus.accepted).build());
            notifyOwner(saved, "quotation_accepted", "Báo giá được chấp nhận: " + saved.getCode(),
                    "Báo giá " + saved.getCode() + " đã được khách hàng chấp nhận.");
            return QuotationCommandMapper.toResult(saved);
        });
    }

    // mở lại khi lỡ bấm nhầm chấp nhận (accepted -> sent). Chặn nếu đã bấm "Chuyển sang đơn hàng"
    // thủ công (isLocked=true) — không thể mở lại báo giá đã phát sinh đơn hàng
    public QuotationResult reopen(Long quotationId) {
        return tx.call(() -> {
            Quotation q = load(quotationId);
            if (q.isLocked()) {
                throw new DomainException("Báo giá đã khóa (đã chuyển sang đơn hàng), không thể mở lại");
            }
            q.getStatus().ensureCanTransitionTo(QuotationStatus.sent);
            Quotation saved = quotationRepo.save(q.toBuilder().status(QuotationStatus.sent).build());
            return QuotationCommandMapper.toResult(saved);
        });
    }

    private Quotation load(Long id) {
        return quotationRepo.findById(id).orElseThrow(() -> new NotFoundException("Quotation not found: " + id));
    }

    // cập nhật bước duyệt pending gần nhất thành approved/rejected, ghi người duyệt + thời điểm
    private void finalizePendingApproval(Long quotationId, Long approverId, QuotationApprovalStatus status, String comment) {
        approvalRepo.findAllByQuotationId(quotationId).stream()
                .filter(a -> a.getStatus() == QuotationApprovalStatus.pending)
                .reduce((first, second) -> second)   // bước pending mới nhất
                .ifPresent(a -> approvalRepo.save(a.toBuilder()
                        .status(status).approverId(approverId).comment(comment)
                        .approvedAt(LocalDateTime.now()).build()));
    }

    // gửi thông báo cho người phụ trách báo giá (nếu có owner)
    private void notifyOwner(Quotation q, String type, String title, String content) {
        if (q.getOwnerId() == null) return;
        createNotificationUC.execute(List.of(q.getOwnerId()), type, title, content, "quotation", q.getId());
    }
}

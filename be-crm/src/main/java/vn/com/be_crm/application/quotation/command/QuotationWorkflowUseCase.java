package vn.com.be_crm.application.quotation.command;

import vn.com.be_crm.application.notification.command.CreateNotificationUseCase;
import vn.com.be_crm.application.quotation.dto.QuotationEmailDraft;
import vn.com.be_crm.application.quotation.dto.QuotationResult;
import vn.com.be_crm.application.quotation.email.QuotationEmailComposer;
import vn.com.be_crm.application.quotation.mapper.QuotationCommandMapper;
import vn.com.be_crm.application.shared.email.IEmailService;
import vn.com.be_crm.application.shared.pdf.IQuotationPdfService;
import vn.com.be_crm.application.shared.pdf.QuotationPdfData;
import vn.com.be_crm.domain.auth.repository.IUserRoleRepository;
import vn.com.be_crm.domain.contact.entity.Contact;
import vn.com.be_crm.domain.contact.repository.IContactRepository;
import vn.com.be_crm.domain.customer.entity.Customer;
import vn.com.be_crm.domain.customer.repository.ICustomerRepository;
import vn.com.be_crm.domain.product.entity.Product;
import vn.com.be_crm.domain.product.repository.IProductRepository;
import vn.com.be_crm.domain.quotation.entity.Quotation;
import vn.com.be_crm.domain.quotation.entity.QuotationApproval;
import vn.com.be_crm.domain.quotation.entity.QuotationItem;
import vn.com.be_crm.domain.quotation.enums.QuotationApprovalStatus;
import vn.com.be_crm.domain.quotation.enums.QuotationStatus;
import vn.com.be_crm.domain.quotation.repository.IQuotationApprovalRepository;
import vn.com.be_crm.domain.quotation.repository.IQuotationItemRepository;
import vn.com.be_crm.domain.quotation.repository.IQuotationRepository;
import vn.com.be_crm.domain.shared.exception.DomainException;
import vn.com.be_crm.domain.shared.exception.NotFoundException;
import vn.com.be_crm.application.shared.tx.ITransactionRunner;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Use case điều phối luồng trạng thái báo giá (theo hành động, không sửa tay):
 * draft → (gửi duyệt) pending → (duyệt) approved / (từ chối) draft → (gửi mail) sent.
 */
public class QuotationWorkflowUseCase {
    /** Các role nhận thông báo có báo giá chờ duyệt. */
    private static final List<String> MANAGER_ROLES = List.of("ADMIN", "SALES_MANAGER");

    private final IQuotationRepository quotationRepo;
    private final IQuotationApprovalRepository approvalRepo;
    private final CreateNotificationUseCase createNotificationUC;
    private final IUserRoleRepository userRoleRepo;
    private final IEmailService emailService;
    private final ICustomerRepository customerRepo;
    private final IContactRepository contactRepo;
    private final IQuotationItemRepository quotationItemRepo;
    private final IProductRepository productRepo;
    private final IQuotationPdfService pdfService;
    private final QuotationEmailComposer emailComposer;
    private final String frontendBaseUrl;
    private final ITransactionRunner tx;

    /** @param quotationRepo báo giá @param approvalRepo bước duyệt @param createNotificationUC tạo thông báo
     *  @param userRoleRepo tra cứu role @param emailService gửi email @param customerRepo khách hàng @param contactRepo liên hệ
     *  @param quotationItemRepo dòng hàng báo giá @param productRepo hàng hóa @param pdfService sinh PDF
     *  @param emailComposer dựng nội dung email mặc định + resolve người nhận @param frontendBaseUrl URL FE cho link phản hồi
     *  @param tx bộ chạy transaction */
    public QuotationWorkflowUseCase(IQuotationRepository quotationRepo, IQuotationApprovalRepository approvalRepo,
                                    CreateNotificationUseCase createNotificationUC, IUserRoleRepository userRoleRepo,
                                    IEmailService emailService, ICustomerRepository customerRepo,
                                    IContactRepository contactRepo, IQuotationItemRepository quotationItemRepo,
                                    IProductRepository productRepo, IQuotationPdfService pdfService,
                                    QuotationEmailComposer emailComposer, String frontendBaseUrl,
                                    ITransactionRunner tx) {
        this.quotationRepo = quotationRepo;
        this.approvalRepo = approvalRepo;
        this.createNotificationUC = createNotificationUC;
        this.userRoleRepo = userRoleRepo;
        this.emailService = emailService;
        this.customerRepo = customerRepo;
        this.contactRepo = contactRepo;
        this.quotationItemRepo = quotationItemRepo;
        this.productRepo = productRepo;
        this.pdfService = pdfService;
        this.emailComposer = emailComposer;
        this.frontendBaseUrl = frontendBaseUrl;
        this.tx = tx;
    }

    /**
     * Nhân viên gửi báo giá lên quản lý duyệt: draft → pending, tạo bước duyệt + thông báo quản lý.
     * Đổi trạng thái + bước duyệt + thông báo chạy trong MỘT transaction.
     * @param quotationId ID báo giá @param userId người gửi @return báo giá sau cập nhật
     */
    public QuotationResult submit(Long quotationId, Long userId) {
        return tx.call(() -> {
            Quotation q = load(quotationId);
            q.getStatus().ensureCanTransitionTo(QuotationStatus.pending);
            Quotation saved = quotationRepo.save(q.toBuilder().status(QuotationStatus.pending).build());

            approvalRepo.save(QuotationApproval.builder()
                    .quotationId(quotationId).level(1).status(QuotationApprovalStatus.pending).build());

            List<Long> recipients = new ArrayList<>(userRoleRepo.findUserIdsByRoleCodes(MANAGER_ROLES));
            createNotificationUC.execute(recipients, "quotation_pending",
                    "Báo giá chờ duyệt: " + saved.getCode(),
                    "Báo giá " + saved.getCode() + " đang chờ bạn phê duyệt.", null, saved.getId());
            return QuotationCommandMapper.toResult(saved);
        });
    }

    /**
     * Quản lý duyệt báo giá: pending → approved, ghi nhận bước duyệt + thông báo người phụ trách.
     * @param quotationId ID báo giá @param approverId người duyệt @param comment ý kiến (có thể null)
     * @return báo giá sau cập nhật
     */
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

    /**
     * Quản lý từ chối báo giá: pending → draft (quay lại nháp để sửa), ghi lý do + thông báo người phụ trách.
     * @param quotationId ID báo giá @param approverId người từ chối @param reason lý do từ chối
     * @return báo giá sau cập nhật
     */
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

    /**
     * Nhân viên gửi email báo giá cho khách hàng: approved → sent.
     * Sinh token phản hồi (nếu chưa có), dựng PDF bảng báo giá đính kèm + link 3 nút phản hồi công khai.
     * Tiêu đề/nội dung do người dùng soạn; nếu để trống thì dùng mặc định từ {@link QuotationEmailComposer}.
     * CỐ Ý không bọc transaction: có I/O ngoài (render PDF + gửi SMTP) chậm, không nên giữ transaction DB mở;
     * hơn nữa chỉ có MỘT lệnh ghi (save ở cuối) nên vốn đã nguyên tử.
     * @param quotationId ID báo giá @param subject tiêu đề tùy biến (có thể null/blank) @param body nội dung tùy biến (có thể null/blank)
     * @return báo giá sau cập nhật
     */
    public QuotationResult send(Long quotationId, String subject, String body) {
        Quotation q = load(quotationId);
        q.getStatus().ensureCanTransitionTo(QuotationStatus.sent);

        QuotationEmailDraft draft = emailComposer.draft(q);
        if (draft.toEmail() == null || draft.toEmail().isBlank()) {
            throw new DomainException("Báo giá chưa có email khách hàng/liên hệ để gửi");
        }

        String finalSubject = (subject != null && !subject.isBlank()) ? subject : draft.subject();
        String finalBody = (body != null && !body.isBlank()) ? body : draft.body();

        String token = (q.getResponseToken() != null && !q.getResponseToken().isBlank())
                ? q.getResponseToken() : UUID.randomUUID().toString().replace("-", "");
        String responseLink = trimTrailingSlash(frontendBaseUrl) + "/bao-gia-phan-hoi/" + token;
        byte[] pdf = pdfService.render(buildPdfData(q, draft.recipientName()));

        emailService.sendQuotationEmail(draft.toEmail(), finalSubject, finalBody,
                responseLink, pdf, "BaoGia-" + q.getCode() + ".pdf");

        Quotation saved = quotationRepo.save(q.toBuilder().status(QuotationStatus.sent).responseToken(token).build());
        // Trả kèm email đã gửi để FE hiển thị xác nhận "đã gửi tới <email>"
        return QuotationCommandMapper.toResult(saved).toBuilder().sentToEmail(draft.toEmail()).build();
    }

    /** Dựng dữ liệu PDF từ báo giá: tên KH, dòng hàng (kèm tên sản phẩm), tổng tiền. */
    private QuotationPdfData buildPdfData(Quotation q, String contactName) {
        String customerName = q.getCustomerId() == null ? "" :
                customerRepo.findById(q.getCustomerId()).map(Customer::getName).orElse("");
        List<QuotationPdfData.Line> lines = new ArrayList<>();
        int stt = 1;
        for (QuotationItem it : quotationItemRepo.findAllByQuotationId(q.getId())) {
            String productName = it.getProductId() == null ? "" :
                    productRepo.findById(it.getProductId()).map(Product::getName).orElse("#" + it.getProductId());
            lines.add(new QuotationPdfData.Line(stt++, productName, it.getUnit(),
                    it.getQuantity(), it.getUnitPrice(), it.getDiscount(), it.getAmount()));
        }
        return new QuotationPdfData(q.getCode(), customerName, contactName,
                q.getQuoteDate(), q.getValidUntil(), q.getCurrency(), q.getNote(), q.getTotal(), lines);
    }

    /** Bỏ dấu '/' cuối URL để ghép path. */
    private String trimTrailingSlash(String url) {
        if (url == null) return "";
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    /**
     * Khách hàng chấp nhận báo giá: sent → accepted. Sau bước này có thể chuyển thành hóa đơn.
     * @param quotationId ID báo giá @return báo giá sau cập nhật
     */
    public QuotationResult accept(Long quotationId) {
        return tx.call(() -> {
            Quotation q = load(quotationId);
            q.getStatus().ensureCanTransitionTo(QuotationStatus.accepted);
            Quotation saved = quotationRepo.save(q.toBuilder().status(QuotationStatus.accepted).build());
            notifyOwner(saved, "quotation_accepted", "Báo giá được chấp nhận: " + saved.getCode(),
                    "Báo giá " + saved.getCode() + " đã được khách hàng chấp nhận. Bạn có thể chuyển thành hóa đơn.");
            return QuotationCommandMapper.toResult(saved);
        });
    }

    /** Tải báo giá theo ID hoặc ném NotFoundException. */
    private Quotation load(Long id) {
        return quotationRepo.findById(id).orElseThrow(() -> new NotFoundException("Quotation not found: " + id));
    }

    /** Cập nhật bước duyệt pending gần nhất thành approved/rejected, ghi người duyệt + thời điểm. */
    private void finalizePendingApproval(Long quotationId, Long approverId, QuotationApprovalStatus status, String comment) {
        approvalRepo.findAllByQuotationId(quotationId).stream()
                .filter(a -> a.getStatus() == QuotationApprovalStatus.pending)
                .reduce((first, second) -> second)   // bước pending mới nhất
                .ifPresent(a -> approvalRepo.save(a.toBuilder()
                        .status(status).approverId(approverId).comment(comment)
                        .approvedAt(LocalDateTime.now()).build()));
    }

    /** Gửi thông báo cho người phụ trách báo giá (nếu có owner). */
    private void notifyOwner(Quotation q, String type, String title, String content) {
        if (q.getOwnerId() == null) return;
        createNotificationUC.execute(List.of(q.getOwnerId()), type, title, content, null, q.getId());
    }
}

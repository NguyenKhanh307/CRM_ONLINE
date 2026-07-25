package vn.com.be_crm.application.lead.command;

import vn.com.be_crm.application.lead.dto.LeadResult;
import vn.com.be_crm.application.lead.mapper.LeadCommandMapper;
import vn.com.be_crm.domain.lead.entity.Lead;
import vn.com.be_crm.domain.lead.enums.LeadStatus;
import vn.com.be_crm.domain.lead.repository.ILeadRepository;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Use case xử lý lượt truy cập web tracking: nếu đã có mã thì trả về trạng thái hiện tại,
 * ngược lại tạo tiềm năng ẩn danh mới với mã dạng TNW... và điểm = 0.
 */
public class TrackVisitUseCase {
    private static final String CODE_PREFIX = "TNW";
    private static final char[] BASE36 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private static final int SUFFIX_LEN = 6;

    private final ILeadRepository repo;

    /** @param repo port lưu trữ Lead */
    public TrackVisitUseCase(ILeadRepository repo) { this.repo = repo; }

    /**
     * Trả về tiềm năng theo mã nếu tồn tại, ngược lại tạo mới tiềm năng ẩn danh.
     *
     * <p><b>Attribution first-touch</b>: chiến dịch chỉ được gắn khi tiềm năng chưa có chiến dịch nào.
     * Khách quay lại qua link của chiến dịch khác thì nguồn ban đầu vẫn giữ — nếu ghi đè, công
     * sinh ra tiềm năng sẽ bị tính nhầm cho chiến dịch chạm sau cùng.</p>
     *
     * @param code       mã TNW... từ cookie/localStorage máy khách (có thể null/rỗng)
     * @param campaignId chiến dịch nguồn từ tham số `utm_campaign` của landing page (có thể null)
     * @return LeadResult của tiềm năng tương ứng
     */
    public LeadResult execute(String code, Long campaignId) {
        if (code != null && !code.isBlank()) {
            Lead existing = repo.findByCode(code.trim()).orElse(null);
            if (existing != null) {
                if (campaignId != null && existing.getCampaignId() == null) {
                    return LeadCommandMapper.toResult(repo.save(existing.toBuilder().campaignId(campaignId).build()));
                }
                return LeadCommandMapper.toResult(existing);
            }
        }
        String newCode = generateCode();
        Lead lead = Lead.builder()
                .code(newCode).name("Khách web " + newCode)
                .source("web").campaignId(campaignId).status(LeadStatus.new_).score(0)
                .doNotCall(false).doNotEmail(false).build();
        return LeadCommandMapper.toResult(repo.save(lead));
    }

    /**
     * Sinh mã TNW với hậu tố ngẫu nhiên base36 (vd TNW7K2P9Q) — tránh va chạm khi nhiều
     * khách truy cập song song (mã tuần tự dễ trùng do giao dịch chưa commit). Vẫn kiểm tra
     * findByCode để bảo đảm duy nhất.
     */
    private String generateCode() {
        for (int attempt = 0; attempt < 20; attempt++) {
            String code = CODE_PREFIX + randomSuffix();
            if (repo.findByCode(code).isEmpty()) return code;
        }
        // Cực hiếm: thêm timestamp để chắc chắn duy nhất
        return CODE_PREFIX + randomSuffix() + Long.toString(System.currentTimeMillis(), 36).toUpperCase();
    }

    /** Sinh chuỗi base36 ngẫu nhiên độ dài SUFFIX_LEN. */
    private String randomSuffix() {
        StringBuilder sb = new StringBuilder(SUFFIX_LEN);
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        for (int i = 0; i < SUFFIX_LEN; i++) sb.append(BASE36[rnd.nextInt(BASE36.length)]);
        return sb.toString();
    }
}

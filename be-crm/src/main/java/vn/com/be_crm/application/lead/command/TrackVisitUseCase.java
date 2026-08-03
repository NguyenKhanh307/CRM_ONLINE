package vn.com.be_crm.application.lead.command;

import vn.com.be_crm.application.lead.dto.LeadResult;
import vn.com.be_crm.application.lead.mapper.LeadCommandMapper;
import vn.com.be_crm.domain.lead.entity.Lead;
import vn.com.be_crm.domain.lead.enums.LeadStatus;
import vn.com.be_crm.domain.lead.repository.ILeadRepository;

import java.util.concurrent.ThreadLocalRandom;

// xử lý lượt truy cập web tracking: đã có mã thì trả về trạng thái hiện tại, ngược lại tạo
// tiềm năng ẩn danh mới với mã dạng TNW... và điểm = 0
public class TrackVisitUseCase {
    private static final String CODE_PREFIX = "TNW";
    private static final char[] BASE36 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private static final int SUFFIX_LEN = 6;

    private final ILeadRepository repo;

    public TrackVisitUseCase(ILeadRepository repo) { this.repo = repo; }

    // code: mã TNW... từ cookie/localStorage máy khách (có thể null/rỗng)
    // campaignId: chiến dịch nguồn từ tham số utm_campaign của landing page (có thể null)
    // attribution first-touch: chiến dịch chỉ gắn khi tiềm năng CHƯA có chiến dịch nào — khách
    // quay lại qua link chiến dịch khác thì nguồn ban đầu vẫn giữ, ghi đè sẽ tính nhầm công sinh
    // ra tiềm năng cho chiến dịch chạm sau cùng
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

    // hậu tố ngẫu nhiên base36 (vd TNW7K2P9Q) — tránh va chạm khi nhiều khách truy cập song song
    // (mã tuần tự dễ trùng do giao dịch chưa commit); vẫn kiểm tra findByCode để bảo đảm duy nhất
    private String generateCode() {
        for (int attempt = 0; attempt < 20; attempt++) {
            String code = CODE_PREFIX + randomSuffix();
            if (repo.findByCode(code).isEmpty()) return code;
        }
        // cực hiếm: thêm timestamp để chắc chắn duy nhất
        return CODE_PREFIX + randomSuffix() + Long.toString(System.currentTimeMillis(), 36).toUpperCase();
    }

    private String randomSuffix() {
        StringBuilder sb = new StringBuilder(SUFFIX_LEN);
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        for (int i = 0; i < SUFFIX_LEN; i++) sb.append(BASE36[rnd.nextInt(BASE36.length)]);
        return sb.toString();
    }
}

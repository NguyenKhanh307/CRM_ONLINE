package vn.com.be_crm.application.dashboard.dto;

import java.math.BigDecimal;

/**
 * Một bậc của phễu chuyển đổi cơ hội theo giai đoạn.
 *
 * @param label      tên giai đoạn
 * @param count      số cơ hội ở giai đoạn
 * @param pctOfFirst tỉ lệ phần trăm so với giai đoạn đầu phễu (0–100)
 */
public record FunnelStage(String label, long count, BigDecimal pctOfFirst) {
}

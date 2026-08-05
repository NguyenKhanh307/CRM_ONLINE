package vn.com.be_crm.application.lead.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

// input khi khách bấm "Yêu cầu báo giá" trên landing page công khai — vừa cập nhật thông tin
// liên hệ của tiềm năng vừa ghi nhận các dòng sản phẩm quan tâm (lead_items)
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class RequestProductQuoteCommand {
    private String code;
    private String name;
    private String companyName;
    private String email;
    private String phone;
    private String note;
    private List<Item> items;

    @Getter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class Item {
        private Long productId;
        private BigDecimal quantity;
    }
}

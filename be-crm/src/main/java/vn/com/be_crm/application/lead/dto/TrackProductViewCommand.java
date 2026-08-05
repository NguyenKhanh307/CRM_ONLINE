package vn.com.be_crm.application.lead.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// input khi khách xem chi tiết một sản phẩm trên landing page công khai
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class TrackProductViewCommand {
    private String code;
    private Long productId;
}

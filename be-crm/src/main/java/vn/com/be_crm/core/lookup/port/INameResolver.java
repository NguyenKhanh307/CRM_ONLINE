package vn.com.be_crm.core.lookup.port;

import java.util.Collection;
import java.util.Map;

/**
 * Port tra tên hiển thị theo ID khóa ngoại, dùng để làm giàu (enrich) các Result DTO danh sách
 * trước khi trả về FE — thay cho việc FE resolve tên phía client.
 *
 * <p>Đặc tả chung cho mọi method:
 * <ul>
 *   <li>Bỏ qua ID null/trùng; {@code ids} rỗng → trả {@link Map#of()} (không chạy query).</li>
 *   <li><b>Không</b> lọc {@code deleted_at}/{@code is_purged} → tên vẫn resolve được cho bản ghi
 *       đã xóa mềm hoặc xóa vĩnh viễn (bản ghi vẫn còn trong DB).</li>
 * </ul>
 */
public interface INameResolver {

    /** Tra tên đầy đủ người dùng (users.full_name). @param ids tập ID @return map id→tên */
    Map<Long, String> users(Collection<Long> ids);

    /** Tra tên khách hàng (customers.name). @param ids tập ID @return map id→tên */
    Map<Long, String> customers(Collection<Long> ids);

    /** Tra tên liên hệ (contacts.full_name). @param ids tập ID @return map id→tên */
    Map<Long, String> contacts(Collection<Long> ids);

    /** Tra tên chiến dịch (campaigns.name). @param ids tập ID @return map id→tên */
    Map<Long, String> campaigns(Collection<Long> ids);

    /** Tra tên cơ hội (opportunities.name). @param ids tập ID @return map id→tên */
    Map<Long, String> opportunities(Collection<Long> ids);

    /** Tra mã báo giá (quotations.code). @param ids tập ID @return map id→mã */
    Map<Long, String> quotationCodes(Collection<Long> ids);

    /** Tra mã đơn hàng (orders.code). @param ids tập ID @return map id→mã */
    Map<Long, String> orderCodes(Collection<Long> ids);

    /** Tra mã hóa đơn (invoices.code). @param ids tập ID @return map id→mã */
    Map<Long, String> invoiceCodes(Collection<Long> ids);

    /** Tra tên giai đoạn cơ hội (opportunity_stages.name). @param ids tập ID @return map id→tên */
    Map<Long, String> stages(Collection<Long> ids);

    /** Tra tên danh mục sản phẩm (product_categories.name). @param ids tập ID @return map id→tên */
    Map<Long, String> productCategories(Collection<Long> ids);
}

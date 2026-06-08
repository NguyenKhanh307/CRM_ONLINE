package vn.com.be_crm.domain.opportunity.repository;

import vn.com.be_crm.domain.opportunity.entity.OpportunityItem;

import java.util.List;
import java.util.Optional;

/**
 * Port lưu trữ cho OpportunityItem.
 */
public interface IOpportunityItemRepository {

    /**
     * Lưu mới hoặc cập nhật dòng sản phẩm trong cơ hội.
     * @param item domain entity @return entity sau khi lưu
     */
    OpportunityItem save(OpportunityItem item);

    /**
     * Tìm dòng sản phẩm theo ID.
     * @param id ID @return Optional
     */
    Optional<OpportunityItem> findById(Long id);

    /**
     * Xóa dòng sản phẩm theo ID.
     * @param id ID cần xóa
     */
    void deleteById(Long id);

    /**
     * Lấy danh sách dòng sản phẩm theo opportunityId.
     * @param opportunityId ID cơ hội @return danh sách
     */
    List<OpportunityItem> findAllByOpportunityId(Long opportunityId);
}

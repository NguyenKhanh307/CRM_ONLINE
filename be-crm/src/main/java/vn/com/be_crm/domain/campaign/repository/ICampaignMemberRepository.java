package vn.com.be_crm.domain.campaign.repository;

import vn.com.be_crm.domain.campaign.entity.CampaignMember;

import java.util.List;
import java.util.Optional;

/**
 * Port lưu trữ cho CampaignMember.
 */
public interface ICampaignMemberRepository {
    /** Lưu mới hoặc cập nhật thành viên chiến dịch. @param m @return entity sau khi lưu */
    CampaignMember save(CampaignMember m);
    /** Tìm thành viên theo ID. @param id @return Optional */
    Optional<CampaignMember> findById(Long id);
    /** Xóa thành viên. @param id */
    void deleteById(Long id);
    /** Lấy danh sách thành viên theo campaignId. @param campaignId @return danh sách */
    List<CampaignMember> findAllByCampaignId(Long campaignId);
}

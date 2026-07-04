package vn.com.be_crm.infrastructure.campaign.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.domain.campaign.entity.CampaignMember;
import vn.com.be_crm.domain.campaign.repository.ICampaignMemberRepository;
import vn.com.be_crm.infrastructure.campaign.entity.CampaignMemberHibernate;
import vn.com.be_crm.infrastructure.campaign.mapper.CampaignMemberHibernateMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Hibernate implementation của ICampaignMemberRepository.
 */
@Repository
public class CampaignMemberRepositoryImpl implements ICampaignMemberRepository {
    private final SessionFactory sf;
    private final CampaignMemberHibernateMapper mapper;

    /** @param sf Hibernate SessionFactory @param mapper mapper */
    public CampaignMemberRepositoryImpl(SessionFactory sf, CampaignMemberHibernateMapper mapper) {
        this.sf = sf; this.mapper = mapper;
    }

    /** Lưu mới hoặc cập nhật CampaignMember. @param m @return entity sau khi lưu */
    @Override public CampaignMember save(CampaignMember m) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            CampaignMemberHibernate h = s.merge(mapper.toHibernate(m));
            tx.commit(); return mapper.toDomain(h);
        }
    }

    /** Tìm CampaignMember theo ID. @param id @return Optional */
    @Override public Optional<CampaignMember> findById(Long id) {
        try (Session s = sf.openSession()) {
            CampaignMemberHibernate h = s.find(CampaignMemberHibernate.class, id);
            return Optional.ofNullable(h).map(mapper::toDomain);
        }
    }

    /** Xóa CampaignMember. @param id */
    @Override public void deleteById(Long id) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            CampaignMemberHibernate h = s.find(CampaignMemberHibernate.class, id);
            if (h != null) s.remove(h);
            tx.commit();
        }
    }

    /** Lấy danh sách CampaignMember theo campaignId. @param campaignId @return danh sách */
    @Override public List<CampaignMember> findAllByCampaignId(Long campaignId) {
        try (Session s = sf.openSession()) {
            return s.createQuery("FROM CampaignMemberHibernate WHERE campaignId = :cid ORDER BY id", CampaignMemberHibernate.class)
                    .setParameter("cid", campaignId).list().stream().map(mapper::toDomain).collect(Collectors.toList());
        }
    }
}

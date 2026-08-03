package vn.com.be_crm.infrastructure.service.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.domain.service.entity.TicketComment;
import vn.com.be_crm.domain.service.repository.ITicketCommentRepository;
import vn.com.be_crm.infrastructure.service.entity.TicketCommentHibernate;
import vn.com.be_crm.infrastructure.service.mapper.TicketCommentHibernateMapper;

import java.util.List;
import java.util.stream.Collectors;
import vn.com.be_crm.core.tx.impl.TxSupport;

/**
 * Hibernate implementation của ITicketCommentRepository.
 */
@Repository
public class TicketCommentRepositoryImpl implements ITicketCommentRepository {
    private final SessionFactory sf;
    private final TicketCommentHibernateMapper mapper;

    /** @param sf Hibernate SessionFactory @param mapper mapper */
    public TicketCommentRepositoryImpl(SessionFactory sf, TicketCommentHibernateMapper mapper) {
        this.sf = sf; this.mapper = mapper;
    }

    /** Lưu mới ghi chú ticket. @param comment @return entity sau khi lưu */
    @Override public TicketComment save(TicketComment comment) {
        return TxSupport.write(sf, s -> {
            TicketCommentHibernate m = s.merge(mapper.toHibernate(comment));
            return mapper.toDomain(m);
        });
    }

    /** Lấy danh sách ghi chú theo ticketId (cũ → mới). @param ticketId @return danh sách */
    @Override public List<TicketComment> findAllByTicketId(Long ticketId) {
        return TxSupport.read(sf, s -> {
            return s.createQuery("FROM TicketCommentHibernate WHERE ticketId = :tid ORDER BY createdAt ASC, id ASC", TicketCommentHibernate.class)
                    .setParameter("tid", ticketId).list().stream().map(mapper::toDomain).collect(Collectors.toList());
        });
    }
}

package vn.com.be_crm.infrastructure.shared.tx;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Component;
import vn.com.be_crm.application.shared.tx.ITransactionRunner;

import java.util.function.Supplier;

/**
 * Bản cài đặt Hibernate của {@link ITransactionRunner}: mở một session + transaction,
 * gắn vào {@link SessionHolder} để các repository join chung, rồi commit (hoặc rollback nếu lỗi).
 * Nếu đã có session ambient (Unit of Work lồng nhau) thì chạy thẳng, không mở transaction mới.
 */
@Component
public class HibernateTransactionRunner implements ITransactionRunner {

    private final SessionFactory sessionFactory;

    /** @param sessionFactory Hibernate SessionFactory dùng chung */
    public HibernateTransactionRunner(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public <T> T call(Supplier<T> work) {
        // Đã trong một Unit of Work → tham gia transaction hiện có, không mở lồng nhau
        if (SessionHolder.hasSession()) {
            return work.get();
        }
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            SessionHolder.set(session);
            try {
                T result = work.get();
                tx.commit();
                return result;
            } catch (RuntimeException e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            } finally {
                SessionHolder.clear();
            }
        }
    }
}

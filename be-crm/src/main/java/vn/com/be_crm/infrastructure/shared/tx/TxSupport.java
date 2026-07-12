package vn.com.be_crm.infrastructure.shared.tx;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Helper thống nhất cách repository lấy session:
 * - Nếu đang trong một Unit of Work ({@link SessionHolder} có session) → dùng session ambient,
 *   KHÔNG tự commit/close (để {@link HibernateTransactionRunner} bên ngoài lo).
 * - Nếu gọi đơn lẻ (ngoài Unit of Work) → mở session + transaction riêng, commit + close như cũ.
 * Nhờ đó nhiều lệnh ghi trong một use case đa bước cùng commit/rollback, nhưng khi gọi lẻ vẫn
 * giữ nguyên hành vi transaction độc lập.
 */
public final class TxSupport {

    private TxSupport() {}

    /**
     * Thao tác GHI có trả kết quả.
     *
     * @param sf   SessionFactory
     * @param body hàm nhận session, thực hiện merge/executeUpdate và trả kết quả
     * @param <T>  kiểu kết quả
     * @return kết quả của body
     */
    public static <T> T write(SessionFactory sf, Function<Session, T> body) {
        if (SessionHolder.hasSession()) {
            return body.apply(SessionHolder.get());
        }
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            try {
                T result = body.apply(s);
                tx.commit();
                return result;
            } catch (RuntimeException e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            }
        }
    }

    /**
     * Thao tác GHI không trả kết quả.
     *
     * @param sf   SessionFactory
     * @param body hàm nhận session, thực hiện thay đổi dữ liệu
     */
    public static void writeVoid(SessionFactory sf, Consumer<Session> body) {
        write(sf, s -> {
            body.accept(s);
            return null;
        });
    }

    /**
     * Thao tác ĐỌC. Trong Unit of Work → dùng session ambient (thấy được thay đổi chưa commit
     * của cùng transaction); ngoài Unit of Work → mở session tạm (không transaction) rồi đóng.
     *
     * @param sf   SessionFactory
     * @param body hàm nhận session, thực hiện truy vấn và trả kết quả
     * @param <T>  kiểu kết quả
     * @return kết quả của body
     */
    public static <T> T read(SessionFactory sf, Function<Session, T> body) {
        if (SessionHolder.hasSession()) {
            return body.apply(SessionHolder.get());
        }
        try (Session s = sf.openSession()) {
            return body.apply(s);
        }
    }
}

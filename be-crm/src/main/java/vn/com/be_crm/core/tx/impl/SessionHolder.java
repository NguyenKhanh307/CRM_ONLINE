package vn.com.be_crm.core.tx.impl;

import org.hibernate.Session;

/**
 * Giữ session Hibernate "ambient" theo thread (Unit of Work) để nhiều repository
 * cùng tham gia MỘT transaction. Chỉ được set/clear bởi {@link HibernateTransactionRunner};
 * repository chỉ đọc qua {@link #get()}/{@link #hasSession()}.
 */
public final class SessionHolder {

    private static final ThreadLocal<Session> CURRENT = new ThreadLocal<>();

    private SessionHolder() {}

    /** @return session ambient của thread hiện tại, hoặc null nếu không trong Unit of Work */
    public static Session get() {
        return CURRENT.get();
    }

    /** @return true nếu thread hiện tại đang trong một Unit of Work */
    public static boolean hasSession() {
        return CURRENT.get() != null;
    }

    /** Gắn session ambient cho thread hiện tại. @param session session của Unit of Work */
    public static void set(Session session) {
        CURRENT.set(session);
    }

    /** Gỡ session ambient khỏi thread hiện tại (bắt buộc gọi trong finally). */
    public static void clear() {
        CURRENT.remove();
    }
}

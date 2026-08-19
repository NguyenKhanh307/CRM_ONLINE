package vn.com.be_crm.application.dashboard.query;

/**
 * Hằng số mặc định dùng chung cho các use case Dashboard.
 */
public final class DashboardDefaults {

    private DashboardDefaults() {
    }

    /** Ngưỡng mặc định (ngày) để coi 1 cơ hội đang mở là "treo" — không được chăm sóc trong ngần ấy ngày. */
    public static final int STALLED_DAYS_DEFAULT = 14;
}

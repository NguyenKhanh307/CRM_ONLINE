package vn.com.be_crm.core.notify.port;

import java.util.List;

/**
 * Port xác định người quản lý cần nhận thông báo của một nhân viên.
 * Thay cho việc broadcast tới toàn bộ ADMIN + SALES_MANAGER + SALES_STAFF (chuông 99+ cho mọi sale).
 */
public interface IManagerResolver {

    /**
     * Tìm quản lý trực tiếp của một nhân viên: đọc đơn vị của nhân viên → trưởng đơn vị,
     * leo lên đơn vị cha nếu đơn vị chưa gán trưởng.
     * Không tìm được ai → trả về toàn bộ user có vai trò SALES_MANAGER (để thông báo không bị rơi).
     *
     * @param userId ID nhân viên (có thể null)
     * @return danh sách ID người nhận (không gồm chính nhân viên đó)
     */
    List<Long> managersOf(Long userId);
}

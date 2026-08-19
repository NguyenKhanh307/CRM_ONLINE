// cầu nối module-level giữa AlertProvider (trong cây React) và code ngoài cây React
// (vd MutationCache của React Query khởi tạo ở module scope — không gọi được useAlert())
// AlertProvider đăng ký showAlert vào đây khi mount; showGlobalAlert là no-op khi chưa đăng ký
type AlertHandler = (message: string) => void;
// handler được set bởi AlertProvider khi mount, unset khi unmount
let handler: AlertHandler | null = null;

// AlertProvider gọi khi mount/unmount để đăng ký/gỡ hàm hiển thị alert
export const setAlertHandler = (fn: AlertHandler | null) => {
    handler = fn;
};

// hiện AlertModal từ ngoài cây React (no-op nếu AlertProvider chưa mount)
export const showGlobalAlert = (message: string) => {
    handler?.(message);
};

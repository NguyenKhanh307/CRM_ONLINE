type Listener = () => void;
const listeners = new Map<string, Set<Listener>>();

// đăng ký nghe key này đổi dữ liệu, trả về hàm hủy đăng ký
export function subscribe(key: string, cb: Listener) {
    if (!listeners.has(key)) listeners.set(key, new Set());
    listeners.get(key)!.add(cb);
    return () => listeners.get(key)?.delete(cb);
}

// báo cho mọi nơi đang nghe key này (và các key con của nó, vd notify('leads')
// đánh thức cả 'leads' lẫn 'leads:paged:...', không đánh thức 'lead:5')
export function notify(prefix: string) {
    for (const [key, cbs] of listeners) {
        // prefix là key hoặc là cha của key thì gọi callback
        // nếu key đang lắng nghe là prefix hoặc là con của prefix thì gọi callback
        if (key === prefix || key.startsWith(prefix + ':')) cbs.forEach((cb) => cb());
    }
}

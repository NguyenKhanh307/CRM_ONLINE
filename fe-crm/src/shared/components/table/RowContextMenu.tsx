import { useLayoutEffect, useEffect, useRef, useState } from 'react';
import { createPortal } from 'react-dom';
import type { RowAction } from '@/shared/types/table';

interface Props {
    /** Toạ độ con trỏ lúc chuột phải (clientX/clientY). */
    x: number;
    y: number;
    actions: RowAction[];
    onClose: () => void;
}

/** Khoảng cách tối thiểu tới mép màn hình khi phải lật menu vào trong. */
const EDGE_GAP = 8;

/**
 * Menu chuột phải cho một dòng bảng — thay cho cột "Thao tác" cũ.
 * Render qua portal + `position: fixed` (giống SearchableSelect) để không bị
 * container `overflow-auto` của bảng cắt mất. Tự lật vào trong khi chạm mép màn hình.
 * Đóng khi: click ngoài, phím Escape, cuộn trang, resize, hoặc sau khi chọn một mục.
 */
export const RowContextMenu = ({ x, y, actions, onClose }: Props) => {
    const panelRef = useRef<HTMLDivElement>(null);
    const [pos, setPos] = useState({ top: y, left: x });

    // Lật vào trong nếu tràn mép phải/dưới — đo sau khi menu đã render (trước khi trình duyệt vẽ).
    useLayoutEffect(() => {
        const el = panelRef.current;
        if (!el) return;
        const { width, height } = el.getBoundingClientRect();
        const left = x + width > window.innerWidth - EDGE_GAP
            ? Math.max(EDGE_GAP, x - width)
            : x;
        const top = y + height > window.innerHeight - EDGE_GAP
            ? Math.max(EDGE_GAP, y - height)
            : y;
        setPos({ top, left });
    }, [x, y]);

    useEffect(() => {
        const onMouseDown = (e: MouseEvent) => {
            if (panelRef.current && !panelRef.current.contains(e.target as Node)) onClose();
        };
        const onKeyDown = (e: KeyboardEvent) => {
            if (e.key === 'Escape') onClose();
        };
        document.addEventListener('mousedown', onMouseDown);
        document.addEventListener('keydown', onKeyDown);
        window.addEventListener('scroll', onClose, true);
        window.addEventListener('resize', onClose);
        return () => {
            document.removeEventListener('mousedown', onMouseDown);
            document.removeEventListener('keydown', onKeyDown);
            window.removeEventListener('scroll', onClose, true);
            window.removeEventListener('resize', onClose);
        };
    }, [onClose]);

    /** Chèn đường kẻ ngay trước mục danger đầu tiên (tách "Xóa" khỏi nhóm còn lại). */
    const firstDangerIndex = actions.findIndex((a) => a.danger);

    return createPortal(
        <div
            ref={panelRef}
            style={{ position: 'fixed', top: pos.top, left: pos.left }}
            className="z-[10000] min-w-[170px] bg-white border border-gray-200 rounded-section shadow-lg py-0.5"
            onContextMenu={(e) => e.preventDefault()}
        >
            {actions.map((action, i) => (
                <div key={action.key}>
                    {i === firstDangerIndex && i > 0 && (
                        <div className="my-0.5 border-t border-gray-100" />
                    )}
                    <button
                        type="button"
                        disabled={action.disabled}
                        onClick={() => {
                            action.onClick();
                            onClose();
                        }}
                        className={[
                            'w-full text-left px-3 py-1.5 text-sm whitespace-nowrap',
                            action.disabled
                                ? 'text-gray-300 cursor-not-allowed'
                                : action.danger
                                  ? 'text-danger hover:bg-red-50'
                                  : 'text-text-main hover:bg-gray-100',
                        ].join(' ')}
                    >
                        {action.label}
                    </button>
                </div>
            ))}
        </div>,
        document.body
    );
};

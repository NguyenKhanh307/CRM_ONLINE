interface PrefillHintProps {
    /** Tên bản ghi vừa kéo dữ liệu về; `null` thì không hiện gì. */
    source: string | null;
}

/**
 * Dòng gợi ý nhạt dưới ô nguồn, báo form vừa tự điền các ô còn trống.
 * Cố ý KHÔNG dùng `showAlert` — đó là modal chặn thao tác, quá nặng cho một tiện ích nhập liệu.
 */
export const PrefillHint = ({ source }: PrefillHintProps) =>
    source ? <p className="text-xs text-gray-400 mt-1">Đã tự điền các ô còn trống từ {source}</p> : null;

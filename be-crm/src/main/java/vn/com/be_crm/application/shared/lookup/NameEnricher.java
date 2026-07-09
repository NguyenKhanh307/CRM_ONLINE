package vn.com.be_crm.application.shared.lookup;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Tiện ích làm giàu tên khóa ngoại cho danh sách Result DTO — gom ID, tra tên một lần, rồi set.
 *
 * <p>Ví dụ (trong List use case):
 * <pre>{@code
 * NameEnricher.apply(items, LeadResult::getOwnerId, nameResolver::users, LeadResult::setOwnerName);
 * }</pre>
 */
public final class NameEnricher {

    private NameEnricher() {}

    /**
     * Set tên hiển thị cho một trường khóa ngoại trên toàn bộ danh sách.
     * @param items   danh sách Result
     * @param idOf    lấy ID khóa ngoại từ một phần tử
     * @param resolve hàm tra id→tên (vd {@code nameResolver::users})
     * @param setName gán tên vào phần tử
     * @param <T>     kiểu Result DTO
     */
    public static <T> void apply(List<T> items,
                                 Function<T, Long> idOf,
                                 Function<Collection<Long>, Map<Long, String>> resolve,
                                 BiConsumer<T, String> setName) {
        if (items == null || items.isEmpty()) return;
        List<Long> ids = items.stream().map(idOf).filter(Objects::nonNull).toList();
        Map<Long, String> map = resolve.apply(ids);
        // Bỏ qua bản ghi có FK null — tránh map.get(null) (Map bất biến rỗng ném NPE với key null).
        items.forEach(t -> {
            Long id = idOf.apply(t);
            if (id != null) setName.accept(t, map.get(id));
        });
    }
}

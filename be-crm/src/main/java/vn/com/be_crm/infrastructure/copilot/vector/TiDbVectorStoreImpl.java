package vn.com.be_crm.infrastructure.copilot.vector;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.domain.copilot.model.VectorHit;
import vn.com.be_crm.domain.copilot.repository.IVectorStore;
import vn.com.be_crm.core.tx.impl.TxSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Tìm kiếm ngữ nghĩa trên bảng {@code copilot_chunks} bằng {@code VEC_COSINE_DISTANCE} của TiDB.
 * <p>Kiểu {@code VECTOR} không có trong MySQLDialect nên bảng này chỉ truy cập bằng native query —
 * không có Hibernate entity, giống module {@code copilot}/{@code dashboard}/{@code related}.
 * <p>Bảng do {@code tools/indexer/} ghi; lớp này chỉ ĐỌC.
 */
@Repository
public class TiDbVectorStoreImpl implements IVectorStore {

    private static final Logger log = LoggerFactory.getLogger(TiDbVectorStoreImpl.class);

    /** Lọc owner ngay trong SQL — nhân viên chỉ thấy chunk của mình hoặc chunk dùng chung. */
    private static final String SQL = """
            SELECT module, record_id, title, content, VEC_COSINE_DISTANCE(embedding, :q) AS d
            FROM copilot_chunks
            WHERE (:ownerId IS NULL OR owner_id IS NULL OR owner_id = :ownerId)
            ORDER BY d
            LIMIT :k
            """;

    private final SessionFactory sf;

    /** @param sf Hibernate SessionFactory */
    public TiDbVectorStoreImpl(SessionFactory sf) {
        this.sf = sf;
    }

    /** {@inheritDoc} */
    @Override
    public List<VectorHit> search(float[] queryVector, Long ownerId, int topK, double maxDistance) {
        if (queryVector == null || queryVector.length == 0) return List.of();
        String vec = toVectorLiteral(queryVector);
        try {
            return TxSupport.read(sf, s -> {
                List<Object[]> rows = s.createNativeQuery(SQL, Object[].class)
                        .setParameter("q", vec)
                        .setParameter("ownerId", ownerId)
                        .setParameter("k", topK)
                        .getResultList();
                List<VectorHit> hits = new ArrayList<>();
                for (Object[] r : rows) {
                    double d = ((Number) r[4]).doubleValue();
                    // Bỏ trích đoạn quá xa: nhồi nội dung không liên quan vào prompt làm
                    // mô hình trả lời lạc đề, tệ hơn là không đưa gì cả.
                    if (d > maxDistance) continue;
                    hits.add(new VectorHit((String) r[0], ((Number) r[1]).longValue(),
                            (String) r[2], (String) r[3], d));
                }
                return hits;
            });
        } catch (RuntimeException e) {
            // Chưa chạy migration / chưa build chỉ mục -> Copilot vẫn phải trả lời được bằng
            // nhánh số liệu SQL. Tuyệt đối không để lỗi ở đây làm chết endpoint đang chạy tốt.
            log.warn("Không tìm kiếm được trong copilot_chunks: {}", e.toString());
            return List.of();
        }
    }

    /** {@inheritDoc} */
    @Override
    public long countChunks() {
        try {
            return TxSupport.read(sf, s -> {
                Object n = s.createNativeQuery("SELECT COUNT(*) FROM copilot_chunks", Object.class)
                        .uniqueResult();
                return n == null ? 0L : ((Number) n).longValue();
            });
        } catch (RuntimeException e) {
            log.warn("Không đọc được số chunk: {}", e.toString());
            return 0L;
        }
    }

    /** Dựng chuỗi {@code '[0.1,0.2,...]'} — TiDB tự cast sang kiểu VECTOR khi so sánh. */
    private String toVectorLiteral(float[] v) {
        StringBuilder sb = new StringBuilder(v.length * 10 + 2).append('[');
        for (int i = 0; i < v.length; i++) {
            if (i > 0) sb.append(',');
            sb.append(String.format(java.util.Locale.ROOT, "%.6f", v[i]));
        }
        return sb.append(']').toString();
    }
}

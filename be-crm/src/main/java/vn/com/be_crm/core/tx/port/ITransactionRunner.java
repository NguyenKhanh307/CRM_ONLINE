package vn.com.be_crm.core.tx.port;

import java.util.function.Supplier;

/**
 * Port chạy một khối công việc trong MỘT transaction (Unit of Work).
 * Dùng cho use case đa bước (convert lead, convert-to-order, create-invoice...) để nhiều lệnh ghi
 * cùng commit hoặc cùng rollback — tránh bản ghi mồ côi khi lỗi giữa chừng.
 * Thuần Java, không phụ thuộc Hibernate → giữ tầng application sạch (impl ở infrastructure).
 */
public interface ITransactionRunner {

    /**
     * Chạy khối công việc có trả kết quả trong một transaction.
     * Lỗi (RuntimeException) → rollback toàn bộ; thành công → commit.
     *
     * @param work khối công việc (các lệnh ghi qua repository)
     * @param <T>  kiểu kết quả
     * @return kết quả của khối công việc
     */
    <T> T call(Supplier<T> work);

    /**
     * Chạy khối công việc không trả kết quả trong một transaction.
     *
     * @param work khối công việc
     */
    default void run(Runnable work) {
        call(() -> {
            work.run();
            return null;
        });
    }
}

package vn.com.be_crm.core.usecase;

/**
 * Marker interface cho tất cả Use Case trong application layer.
 *
 * @param <I> kiểu input (Command hoặc Query parameter)
 * @param <O> kiểu output (Result hoặc Void)
 */
public interface IUseCase<I, O> {

    /**
     * Thực thi use case với input đã cho.
     *
     * @param input dữ liệu đầu vào
     * @return kết quả đầu ra
     */
    O execute(I input);
}

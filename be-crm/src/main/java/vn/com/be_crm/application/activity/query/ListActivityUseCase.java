package vn.com.be_crm.application.activity.query;

import vn.com.be_crm.application.activity.dto.ActivityResult;
import vn.com.be_crm.application.activity.mapper.ActivityCommandMapper;
import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.shared.dto.PageResult;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.activity.repository.IActivityRepository;

import java.util.stream.Collectors;

/** Use case lấy danh sách hoạt động có phân trang. */
public class ListActivityUseCase implements IUseCase<PageRequest, PageResult<ActivityResult>> {
    private final IActivityRepository repository;
    /** @param repository port lưu trữ Activity */
    public ListActivityUseCase(IActivityRepository repository) { this.repository = repository; }
    /**
     * Lấy danh sách Activity theo tham số phân trang.
     * @param request tham số phân trang
     * @return PageResult chứa danh sách ActivityResult
     */
    @Override
    public PageResult<ActivityResult> execute(PageRequest request) {
        var page = repository.findAll(request);
        return PageResult.<ActivityResult>builder()
                .items(page.getItems().stream().map(ActivityCommandMapper::toResult).collect(Collectors.toList()))
                .total(page.getTotal()).page(page.getPage()).size(page.getSize()).build();
    }
}

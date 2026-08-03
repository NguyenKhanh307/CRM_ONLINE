package vn.com.be_crm.domain.lead.repository;

import vn.com.be_crm.domain.lead.entity.LeadTransfer;

import java.util.List;
import java.util.Optional;

// port lưu trữ cho LeadTransfer
public interface ILeadTransferRepository {

    LeadTransfer save(LeadTransfer transfer);

    Optional<LeadTransfer> findById(Long id);

    void deleteById(Long id);

    List<LeadTransfer> findAllByLeadId(Long leadId);
}

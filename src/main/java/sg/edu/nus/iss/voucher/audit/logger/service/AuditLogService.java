package sg.edu.nus.iss.voucher.audit.logger.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;

import sg.edu.nus.iss.voucher.audit.logger.dto.AuditLogDTO;

public interface AuditLogService {

	// AuditLogDTO executeAuditLog(AuditLog auditLog);

	void executeAuditLog(AuditLogDTO auditLogDTO);

	// void executeAuditLog(AuditLog auditLog);

	Map<Long, List<AuditLogDTO>> retrieveAllAuditLogs(Pageable pageable);

	Map<Long, List<AuditLogDTO>> searchAuditLogs(String activityType, String userId, Pageable pageable);

}

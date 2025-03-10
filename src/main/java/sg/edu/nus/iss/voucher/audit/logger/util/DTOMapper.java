package sg.edu.nus.iss.voucher.audit.logger.util;

import sg.edu.nus.iss.voucher.audit.logger.dto.AuditLogDTO;
import sg.edu.nus.iss.voucher.audit.logger.entity.AuditLog;

public class DTOMapper {
	
	public static AuditLogDTO toauditLogDTO(AuditLog auditLogData) {
		
		AuditLogDTO auditLogDTO = new AuditLogDTO();
		auditLogDTO.setAuditId(auditLogData.getAuditId());
		auditLogDTO.setStatusCode(auditLogData.getStatusCode());
		auditLogDTO.setUserId(auditLogData.getUserId());
		auditLogDTO.setUsername(auditLogData.getUsername());
		auditLogDTO.setActivityType(auditLogData.getActivityType());
		auditLogDTO.setActivityDescription(auditLogData.getActivityDescription());
		auditLogDTO.setRequestActionEndpoint(auditLogData.getRequestActionEndpoint());
		auditLogDTO.setResponseStatus(auditLogData.getResponseStatus());
		auditLogDTO.setRequestType(auditLogData.getRequestType());
		auditLogDTO.setInsertDate(auditLogData.getInsertDate());
		auditLogDTO.setLastupdatedDate(auditLogData.getLastupdatedDate());
		auditLogDTO.setRemarks(auditLogData.getRemarks());
	    
		return auditLogDTO;
	}
	
	

}

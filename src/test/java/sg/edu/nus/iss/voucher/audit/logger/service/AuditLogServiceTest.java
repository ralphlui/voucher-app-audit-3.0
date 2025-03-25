package sg.edu.nus.iss.voucher.audit.logger.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import jakarta.transaction.Transactional;
import sg.edu.nus.iss.voucher.audit.logger.dto.AuditLogDTO;
import sg.edu.nus.iss.voucher.audit.logger.entity.AuditLog;
import sg.edu.nus.iss.voucher.audit.logger.repository.AuditLogRepository;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ActiveProfiles("test")
public class AuditLogServiceTest {

	@MockBean
    private AuditLogRepository auditLogRepository;

	@Autowired
    private AuditLogService auditLogService;
    
    @Test
    void testExecuteAuditLog_shouldSaveAuditLogWithCorrectValues() {
        // Arrange
        AuditLogDTO dto = new AuditLogDTO();
        dto.setStatusCode("200");
        dto.setUserId("user123");
        dto.setUsername("john.doe");
        dto.setActivityType("LOGIN");
        dto.setActivityDescription("User logged in");
        dto.setRequestActionEndpoint("/api/login");
        dto.setResponseStatus("SUCCESS");
        dto.setRequestType("POST");
        dto.setRemarks("No issues");
        
        ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);

        auditLogService.executeAuditLog(dto);
        verify(auditLogRepository, times(1)).save(captor.capture());
  
        AuditLog savedLog = captor.getValue();
        assertEquals(dto.getStatusCode(), savedLog.getStatusCode());
        assertEquals(dto.getUserId(), savedLog.getUserId());
    }
}

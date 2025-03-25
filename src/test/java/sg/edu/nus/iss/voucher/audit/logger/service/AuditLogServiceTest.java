package sg.edu.nus.iss.voucher.audit.logger.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import jakarta.transaction.Transactional;
import sg.edu.nus.iss.voucher.audit.logger.dto.AuditLogDTO;
import sg.edu.nus.iss.voucher.audit.logger.entity.AuditLog;
import sg.edu.nus.iss.voucher.audit.logger.repository.AuditLogRepository;
import sg.edu.nus.iss.voucher.audit.logger.util.DTOMapper;

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
    
    
	@Test
	void testRetrieveAllAuditLogs_shouldReturnMappedDTOsWithTotalCountKey() {
		// Arrange
		AuditLog auditLog = new AuditLog();
		AuditLogDTO auditLogDTO = new AuditLogDTO();
		Pageable pageable = PageRequest.of(0, 10);

		List<AuditLog> auditLogList = List.of(auditLog);
		Page<AuditLog> auditLogPage = new PageImpl<>(auditLogList, pageable, 1);

		when(auditLogRepository.retrieveAuditLogWith(pageable)).thenReturn(auditLogPage);

		try (MockedStatic<DTOMapper> mockedStatic = Mockito.mockStatic(DTOMapper.class)) {
			mockedStatic.when(() -> DTOMapper.toauditLogDTO(auditLog)).thenReturn(auditLogDTO);

			// Act
			Map<Long, List<AuditLogDTO>> result = auditLogService.retrieveAllAuditLogs(pageable);

			// Assert
			assertEquals(1, result.size());
			List<AuditLogDTO> dtoList = result.get(1L);
			assertNotNull(dtoList);
			assertEquals(1, dtoList.size());
			assertSame(auditLogDTO, dtoList.get(0));
		}
	}
}

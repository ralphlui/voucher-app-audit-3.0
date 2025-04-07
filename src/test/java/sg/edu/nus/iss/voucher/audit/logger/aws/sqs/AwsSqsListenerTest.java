package sg.edu.nus.iss.voucher.audit.logger.aws.sqs;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import sg.edu.nus.iss.voucher.audit.logger.dto.AuditLogDTO;
import sg.edu.nus.iss.voucher.audit.logger.service.AuditLogService;

import static org.mockito.Mockito.*;

class AwsSqsListenerTest {

    @InjectMocks
    private AwsSqsListener awsSqsListener;

    @Mock
    private AuditLogService auditLogService;

    @Captor
    private ArgumentCaptor<AuditLogDTO> auditLogCaptor;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void receiveMessage_validJson_shouldProcessAuditLog() throws Exception {
        // Arrange
        AuditLogDTO dto = new AuditLogDTO();
        dto.setUsername("john_doe");

        String jsonMessage = objectMapper.writeValueAsString(dto);

        // Act
        awsSqsListener.receiveMessage(jsonMessage);

        // Assert
        verify(auditLogService, times(1)).executeAuditLog(auditLogCaptor.capture());
        AuditLogDTO capturedDto = auditLogCaptor.getValue();
        assert capturedDto.getUsername().equals("john_doe");
    }

    @Test
    void receiveMessage_invalidJson_shouldLogErrorAndNotCallService() {
        // Arrange
        String invalidJson = "{ invalid json }";

        // Act
        awsSqsListener.receiveMessage(invalidJson);

        // Assert
        verify(auditLogService, never()).executeAuditLog(any());
    }

    @Test
    void receiveMessage_validJson_butServiceThrowsException_shouldLogError() throws Exception {
        // Arrange
        AuditLogDTO dto = new AuditLogDTO();
        dto.setUsername("jane_doe");

        String jsonMessage = objectMapper.writeValueAsString(dto);

        doThrow(new RuntimeException("Service exception")).when(auditLogService).executeAuditLog(any());

        // Act
        awsSqsListener.receiveMessage(jsonMessage);

        // Assert
        verify(auditLogService, times(1)).executeAuditLog(any());
    }
}

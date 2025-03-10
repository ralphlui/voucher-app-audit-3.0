package sg.edu.nus.iss.voucher.audit.logger;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import sg.edu.nus.iss.voucher.audit.logger.dto.AuditLogDTO;
import sg.edu.nus.iss.voucher.audit.logger.entity.AuditLog;
import sg.edu.nus.iss.voucher.audit.logger.service.Impl.AuditLogServiceImpl;
import sg.edu.nus.iss.voucher.audit.logger.util.DTOMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class AuditLogTriggeringTests {
	
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;
	
	@MockBean
	private AuditLogServiceImpl auditLogService;
	
	private static List<AuditLogDTO> mockAuditLog = new ArrayList<>();
	
	private static final Date testDate = new Date();
	
	private static AuditLog auditLogData1 = new AuditLog(1, "200", "12345", "mocktest_data_001", "login", "User log into the system",
			"/api/login","Success", "POST", testDate, testDate,"Login successful");
	
	private static AuditLog auditLogData2 = new AuditLog(2, "200", "12345", "mocktest_data_002", "login", "User log into the system",
			"/api/login","Failed", "POST", testDate, testDate,"Login attempt is failed");

	
	@BeforeAll
	static void setUp() {
		mockAuditLog.add(DTOMapper.toauditLogDTO(auditLogData1));
		mockAuditLog.add(DTOMapper.toauditLogDTO(auditLogData2));
	}

	@Test
	void testRetrieveAllAuditLogs() throws Exception {
		Pageable pageable = PageRequest.of(0, 10, Sort.by("lastupdatedDate").ascending());
		Map<Long, List<AuditLogDTO>> mockAuditDataMap = new HashMap<>();
		mockAuditDataMap.put(0L, mockAuditLog);

		Mockito.when(auditLogService.retrieveAllAuditLogs(pageable)).thenReturn(mockAuditDataMap);

		mockMvc.perform(MockMvcRequestBuilders.get("/api/audit/retrieveAllAuditLogs").param("page", "0").param("size", "10")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.success").value(true)).andExpect(jsonPath("$.data[0].auditId").value(1))
				.andDo(print());
	}
	
	@Test
	void testSearchAuditLogsWithActivityTypeAndUserId() throws Exception {
	    Pageable pageable = PageRequest.of(0, 10, Sort.by("lastupdatedDate").ascending());
	    Map<Long, List<AuditLogDTO>> mockAuditDataMap = new HashMap<>();
	    mockAuditDataMap.put(0L, mockAuditLog);

	    Mockito.when(auditLogService.searchAuditLogs("login", "12345", pageable)).thenReturn(mockAuditDataMap);

	    mockMvc.perform(MockMvcRequestBuilders.get("/api/audit/searchByParams")
	            .param("activityType", "login")
	            .param("userId", "12345")
	            .param("page", "0")
	            .param("size", "10")
	            .contentType(MediaType.APPLICATION_JSON))
	        .andExpect(MockMvcResultMatchers.status().isOk())
	        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
	        .andExpect(jsonPath("$.success").value(true))
	        .andExpect(jsonPath("$.data[0].auditId").value(1))
	        .andDo(print());
	}

	@Test
	void testSearchAuditLogsWithActivityTypeOnly() throws Exception {
	    Pageable pageable = PageRequest.of(0, 10, Sort.by("lastupdatedDate").ascending());
	    Map<Long, List<AuditLogDTO>> mockAuditDataMap = new HashMap<>();
	    mockAuditDataMap.put(0L, mockAuditLog); 

	    Mockito.when(auditLogService.searchAuditLogs("login", null, pageable)).thenReturn(mockAuditDataMap);

	    mockMvc.perform(MockMvcRequestBuilders.get("/api/audit/searchByParams")
	            .param("activityType", "login")
	            .param("page", "0")
	            .param("size", "10")
	            .contentType(MediaType.APPLICATION_JSON))
	        .andExpect(MockMvcResultMatchers.status().isOk())
	        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
	        .andExpect(jsonPath("$.success").value(true))
	        .andExpect(jsonPath("$.data[0].auditId").value(1))
	        .andDo(print());
	}

	@Test
	void testSearchAuditLogsWithUserIdOnly() throws Exception {
	    Pageable pageable = PageRequest.of(0, 10, Sort.by("lastupdatedDate").ascending());
	    Map<Long, List<AuditLogDTO>> mockAuditDataMap = new HashMap<>();
	    mockAuditDataMap.put(1L, mockAuditLog.subList(1, 2)); 

	    Mockito.when(auditLogService.searchAuditLogs(null, "12345", pageable)).thenReturn(mockAuditDataMap);

	    mockMvc.perform(MockMvcRequestBuilders.get("/api/audit/searchByParams")
	            .param("userId", "12345")
	            .param("page", "0")
	            .param("size", "10")
	            .contentType(MediaType.APPLICATION_JSON))
	        .andExpect(MockMvcResultMatchers.status().isOk())
	        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
	        .andExpect(jsonPath("$.success").value(true))
	        .andExpect(jsonPath("$.data[0].auditId").value(2))
	        .andDo(print());
	}

	@Test
	void testSearchAuditLogsNoParams() throws Exception {
	    Pageable pageable = PageRequest.of(0, 10, Sort.by("lastupdatedDate").ascending());

	    Mockito.when(auditLogService.searchAuditLogs(null, null, pageable)).thenReturn(new HashMap<>());

	    mockMvc.perform(MockMvcRequestBuilders.get("/api/audit/searchByParams")
	            .param("page", "0")
	            .param("size", "10")
	            .contentType(MediaType.APPLICATION_JSON))
	        .andExpect(MockMvcResultMatchers.status().isNotFound())
	        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
	        .andExpect(jsonPath("$.success").value(false))
	        .andExpect(jsonPath("$.message").value("No audit logs found."))
	        .andDo(print());
	}


}

package com.ai.chat.controller;

import com.ai.chat.common.enums.ResultCode;
import com.ai.chat.common.exception.BusinessException;
import com.ai.chat.common.handler.GlobalExceptionHandler;
import com.ai.chat.controller.ConversationController;
import com.ai.chat.pojo.dto.ConversationDTO;
import com.ai.chat.pojo.dto.ConversationUpdateDTO;
import com.ai.chat.pojo.vo.ConversationVo;
import com.ai.chat.service.IConversationService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc // 自动配置 MockMvc
@ActiveProfiles("test") // 激活 application-test.yml，走 H2 内存数据库，防止连不上真实库
@Import(GlobalExceptionHandler.class)
@DisplayName("ConversationController 接口行为测试")
class ConversationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IConversationService conversationService;

    private ConversationVo mockVo;

    @BeforeEach
    void setUp() {
        mockVo = new ConversationVo();
        mockVo.setId(1L);
        mockVo.setConversationId("conv-001");
        mockVo.setTitle("测试会话");
        mockVo.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("创建会话 - 成功")
    void shouldCreateConversationSuccessfully() throws Exception {
        ConversationDTO dto = new ConversationDTO();
        dto.setConversationId("conv-001");
        dto.setTitle("测试会话");

        when(conversationService.createConversation(any())).thenReturn(mockVo);

        mockMvc.perform(post("/api/conversation/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.conversationId").value("conv-001"));
    }

    @Test
    @DisplayName("创建会话 - 会话ID为空(参数校验拦截)")
    void shouldReturnBadRequestWhenConversationIdBlank() throws Exception {
        ConversationDTO dto = new ConversationDTO();
        dto.setConversationId(""); // 触发 @NotBlank 校验

        mockMvc.perform(post("/api/conversation/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("conversationId 不能为空")));
    }

    @Test
    @DisplayName("分页查询会话 - 成功")
    void shouldPageConversationsSuccessfully() throws Exception {
        IPage<ConversationVo> page = new Page<>(1, 10);
        page.setRecords(Collections.singletonList(mockVo));
        page.setTotal(1);

        when(conversationService.pageQuery(1, 10, null)).thenReturn(page);

        mockMvc.perform(get("/api/conversation/page")
                        .param("current", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.records[0].conversationId").value("conv-001"));
    }

    @Test
    @DisplayName("批量删除 - ID列表为空")
    void shouldReturnBadRequestWhenBatchDeleteIdsEmpty() throws Exception {
        mockMvc.perform(delete("/api/conversation/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[]")) // 触发 @NotEmpty 校验
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("删除ID列表不能为空")));
    }
}
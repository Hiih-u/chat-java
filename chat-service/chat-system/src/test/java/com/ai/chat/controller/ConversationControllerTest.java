package com.ai.chat.controller;

import com.ai.chat.system.pojo.dto.ConversationDTO;
import com.ai.chat.system.pojo.dto.ConversationUpdateDTO;
import com.ai.chat.system.pojo.vo.ConversationVo;
import com.ai.chat.service.IConversationService;
import com.ai.chat.common.enums.ResultCode;
import com.ai.chat.common.exception.BusinessException;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ConversationController 单元测试
 * 使用 @WebMvcTest 只加载 Web 层，Mock Service 层
 */
@WebMvcTest(ConversationController.class)
@DisplayName("Controller 单元测试 - 会话管理接口")
class ConversationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IConversationService conversationService;

    private ConversationVo mockVo;
    private ConversationDTO mockDTO;

    @BeforeEach
    void setUp() {
        mockVo = new ConversationVo();
        mockVo.setId(1L);
        mockVo.setConversationId("conv-001");
        mockVo.setTitle("测试会话");
        mockVo.setCreatedAt(LocalDateTime.now());

        mockDTO = new ConversationDTO();
        mockDTO.setConversationId("conv-001");
        mockDTO.setTitle("测试会话");
    }

    @Test
    @DisplayName("POST /api/conversation/create - 成功创建会话")
    void shouldCreateConversationSuccessfully() throws Exception {
        when(conversationService.createConversation(any(ConversationDTO.class)))
                .thenReturn(mockVo);

        mockMvc.perform(post("/api/conversation/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.conversationId").value("conv-001"))
                .andExpect(jsonPath("$.data.title").value("测试会话"));

        verify(conversationService, times(1)).createConversation(any(ConversationDTO.class));
    }

    @Test
    @DisplayName("POST /api/conversation/create - conversationId 为空时参数校验失败")
    void shouldReturnBadRequestWhenConversationIdBlank() throws Exception {
        ConversationDTO invalidDTO = new ConversationDTO();
        invalidDTO.setConversationId(""); // 触发 @NotBlank 校验

        mockMvc.perform(post("/api/conversation/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("conversationId 不能为空")));

        verify(conversationService, never()).createConversation(any());
    }

    @Test
    @DisplayName("POST /api/conversation/create - conversationId 已存在时抛出业务异常")
    void shouldReturnBadRequestWhenConversationIdExists() throws Exception {
        when(conversationService.createConversation(any(ConversationDTO.class)))
                .thenThrow(new BusinessException(ResultCode.BAD_REQUEST, "conversationId 已存在"));

        mockMvc.perform(post("/api/conversation/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message", containsString("conversationId 已存在")));
    }

    @Test
    @DisplayName("GET /api/conversation/{conversationId} - 成功获取会话详情")
    void shouldGetConversationDetailsSuccessfully() throws Exception {
        when(conversationService.getDetails("conv-001")).thenReturn(mockVo);

        mockMvc.perform(get("/api/conversation/{conversationId}", "conv-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.conversationId").value("conv-001"));

        verify(conversationService, times(1)).getDetails("conv-001");
    }

    @Test
    @DisplayName("GET /api/conversation/{conversationId} - 会话不存在时返回 404")
    void shouldReturn404WhenConversationNotFound() throws Exception {
        when(conversationService.getDetails("non-existent"))
                .thenThrow(new BusinessException(ResultCode.NOT_FOUND, "会话不存在"));

        mockMvc.perform(get("/api/conversation/{conversationId}", "non-existent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message", containsString("会话不存在")));
    }

    @Test
    @DisplayName("GET /api/conversation/page - 成功分页查询")
    void shouldPageConversationsSuccessfully() throws Exception {
        IPage<ConversationVo> page = new Page<>(1, 10);
        page.setRecords(Collections.singletonList(mockVo));
        page.setTotal(1);

        when(conversationService.pageQuery(1, 10, null)).thenReturn(page);

        mockMvc.perform(get("/api/conversation/page")
                        .param("current", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.records[0].conversationId").value("conv-001"));

        verify(conversationService, times(1)).pageQuery(1, 10, null);
    }

    @Test
    @DisplayName("GET /api/conversation/page - 带关键词分页查询")
    void shouldPageConversationsWithKeyword() throws Exception {
        IPage<ConversationVo> page = new Page<>(1, 10);
        page.setRecords(Collections.singletonList(mockVo));
        page.setTotal(1);

        when(conversationService.pageQuery(1, 10, "测试")).thenReturn(page);

        mockMvc.perform(get("/api/conversation/page")
                        .param("current", "1")
                        .param("size", "10")
                        .param("keyword", "测试"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.total").value(1));

        verify(conversationService, times(1)).pageQuery(1, 10, "测试");
    }

    @Test
    @DisplayName("GET /api/conversation/page - 页码小于1时参数校验失败")
    void shouldReturnBadRequestWhenPageNumberInvalid() throws Exception {
        mockMvc.perform(get("/api/conversation/page")
                        .param("current", "0")
                        .param("size", "10"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("页码必须大于0")));

        verify(conversationService, never()).pageQuery(anyInt(), anyInt(), any());
    }

    @Test
    @DisplayName("GET /api/conversation/list - 成功查询所有会话")
    void shouldListAllConversationsSuccessfully() throws Exception {
        when(conversationService.listAll()).thenReturn(Collections.singletonList(mockVo));

        mockMvc.perform(get("/api/conversation/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].conversationId").value("conv-001"));

        verify(conversationService, times(1)).listAll();
    }

    @Test
    @DisplayName("PUT /api/conversation/{id} - 成功更新会话")
    void shouldUpdateConversationSuccessfully() throws Exception {
        ConversationUpdateDTO updateDTO = new ConversationUpdateDTO();
        updateDTO.setTitle("更新后的标题");

        mockVo.setTitle("更新后的标题");
        when(conversationService.updateConversation(eq(1L), any(ConversationUpdateDTO.class)))
                .thenReturn(mockVo);

        mockMvc.perform(put("/api/conversation/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.title").value("更新后的标题"));

        verify(conversationService, times(1)).updateConversation(eq(1L), any(ConversationUpdateDTO.class));
    }

    @Test
    @DisplayName("PUT /api/conversation/{id} - 会话不存在时返回 404")
    void shouldReturn404WhenUpdateNonExistentConversation() throws Exception {
        ConversationUpdateDTO updateDTO = new ConversationUpdateDTO();
        updateDTO.setTitle("新标题");

        when(conversationService.updateConversation(eq(999L), any(ConversationUpdateDTO.class)))
                .thenThrow(new BusinessException(ResultCode.NOT_FOUND, "会话不存在"));

        mockMvc.perform(put("/api/conversation/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    @DisplayName("DELETE /api/conversation/{id} - 成功删除会话")
    void shouldDeleteConversationSuccessfully() throws Exception {
        doNothing().when(conversationService).deleteConversation(1L);

        mockMvc.perform(delete("/api/conversation/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(conversationService, times(1)).deleteConversation(1L);
    }

    @Test
    @DisplayName("DELETE /api/conversation/{id} - 会话不存在时返回 404")
    void shouldReturn404WhenDeleteNonExistentConversation() throws Exception {
        doThrow(new BusinessException(ResultCode.NOT_FOUND, "会话不存在"))
                .when(conversationService).deleteConversation(999L);

        mockMvc.perform(delete("/api/conversation/{id}", 999L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    @DisplayName("DELETE /api/conversation/batch - 成功批量删除")
    void shouldBatchDeleteConversationsSuccessfully() throws Exception {
        doNothing().when(conversationService).batchDeleteConversation(anyList());

        mockMvc.perform(delete("/api/conversation/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[1, 2, 3]"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(conversationService, times(1)).batchDeleteConversation(anyList());
    }

    @Test
    @DisplayName("DELETE /api/conversation/batch - ID列表为空时参数校验失败")
    void shouldReturnBadRequestWhenBatchDeleteIdsEmpty() throws Exception {
        mockMvc.perform(delete("/api/conversation/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[]"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("删除ID列表不能为空")));

        verify(conversationService, never()).batchDeleteConversation(anyList());
    }
}

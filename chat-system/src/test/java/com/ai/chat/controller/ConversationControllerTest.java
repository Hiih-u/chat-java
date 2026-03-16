package com.ai.chat.controller;

import com.ai.chat.common.enums.ResultCode;
import com.ai.chat.common.exception.BusinessException;
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
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ConversationController 单元测试
 * 使用 MockMvc 进行 Controller 层测试
 */
@WebMvcTest(ConversationController.class)
@DisplayName("会话控制器测试")
class ConversationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IConversationService conversationService;

    private ConversationVo conversationVo;
    private ConversationDTO conversationDTO;
    private ConversationUpdateDTO updateDTO;

    @BeforeEach
    void setUp() {
        conversationVo = new ConversationVo();
        conversationVo.setId(1L);
        conversationVo.setConversationId("test-conv-001");
        conversationVo.setTitle("测试会话");
        conversationVo.setCreatedAt(LocalDateTime.now());
        conversationVo.setUpdatedAt(LocalDateTime.now());

        conversationDTO = new ConversationDTO();
        conversationDTO.setConversationId("test-conv-001");
        conversationDTO.setTitle("测试会话");

        updateDTO = new ConversationUpdateDTO();
        updateDTO.setTitle("更新后的标题");
    }

    @Test
    @DisplayName("创建会话 - 成功")
    void testCreateConversation_Success() throws Exception {
        // Given
        when(conversationService.createConversation(any(ConversationDTO.class)))
                .thenReturn(conversationVo);

        // When & Then
        mockMvc.perform(post("/api/conversation/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(conversationDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.conversationId").value("test-conv-001"))
                .andExpect(jsonPath("$.data.title").value("测试会话"));
    }

    @Test
    @DisplayName("创建会话 - conversationId 已存在")
    void testCreateConversation_ConversationIdExists() throws Exception {
        // Given
        when(conversationService.createConversation(any(ConversationDTO.class)))
                .thenThrow(new BusinessException(ResultCode.BAD_REQUEST, "conversationId 已存在"));

        // When & Then
        mockMvc.perform(post("/api/conversation/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(conversationDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    @DisplayName("创建会话 - 参数校验失败")
    void testCreateConversation_ValidationFailed() throws Exception {
        // Given
        ConversationDTO invalidDTO = new ConversationDTO();
        // conversationId 为空，应该校验失败

        // When & Then
        mockMvc.perform(post("/api/conversation/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    @DisplayName("根据 conversationId 获取会话 - 成功")
    void testGetByConversationId_Success() throws Exception {
        // Given
        when(conversationService.getDetails(anyString())).thenReturn(conversationVo);

        // When & Then
        mockMvc.perform(get("/api/conversation/{conversationId}", "test-conv-001"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.conversationId").value("test-conv-001"));
    }

    @Test
    @DisplayName("根据 conversationId 获取会话 - 不存在")
    void testGetByConversationId_NotFound() throws Exception {
        // Given
        when(conversationService.getDetails(anyString()))
                .thenThrow(new BusinessException(ResultCode.NOT_FOUND, "会话不存在"));

        // When & Then
        mockMvc.perform(get("/api/conversation/{conversationId}", "non-existent"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    @DisplayName("分页查询会话 - 成功")
    void testPageQuery_Success() throws Exception {
        // Given
        IPage<ConversationVo> page = new Page<>(1, 10);
        page.setRecords(Arrays.asList(conversationVo));
        page.setTotal(1);

        when(conversationService.pageQuery(anyInt(), anyInt(), any()))
                .thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/conversation/page")
                        .param("current", "1")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.records[0].conversationId").value("test-conv-001"));
    }

    @Test
    @DisplayName("分页查询会话 - 带关键词")
    void testPageQuery_WithKeyword() throws Exception {
        // Given
        IPage<ConversationVo> page = new Page<>(1, 10);
        page.setRecords(Arrays.asList(conversationVo));
        page.setTotal(1);

        when(conversationService.pageQuery(anyInt(), anyInt(), anyString()))
                .thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/conversation/page")
                        .param("current", "1")
                        .param("size", "10")
                        .param("keyword", "测试"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1));
    }

    @Test
    @DisplayName("分页查询会话 - 参数校验失败（页码小于1）")
    void testPageQuery_InvalidPageNumber() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/conversation/page")
                        .param("current", "0")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    @DisplayName("查询所有会话 - 成功")
    void testListAll_Success() throws Exception {
        // Given
        List<ConversationVo> list = Arrays.asList(conversationVo);
        when(conversationService.listAll()).thenReturn(list);

        // When & Then
        mockMvc.perform(get("/api/conversation/list"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].conversationId").value("test-conv-001"));
    }

    @Test
    @DisplayName("更新会话 - 成功")
    void testUpdateConversation_Success() throws Exception {
        // Given
        conversationVo.setTitle("更新后的标题");
        when(conversationService.updateConversation(anyLong(), any(ConversationUpdateDTO.class)))
                .thenReturn(conversationVo);

        // When & Then
        mockMvc.perform(put("/api/conversation/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.title").value("更新后的标题"));
    }

    @Test
    @DisplayName("更新会话 - 会话不存在")
    void testUpdateConversation_NotFound() throws Exception {
        // Given
        when(conversationService.updateConversation(anyLong(), any(ConversationUpdateDTO.class)))
                .thenThrow(new BusinessException(ResultCode.NOT_FOUND, "会话不存在"));

        // When & Then
        mockMvc.perform(put("/api/conversation/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    @DisplayName("删除会话 - 成功")
    void testDeleteConversation_Success() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/conversation/{id}", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("删除会话 - 会话不存在")
    void testDeleteConversation_NotFound() throws Exception {
        // Given
        doThrow(new BusinessException(ResultCode.NOT_FOUND, "会话不存在"))
                .when(conversationService).deleteConversation(anyLong());

        // When & Then
        mockMvc.perform(delete("/api/conversation/{id}", 999L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    @DisplayName("批量删除会话 - 成功")
    void testBatchDeleteConversation_Success() throws Exception {
        // Given
        List<Long> ids = Arrays.asList(1L, 2L, 3L);

        // When & Then
        mockMvc.perform(delete("/api/conversation/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("批量删除会话 - ID列表为空")
    void testBatchDeleteConversation_EmptyList() throws Exception {
        // Given
        List<Long> ids = Arrays.asList();

        // When & Then
        mockMvc.perform(delete("/api/conversation/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));
    }
}

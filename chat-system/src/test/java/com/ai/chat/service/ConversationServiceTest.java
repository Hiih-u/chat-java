package com.ai.chat.service;

import com.ai.chat.common.enums.ResultCode;
import com.ai.chat.common.exception.BusinessException;
import com.ai.chat.mapper.ConversationMapper;
import com.ai.chat.pojo.dto.ConversationDTO;
import com.ai.chat.pojo.dto.ConversationUpdateDTO;
import com.ai.chat.pojo.entity.Conversation;
import com.ai.chat.pojo.vo.ConversationVo;
import com.ai.chat.service.impl.ConversationServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * ConversationService 单元测试
 * 使用 JUnit 5 + Mockito 进行测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("会话服务测试")
class ConversationServiceTest {

    @Mock
    private ConversationMapper conversationMapper;

    @InjectMocks
    private ConversationServiceImpl conversationService;

    private ConversationDTO conversationDTO;
    private Conversation conversation;
    private ConversationUpdateDTO updateDTO;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        conversationDTO = new ConversationDTO();
        conversationDTO.setConversationId("test-conv-001");
        conversationDTO.setTitle("测试会话");

        conversation = new Conversation();
        conversation.setId(1L);
        conversation.setConversationId("test-conv-001");
        conversation.setTitle("测试会话");
        conversation.setCreatedAt(LocalDateTime.now());
        conversation.setUpdatedAt(LocalDateTime.now());

        updateDTO = new ConversationUpdateDTO();
        updateDTO.setTitle("更新后的标题");
    }

    @Test
    @DisplayName("创建会话 - 成功")
    void testCreateConversation_Success() {
        // Given
        when(conversationMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(conversationMapper.insert(any(Conversation.class))).thenReturn(1);

        // When
        ConversationVo result = conversationService.createConversation(conversationDTO);

        // Then
        assertNotNull(result);
        assertEquals("test-conv-001", result.getConversationId());
        verify(conversationMapper, times(1)).insert(any(Conversation.class));
    }

    @Test
    @DisplayName("创建会话 - conversationId 已存在")
    void testCreateConversation_ConversationIdExists() {
        // Given
        when(conversationMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            conversationService.createConversation(conversationDTO);
        });

        assertEquals(ResultCode.BAD_REQUEST, exception.getCode());
        assertTrue(exception.getMessage().contains("conversationId 已存在"));
        verify(conversationMapper, never()).insert(any(Conversation.class));
    }

    @Test
    @DisplayName("根据 conversationId 获取会话 - 成功")
    void testGetByConversationId_Success() {
        // Given
        when(conversationMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(conversation);

        // When
        ConversationVo result = conversationService.getByConversationId("test-conv-001");

        // Then
        assertNotNull(result);
        assertEquals("test-conv-001", result.getConversationId());
        assertEquals("测试会话", result.getTitle());
    }

    @Test
    @DisplayName("根据 conversationId 获取会话 - 不存在")
    void testGetByConversationId_NotFound() {
        // Given
        when(conversationMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        // When
        ConversationVo result = conversationService.getByConversationId("non-existent");

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("分页查询会话 - 无关键词")
    void testPageQuery_WithoutKeyword() {
        // Given
        Page<Conversation> page = new Page<>(1, 10);
        page.setRecords(Arrays.asList(conversation));
        page.setTotal(1);

        when(conversationMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                .thenReturn(page);

        // When
        IPage<ConversationVo> result = conversationService.pageQuery(1, 10, null);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getRecords().size());
    }

    @Test
    @DisplayName("分页查询会话 - 带关键词")
    void testPageQuery_WithKeyword() {
        // Given
        Page<Conversation> page = new Page<>(1, 10);
        page.setRecords(Arrays.asList(conversation));
        page.setTotal(1);

        when(conversationMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                .thenReturn(page);

        // When
        IPage<ConversationVo> result = conversationService.pageQuery(1, 10, "测试");

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotal());
    }

    @Test
    @DisplayName("获取会话详情 - 成功")
    void testGetDetails_Success() {
        // Given
        when(conversationMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(conversation);

        // When
        ConversationVo result = conversationService.getDetails("test-conv-001");

        // Then
        assertNotNull(result);
        assertEquals("test-conv-001", result.getConversationId());
    }

    @Test
    @DisplayName("获取会话详情 - 不存在")
    void testGetDetails_NotFound() {
        // Given
        when(conversationMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            conversationService.getDetails("non-existent");
        });

        assertEquals(ResultCode.NOT_FOUND, exception.getCode());
        assertTrue(exception.getMessage().contains("会话不存在"));
    }

    @Test
    @DisplayName("更新会话 - 成功")
    void testUpdateConversation_Success() {
        // Given
        when(conversationMapper.selectById(1L)).thenReturn(conversation);
        when(conversationMapper.updateById(any(Conversation.class))).thenReturn(1);

        // When
        ConversationVo result = conversationService.updateConversation(1L, updateDTO);

        // Then
        assertNotNull(result);
        assertEquals("更新后的标题", result.getTitle());
        verify(conversationMapper, times(1)).updateById(any(Conversation.class));
    }

    @Test
    @DisplayName("更新会话 - 会话不存在")
    void testUpdateConversation_NotFound() {
        // Given
        when(conversationMapper.selectById(1L)).thenReturn(null);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            conversationService.updateConversation(1L, updateDTO);
        });

        assertEquals(ResultCode.NOT_FOUND, exception.getCode());
        verify(conversationMapper, never()).updateById(any(Conversation.class));
    }

    @Test
    @DisplayName("删除会话 - 成功")
    void testDeleteConversation_Success() {
        // Given
        when(conversationMapper.selectById(1L)).thenReturn(conversation);
        when(conversationMapper.deleteById(1L)).thenReturn(1);

        // When
        conversationService.deleteConversation(1L);

        // Then
        verify(conversationMapper, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("删除会话 - 会话不存在")
    void testDeleteConversation_NotFound() {
        // Given
        when(conversationMapper.selectById(1L)).thenReturn(null);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            conversationService.deleteConversation(1L);
        });

        assertEquals(ResultCode.NOT_FOUND, exception.getCode());
        verify(conversationMapper, never()).deleteById(eq(1L));
    }

    @Test
    @DisplayName("批量删除会话 - 成功")
    void testBatchDeleteConversation_Success() {
        // Given
        List<Long> ids = Arrays.asList(1L, 2L, 3L);
        when(conversationMapper.deleteBatchIds(ids)).thenReturn(3);

        // When
        conversationService.batchDeleteConversation(ids);

        // Then
        verify(conversationMapper, times(1)).deleteBatchIds(ids);
    }

    @Test
    @DisplayName("批量删除会话 - ID列表为空")
    void testBatchDeleteConversation_EmptyList() {
        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            conversationService.batchDeleteConversation(Arrays.asList());
        });

        assertEquals(ResultCode.BAD_REQUEST, exception.getCode());
        verify(conversationMapper, never()).deleteBatchIds(any());
    }

    @Test
    @DisplayName("查询所有会话")
    void testListAll() {
        // Given
        List<Conversation> conversations = Arrays.asList(conversation);
        when(conversationMapper.selectList(any())).thenReturn(conversations);

        // When
        List<ConversationVo> result = conversationService.listAll();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("test-conv-001", result.get(0).getConversationId());
    }
}

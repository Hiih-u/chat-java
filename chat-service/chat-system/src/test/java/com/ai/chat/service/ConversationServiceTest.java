package com.ai.chat.service;

import com.ai.chat.common.enums.ResultCode;
import com.ai.chat.common.exception.BusinessException;
import com.ai.chat.pojo.dto.ConversationDTO;
import com.ai.chat.pojo.dto.ConversationUpdateDTO;
import com.ai.chat.pojo.entity.Conversation;
import com.ai.chat.pojo.vo.ConversationVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional // 自动回滚，确保测试数据不污染 H2 数据库
@DisplayName("Service 集成测试 - 会话业务逻辑")
class ConversationServiceTest {

    @Autowired
    private IConversationService conversationService;

    // ✅ 移除对 ConversationMapper 的 @MockBean，使用真实的 Mapper 配合 H2

    // 屏蔽 Redis 环境
    @MockBean
    private RedisConnectionFactory redisConnectionFactory;
    @MockBean
    private ReactiveRedisConnectionFactory reactiveRedisConnectionFactory;

    private ConversationDTO mockDTO;

    @BeforeEach
    void setUp() {
        mockDTO = new ConversationDTO();
        mockDTO.setConversationId("conv-001");
        mockDTO.setTitle("测试会话");
    }

    @Test
    @DisplayName("创建会话 - 成功")
    void shouldCreateConversationSuccessfully() {
        ConversationVo result = conversationService.createConversation(mockDTO);

        assertThat(result).isNotNull();
        assertThat(result.getConversationId()).isEqualTo("conv-001");
    }

    @Test
    @DisplayName("创建会话 - conversationId 已存在时抛出异常")
    void shouldThrowExceptionWhenConversationIdExists() {
        // 先存入一条数据
        conversationService.createConversation(mockDTO);

        // ✅ 修复：比较 code 的 int 值
        assertThatThrownBy(() -> conversationService.createConversation(mockDTO))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("conversationId 已存在")
                .extracting("code").isEqualTo(ResultCode.BAD_REQUEST.getCode());
    }

    @Test
    @DisplayName("获取会话详情 - 不存在时抛出异常")
    void shouldThrowExceptionWhenGetDetailsNotFound() {
        // ✅ 修复：比较 code 的 int 值
        assertThatThrownBy(() -> conversationService.getDetails("non-existent"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("会话不存在")
                .extracting("code").isEqualTo(ResultCode.NOT_FOUND.getCode());
    }

    @Test
    @DisplayName("更新会话 - 成功")
    void shouldUpdateConversationSuccessfully() {
        // 准备数据
        ConversationVo created = conversationService.createConversation(mockDTO);

        ConversationUpdateDTO updateDTO = new ConversationUpdateDTO();
        updateDTO.setTitle("更新后的标题");

        // 执行更新
        ConversationVo result = conversationService.updateConversation(created.getId(), updateDTO);

        assertThat(result.getTitle()).isEqualTo("更新后的标题");
    }

    @Test
    @DisplayName("删除会话 - 成功")
    void shouldDeleteConversationSuccessfully() {
        // 准备数据
        ConversationVo created = conversationService.createConversation(mockDTO);

        // ✅ 此时调用 removeById 不再报 tableInfo null，因为使用的是真实 Mapper
        conversationService.deleteConversation(created.getId());

        // 验证已删除 (getDetails 会抛异常)
        assertThatThrownBy(() -> conversationService.getDetails(created.getConversationId()))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("分页查询 - 带关键词")
    void shouldPageQueryWithKeyword() {
        conversationService.createConversation(mockDTO);

        IPage<ConversationVo> result = conversationService.pageQuery(1, 10, "测试");

        assertThat(result.getTotal()).isGreaterThanOrEqualTo(1);
        assertThat(result.getRecords())
                .extracting(ConversationVo::getTitle)
                .anyMatch(title -> title.contains("测试"));
    }
}
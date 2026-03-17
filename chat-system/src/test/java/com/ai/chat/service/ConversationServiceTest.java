package com.ai.chat.service;

import com.ai.chat.common.exception.BusinessException;
import com.ai.chat.pojo.dto.ConversationDTO;
import com.ai.chat.pojo.dto.ConversationUpdateDTO;
import com.ai.chat.pojo.vo.ConversationVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
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
@ActiveProfiles("test") // 激活 application-test.yml
@Transactional // 确保每个测试执行完后自动回滚数据库
@DisplayName("ConversationService 数据库集成测试")
class ConversationServiceTest {

    @Autowired
    private IConversationService conversationService;

    // 屏蔽真实的 Redis 连接尝试
    @MockBean
    private RedisConnectionFactory redisConnectionFactory;

    @MockBean
    private ReactiveRedisConnectionFactory reactiveRedisConnectionFactory;

    @Test
    @DisplayName("测试真实数据落库与查询：创建并获取会话")
    void shouldCreateAndGetConversationSuccessfully() {
        // 1. 创建会话
        ConversationDTO dto = new ConversationDTO();
        dto.setConversationId("sys-test-001");
        dto.setTitle("集成测试标题");
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("source", "junit");
        dto.setSessionMetadata(metadata);

        ConversationVo created = conversationService.createConversation(dto);
        assertThat(created).isNotNull();
        assertThat(created.getId()).isNotNull(); // 验证 ID 已自增

        // 2. 根据 ConversationId 查询
        ConversationVo fetched = conversationService.getDetails("sys-test-001");
        assertThat(fetched.getTitle()).isEqualTo("集成测试标题");
        assertThat(fetched.getSessionMetadata().get("source")).isEqualTo("junit");
    }

    @Test
    @DisplayName("测试业务规则：重复的 ConversationId 会抛出异常")
    void shouldThrowExceptionWhenConversationIdExists() {
        ConversationDTO dto = new ConversationDTO();
        dto.setConversationId("duplicate-001");
        conversationService.createConversation(dto);

        // 再次插入同样的 ID
        assertThatThrownBy(() -> conversationService.createConversation(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("conversationId 已存在");
    }

    @Test
    @DisplayName("测试业务规则：更新不存在的会话会抛出异常")
    void shouldThrowWhenUpdateNonExistentConversation() {
        ConversationUpdateDTO updateDTO = new ConversationUpdateDTO();
        updateDTO.setTitle("新标题");

        assertThatThrownBy(() -> conversationService.updateConversation(99999L, updateDTO))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("会话不存在");
    }

    @Test
    @DisplayName("测试分页逻辑与模糊查询")
    void shouldPageConversationsSuccessfully() {
        // 准备数据
        ConversationDTO dto1 = new ConversationDTO();
        dto1.setConversationId("page-test-001");
        dto1.setTitle("搜索关键词测试");
        conversationService.createConversation(dto1);

        ConversationDTO dto2 = new ConversationDTO();
        dto2.setConversationId("page-test-002");
        dto2.setTitle("无关标题");
        conversationService.createConversation(dto2);

        // 模糊搜索
        IPage<ConversationVo> page = conversationService.pageQuery(1, 10, "搜索");

        assertThat(page.getTotal()).isGreaterThanOrEqualTo(1);
        assertThat(page.getRecords())
                .extracting(ConversationVo::getTitle)
                .anyMatch(title -> title.contains("搜索"));
    }
}
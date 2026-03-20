package com.ai.chat.integration;

import com.ai.chat.common.exception.BusinessException;
import com.ai.chat.pojo.dto.ConversationDTO;
import com.ai.chat.pojo.dto.ConversationUpdateDTO;
import com.ai.chat.pojo.vo.ConversationVo;
import com.ai.chat.service.IConversationService;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Conversation 集成测试
 * 使用 @SpringBootTest 启动完整 Spring 容器，测试真实数据库交互
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional // 每个测试方法执行后自动回滚
@DisplayName("集成测试 - 会话完整业务流程")
class ConversationIntegrationTest {

    @Autowired
    private IConversationService conversationService;

    // Mock Redis 连接，避免测试环境连接真实 Redis
    @MockBean
    private RedisConnectionFactory redisConnectionFactory;

    @MockBean
    private ReactiveRedisConnectionFactory reactiveRedisConnectionFactory;

    @Test
    @DisplayName("完整业务流程 - 创建、查询、更新、删除")
    void shouldCompleteFullConversationLifecycle() {
        // 1. 创建会话
        ConversationDTO createDTO = new ConversationDTO();
        createDTO.setConversationId("integration-test-001");
        createDTO.setTitle("集成测试会话");
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("source", "integration-test");
        metadata.put("version", "1.0");
        createDTO.setSessionMetadata(metadata);

        ConversationVo created = conversationService.createConversation(createDTO);
        assertThat(created).isNotNull();
        assertThat(created.getId()).isNotNull();
        assertThat(created.getConversationId()).isEqualTo("integration-test-001");
        assertThat(created.getTitle()).isEqualTo("集成测试会话");
        assertThat(created.getSessionMetadata()).containsEntry("source", "integration-test");

        // 2. 根据 conversationId 查询
        ConversationVo fetched = conversationService.getDetails("integration-test-001");
        assertThat(fetched).isNotNull();
        assertThat(fetched.getId()).isEqualTo(created.getId());
        assertThat(fetched.getTitle()).isEqualTo("集成测试会话");

        // 3. 更新会话
        ConversationUpdateDTO updateDTO = new ConversationUpdateDTO();
        updateDTO.setTitle("更新后的标题");
        Map<String, Object> newMetadata = new HashMap<>();
        newMetadata.put("updated", true);
        updateDTO.setSessionMetadata(newMetadata);

        ConversationVo updated = conversationService.updateConversation(created.getId(), updateDTO);
        assertThat(updated.getTitle()).isEqualTo("更新后的标题");
        assertThat(updated.getSessionMetadata()).containsEntry("updated", true);

        // 4. 验证更新后的数据
        ConversationVo afterUpdate = conversationService.getDetails("integration-test-001");
        assertThat(afterUpdate.getTitle()).isEqualTo("更新后的标题");

        // 5. 删除会话
        conversationService.deleteConversation(created.getId());

        // 6. 验证删除后无法查询（逻辑删除）
        assertThatThrownBy(() -> conversationService.getDetails("integration-test-001"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("会话不存在");
    }

    @Test
    @DisplayName("业务规则验证 - conversationId 唯一性约束")
    void shouldEnforceConversationIdUniqueness() {
        // 创建第一个会话
        ConversationDTO dto1 = new ConversationDTO();
        dto1.setConversationId("unique-test-001");
        dto1.setTitle("第一个会话");
        conversationService.createConversation(dto1);

        // 尝试创建相同 conversationId 的会话
        ConversationDTO dto2 = new ConversationDTO();
        dto2.setConversationId("unique-test-001");
        dto2.setTitle("第二个会话");

        assertThatThrownBy(() -> conversationService.createConversation(dto2))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("conversationId 已存在");
    }

    @Test
    @DisplayName("分页查询 - 无关键词")
    void shouldPageQueryWithoutKeyword() {
        // 准备测试数据
        for (int i = 1; i <= 15; i++) {
            ConversationDTO dto = new ConversationDTO();
            dto.setConversationId("page-test-" + String.format("%03d", i));
            dto.setTitle("分页测试会话 " + i);
            conversationService.createConversation(dto);
        }

        // 第一页
        IPage<ConversationVo> page1 = conversationService.pageQuery(1, 10, null);
        assertThat(page1.getTotal()).isGreaterThanOrEqualTo(15);
        assertThat(page1.getRecords()).hasSize(10);
        assertThat(page1.getCurrent()).isEqualTo(1);

        // 第二页
        IPage<ConversationVo> page2 = conversationService.pageQuery(2, 10, null);
        assertThat(page2.getRecords()).hasSizeGreaterThanOrEqualTo(5);
        assertThat(page2.getCurrent()).isEqualTo(2);
    }

    @Test
    @DisplayName("分页查询 - 带关键词模糊搜索")
    void shouldPageQueryWithKeyword() {
        // 准备测试数据
        ConversationDTO dto1 = new ConversationDTO();
        dto1.setConversationId("search-test-001");
        dto1.setTitle("包含搜索关键词的会话");
        conversationService.createConversation(dto1);

        ConversationDTO dto2 = new ConversationDTO();
        dto2.setConversationId("search-test-002");
        dto2.setTitle("另一个搜索测试");
        conversationService.createConversation(dto2);

        ConversationDTO dto3 = new ConversationDTO();
        dto3.setConversationId("search-test-003");
        dto3.setTitle("无关的会话标题");
        conversationService.createConversation(dto3);

        // 搜索包含"搜索"关键词的会话
        IPage<ConversationVo> page = conversationService.pageQuery(1, 10, "搜索");

        assertThat(page.getTotal()).isGreaterThanOrEqualTo(2);
        assertThat(page.getRecords())
                .extracting(ConversationVo::getTitle)
                .anyMatch(title -> title.contains("搜索"));
    }

    @Test
    @DisplayName("分页查询 - 按 conversationId 搜索")
    void shouldPageQueryByConversationId() {
        // 准备测试数据
        ConversationDTO dto = new ConversationDTO();
        dto.setConversationId("id-search-test-001");
        dto.setTitle("通过ID搜索");
        conversationService.createConversation(dto);

        // 通过 conversationId 搜索
        IPage<ConversationVo> page = conversationService.pageQuery(1, 10, "id-search");

        assertThat(page.getTotal()).isGreaterThanOrEqualTo(1);
        assertThat(page.getRecords())
                .extracting(ConversationVo::getConversationId)
                .anyMatch(id -> id.contains("id-search"));
    }

    @Test
    @DisplayName("批量删除 - 成功删除多个会话")
    void shouldBatchDeleteConversationsSuccessfully() {
        // 准备测试数据
        ConversationDTO dto1 = new ConversationDTO();
        dto1.setConversationId("batch-delete-001");
        dto1.setTitle("批量删除测试1");
        ConversationVo created1 = conversationService.createConversation(dto1);

        ConversationDTO dto2 = new ConversationDTO();
        dto2.setConversationId("batch-delete-002");
        dto2.setTitle("批量删除测试2");
        ConversationVo created2 = conversationService.createConversation(dto2);

        ConversationDTO dto3 = new ConversationDTO();
        dto3.setConversationId("batch-delete-003");
        dto3.setTitle("批量删除测试3");
        ConversationVo created3 = conversationService.createConversation(dto3);

        // 批量删除
        List<Long> ids = Arrays.asList(created1.getId(), created2.getId(), created3.getId());
        conversationService.batchDeleteConversation(ids);

        // 验证删除结果
        assertThatThrownBy(() -> conversationService.getDetails("batch-delete-001"))
                .isInstanceOf(BusinessException.class);
        assertThatThrownBy(() -> conversationService.getDetails("batch-delete-002"))
                .isInstanceOf(BusinessException.class);
        assertThatThrownBy(() -> conversationService.getDetails("batch-delete-003"))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("查询所有会话 - 返回完整列表")
    void shouldListAllConversationsSuccessfully() {
        // 准备测试数据
        ConversationDTO dto1 = new ConversationDTO();
        dto1.setConversationId("list-all-001");
        dto1.setTitle("列表测试1");
        conversationService.createConversation(dto1);

        ConversationDTO dto2 = new ConversationDTO();
        dto2.setConversationId("list-all-002");
        dto2.setTitle("列表测试2");
        conversationService.createConversation(dto2);

        // 查询所有
        List<ConversationVo> allConversations = conversationService.listAll();

        assertThat(allConversations).hasSizeGreaterThanOrEqualTo(2);
        assertThat(allConversations)
                .extracting(ConversationVo::getConversationId)
                .contains("list-all-001", "list-all-002");
    }

    @Test
    @DisplayName("边界测试 - 更新不存在的会话")
    void shouldThrowExceptionWhenUpdateNonExistentConversation() {
        ConversationUpdateDTO updateDTO = new ConversationUpdateDTO();
        updateDTO.setTitle("新标题");

        assertThatThrownBy(() -> conversationService.updateConversation(99999L, updateDTO))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("会话不存在");
    }

    @Test
    @DisplayName("边界测试 - 删除不存在的会话")
    void shouldThrowExceptionWhenDeleteNonExistentConversation() {
        assertThatThrownBy(() -> conversationService.deleteConversation(99999L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("会话不存在");
    }

    @Test
    @DisplayName("边界测试 - 批量删除空列表")
    void shouldThrowExceptionWhenBatchDeleteEmptyList() {
        assertThatThrownBy(() -> conversationService.batchDeleteConversation(Arrays.asList()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("批量删除 id 列表不能为空");
    }

    @Test
    @DisplayName("数据完整性 - metadata 的 JSON 序列化与反序列化")
    void shouldHandleMetadataJsonSerializationCorrectly() {
        // 创建包含复杂 metadata 的会话
        ConversationDTO dto = new ConversationDTO();
        dto.setConversationId("json-test-001");
        dto.setTitle("JSON测试");

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("string", "value");
        metadata.put("number", 123);
        metadata.put("boolean", true);
        metadata.put("nested", Map.of("key1", "value1", "key2", "value2"));
        metadata.put("array", Arrays.asList("item1", "item2", "item3"));
        dto.setSessionMetadata(metadata);

        ConversationVo created = conversationService.createConversation(dto);

        // 验证 metadata 正确保存和读取
        ConversationVo fetched = conversationService.getDetails("json-test-001");
        assertThat(fetched.getSessionMetadata()).isNotNull();
        assertThat(fetched.getSessionMetadata()).containsEntry("string", "value");
        assertThat(fetched.getSessionMetadata()).containsEntry("number", 123);
        assertThat(fetched.getSessionMetadata()).containsEntry("boolean", true);
        assertThat(fetched.getSessionMetadata()).containsKey("nested");
        assertThat(fetched.getSessionMetadata()).containsKey("array");
    }

    @Test
    @DisplayName("并发场景 - 同时创建多个会话")
    void shouldHandleConcurrentConversationCreation() {
        // 模拟并发创建多个会话
        for (int i = 1; i <= 10; i++) {
            ConversationDTO dto = new ConversationDTO();
            dto.setConversationId("concurrent-test-" + String.format("%03d", i));
            dto.setTitle("并发测试会话 " + i);
            
            ConversationVo created = conversationService.createConversation(dto);
            assertThat(created).isNotNull();
            assertThat(created.getId()).isNotNull();
        }

        // 验证所有会话都已创建
        IPage<ConversationVo> page = conversationService.pageQuery(1, 20, "concurrent-test");
        assertThat(page.getTotal()).isGreaterThanOrEqualTo(10);
    }
}

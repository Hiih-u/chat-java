package com.ai.chat.controller;

import com.ai.chat.common.entity.Result;
import com.ai.chat.dto.request.ConversationCreateRequest;
import com.ai.chat.dto.request.ConversationUpdateRequest;
import com.ai.chat.dto.response.ConversationResponse;
import com.ai.chat.service.IConversationService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 会话管理控制器
 * 提供会话的 CRUD 操作接口
 */
@Slf4j
@RestController
@RequestMapping("/api/conversation")
@RequiredArgsConstructor
@Validated
public class ConversationController {

    private final IConversationService conversationService;

    /**
     * 创建会话
     *
     * @param request 会话创建请求
     * @return 创建的会话信息
     */
    @PostMapping("/create")
    public Result<ConversationResponse> create(@Validated @RequestBody ConversationCreateRequest request) {
        log.info("创建会话: conversationId={}", request.getConversationId());
        ConversationResponse response = conversationService.createConversation(request);
        log.info("会话创建成功: id={}", response.getId());
        return Result.success(response);
    }

    /**
     * 根据 conversationId 获取会话详情
     *
     * @param conversationId 会话唯一标识
     * @return 会话详情
     */
    @GetMapping("/{conversationId}")
    public Result<ConversationResponse> getByConversationId(@PathVariable String conversationId) {
        return Result.success(conversationService.getDetails(conversationId));
    }

    /**
     * 分页查询会话列表
     *
     * @param current 当前页码，从1开始
     * @param size 每页数量
     * @param keyword 搜索关键词（可选）
     * @return 分页结果
     */
    @GetMapping("/page")
    public Result<IPage<ConversationResponse>> page(
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码必须大于0") int current,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "每页数量必须大于0") int size,
            @RequestParam(required = false) String keyword) {
        IPage<ConversationResponse> page = conversationService.pageQuery(current, size, keyword);
        return Result.success(page);
    }

    /**
     * 查询所有会话
     *
     * @return 会话列表
     */
    @GetMapping("/list")
    public Result<List<ConversationResponse>> list() {
        List<ConversationResponse> list = conversationService.listAll();
        return Result.success(list);
    }

    /**
     * 更新会话
     *
     * @param id 会话数据库主键ID
     * @param request 更新请求
     * @return 更新后的会话信息
     */
    @PutMapping("/{id}")
    public Result<ConversationResponse> update(
            @PathVariable @Min(value = 1, message = "ID必须大于0") Long id,
            @Validated @RequestBody ConversationUpdateRequest request) {
        log.info("更新会话: id={}", id);
        ConversationResponse response = conversationService.updateConversation(id, request);
        log.info("会话更新成功: id={}", id);
        return Result.success(response);
    }

    /**
     * 删除会话（逻辑删除）
     *
     * @param id 会话数据库主键ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable @Min(value = 1, message = "ID必须大于0") Long id) {
        log.info("删除会话: id={}", id);
        conversationService.deleteConversation(id);
        log.info("会话删除成功: id={}", id);
        return Result.success();
    }

    /**
     * 批量删除会话（逻辑删除）
     *
     * @param ids 会话ID列表
     * @return 操作结果
     */
    @DeleteMapping("/batch")
    public Result<Void> batchDelete(
            @RequestBody @NotEmpty(message = "删除ID列表不能为空") List<@Min(value = 1, message = "ID必须大于0") Long> ids) {
        log.info("批量删除会话: count={}", ids.size());
        conversationService.batchDeleteConversation(ids);
        log.info("批量删除成功: count={}", ids.size());
        return Result.success();
    }

}

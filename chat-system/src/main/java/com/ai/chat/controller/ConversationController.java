package com.ai.chat.controller;

import com.ai.chat.common.entity.Result;
import com.ai.chat.dto.request.ConversationCreateRequest;
import com.ai.chat.dto.request.ConversationUpdateRequest;
import com.ai.chat.dto.response.ConversationResponse;
import com.ai.chat.service.IConversationService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/conversation")
@AllArgsConstructor
public class ConversationController {

    private final IConversationService conversationService;

    /**
     * 创建会话
     */
    @PostMapping("/create")
    public Result<ConversationResponse> create(@Validated @RequestBody ConversationCreateRequest request) {
        return Result.success(conversationService.createConversation(request));
    }

    /**
     * 根据 conversationId 获取会话详情
     */
    @GetMapping("/{conversationId}")
    public Result<ConversationResponse> getByConversationId(@PathVariable String conversationId) {
        return Result.success(conversationService.getDetails(conversationId));
    }

    /**
     * 分页查询会话列表
     */
    @GetMapping("/page")
    public Result<Page<ConversationResponse>> page(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        Page<ConversationResponse> page = conversationService.pageQuery(current, size, keyword);
        return Result.success(page);
    }

    /**
     * 查询所有会话
     */
    @GetMapping("/list")
    public Result<List<ConversationResponse>> list() {
        List<ConversationResponse> list = conversationService.listAll();
        return Result.success(list);
    }

    /**
     * 更新会话
     */
    @PutMapping("/{id}")
    public Result<ConversationResponse> update(
            @PathVariable Long id,
            @Validated @RequestBody ConversationUpdateRequest request) {
        return Result.success(conversationService.updateConversation(id, request));
    }

    /**
     * 删除会话
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        conversationService.deleteConversation(id);
        return Result.success();
    }

    /**
     * 批量删除会话
     */
    @DeleteMapping("/batch")
    public Result<Void> batchDelete(@Validated @RequestBody List<Long> ids) {
        conversationService.batchDeleteConversation(ids);
        return Result.success();
    }

}

package com.ai.chat.controller;

import com.ai.chat.common.Result;
import com.ai.chat.common.entity.Conversation;
import com.ai.chat.dto.request.ConversationCreateRequest;
import com.ai.chat.dto.request.ConversationUpdateRequest;
import com.ai.chat.service.IConversationService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/conversation")
@AllArgsConstructor
public class ConversationController {

    private IConversationService conversationService;

    // 创建会话
    @PostMapping("/create")
    public Result<Conversation> create(@Validated @RequestBody ConversationCreateRequest request) {
        Conversation conversation = new Conversation();
        BeanUtils.copyProperties(request, conversation);
        conversationService.save(conversation);
        return Result.success(conversation);
    }

    // 根据 conversationId 获取会话
    @GetMapping("/{conversationId}")
    public Result<Conversation> getByConversationId(@PathVariable String conversationId) {
        // 先尝试按 conversationId 查询
        Conversation conversation = conversationService.getByConversationId(conversationId);

        // 如果查不到且是数字，尝试按 id 查询
        if (conversation == null && conversationId.matches("\\d+")) {
            conversation = conversationService.getById(Long.parseLong(conversationId));
        }

        if (conversation == null){
            return Result.error("会话不存在");
        }
        return Result.success(conversation);
    }

    // 分页查询
    @GetMapping("/page")
    public Result<Page<Conversation>> page(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        Page<Conversation> page = conversationService.pageQuery(current, size, keyword);
        return Result.success(page);
    }

    // 查询所有
    @GetMapping("list")
    public Result<List<Conversation>> list() {
        List<Conversation> list = conversationService.list();
        return Result.success(list);
    }

    // 更新会话
    @PutMapping("/{id}")
    public Result<Conversation> update(
            @PathVariable Long id,
            @RequestBody ConversationUpdateRequest request) {
        Conversation conversation = conversationService.getById(id);
        if (conversation == null) {
            return Result.error("会话不存在");
        }

        if (request.getTitle() != null) {
            conversation.setTitle(request.getTitle());
        }
//        if (request.getSessionMetadata() != null) {
//            conversation.setSessionMetadata(request.getSessionMetadata());
//        }

        conversationService.updateById(conversation);
        return Result.success(conversation);
    }

    // 删除会话
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id){
        boolean success = conversationService.removeById(id);
        if (!success) {
            return Result.error("删除失败");
        }
        return Result.success(null);
    }


    // 批量删除
    @DeleteMapping("/batch")
    public Result<Void> batchDelete(@RequestBody List<Long> ids) {
        boolean success = conversationService.removeByIds(ids);
        if (!success) {
            return Result.error("批量删除失败");
        }
        return Result.success(null);
    }

}

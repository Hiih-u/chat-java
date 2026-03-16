package com.ai.chat.controller;

import com.ai.chat.common.pojo.entity.Result;
import com.ai.chat.pojo.dto.ConversationDTO;
import com.ai.chat.pojo.dto.ConversationUpdateDTO;
import com.ai.chat.pojo.vo.ConversationVo;
import com.ai.chat.service.IConversationService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "会话管理", description = "会话管理接口")
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
     * @param dto 会话创建DTO
     * @return 创建的会话信息
     */
    @Operation(summary = "创建会话", description = "创建一个新会话")
    @PostMapping("/create")
    public Result<ConversationVo> create(@Validated @RequestBody ConversationDTO dto) {
        log.info("创建会话: conversationId={}", dto.getConversationId());
        ConversationVo response = conversationService.createConversation(dto);
        log.info("会话创建成功: id={}", response.getId());
        return Result.success(response);
    }

    /**
     * 根据 conversationId 获取会话详情
     *
     * @param conversationId 会话唯一标识
     * @return 会话详情
     */
    @Operation(summary = "获取会话详情", description = "根据 conversationId 获取会话详情")
    @GetMapping("/{conversationId}")
    public Result<ConversationVo> getByConversationId(
            @Parameter(description = "会话唯一标识", required = true) @PathVariable String conversationId) {
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
    @Operation(summary = "分页查询会话列表", description = "分页查询会话列表")
    @GetMapping("/page")
    public Result<IPage<ConversationVo>> page(
            @Parameter(description = "当前页码", example = "1") @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码必须大于0") int current,
            @Parameter(description = "每页数量", example = "10") @RequestParam(defaultValue = "10") @Min(value = 1, message = "每页数量必须大于0") int size,
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword) {
        IPage<ConversationVo> page = conversationService.pageQuery(current, size, keyword);
        return Result.success(page);
    }

    /**
     * 查询所有会话
     *
     * @return 会话列表
     */
    @Operation(summary = "查询所有会话", description = "查询所有会话")
    @GetMapping("/list")
    public Result<List<ConversationVo>> list() {
        List<ConversationVo> list = conversationService.listAll();
        return Result.success(list);
    }

    /**
     * 更新会话
     *
     * @param id 会话数据库主键ID
     * @param dto 更新DTO
     * @return 更新后的会话信息
     */
    @Operation(summary = "更新会话", description = "更新指定ID的会话")
    @PutMapping("/{id}")
    public Result<ConversationVo> update(
            @Parameter(description = "会话数据库主键ID", required = true) @PathVariable @Min(value = 1, message = "ID必须大于0") Long id,
            @Validated @RequestBody ConversationUpdateDTO dto) {
        log.info("更新会话: id={}", id);
        ConversationVo response = conversationService.updateConversation(id, dto);
        log.info("会话更新成功: id={}", id);
        return Result.success(response);
    }

    /**
     * 删除会话（逻辑删除）
     *
     * @param id 会话数据库主键ID
     * @return 操作结果
     */
    @Operation(summary = "删除会话", description = "根据ID删除指定会话(逻辑删除)")
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @Parameter(description = "会话数据库主键ID", required = true) @PathVariable @Min(value = 1, message = "ID必须大于0") Long id) {
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
    @Operation(summary = "批量删除会话", description = "批量删除指定ID的会话(逻辑删除)")
    @DeleteMapping("/batch")
    public Result<Void> batchDelete(
            @Parameter(description = "会话ID列表", required = true) @RequestBody @NotEmpty(message = "删除ID列表不能为空") List<@Min(value = 1, message = "ID必须大于0") Long> ids) {
        log.info("批量删除会话: count={}", ids.size());
        conversationService.batchDeleteConversation(ids);
        log.info("批量删除成功: count={}", ids.size());
        return Result.success();
    }

}

package com.ai.chat.service.impl;

import com.ai.chat.common.entity.Conversation;
import com.ai.chat.common.enums.ResultCode;
import com.ai.chat.common.exception.BusinessException;
import com.ai.chat.common.mapper.ConversationMapper;
import com.ai.chat.dto.request.ConversationCreateRequest;
import com.ai.chat.dto.request.ConversationUpdateRequest;
import com.ai.chat.service.IConversationService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collection;

@Service
public class ConversationServiceImpl extends ServiceImpl<ConversationMapper, Conversation>
        implements IConversationService {


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Conversation createConversation(ConversationCreateRequest request) {
        boolean exist = this.lambdaQuery()
                .eq(Conversation::getConversationId, request.getConversationId())
                .exists();
        if (exist) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "conversationId 已存在");
        }

        Conversation conversation = new Conversation();
        BeanUtils.copyProperties(request, conversation);
        boolean saved = this.save(conversation);
        if (!saved) {
            throw new BusinessException(ResultCode.INTERNAL_ERROR, "创建会话失败");
        }
        return conversation;


    }

    @Override
    public Conversation getByConversationId(String conversationId) {
        return this.lambdaQuery()
                .eq(Conversation::getConversationId, conversationId)
                .one();
    }

    @Override
    public Page<Conversation> pageQuery(int current, int size, String keyword) {
        Page<Conversation> page = new Page<>(current, size);
        LambdaQueryWrapper<Conversation> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(keyword)){
            wrapper.and(w -> w.like(Conversation::getTitle, keyword)
                    .or()
                    .like(Conversation::getConversationId, keyword));
        }

        wrapper.orderByDesc(Conversation::getCreatedAt);
        return this.page(page, wrapper);
    }

    @Override
    public Conversation getDetails(String conversationIdOrId) {
        Conversation conversation = this.getByConversationId(conversationIdOrId);
        if (conversation == null && conversationIdOrId != null && conversationIdOrId.matches("\\d+")) {
            conversation = this.getById(Long.parseLong(conversationIdOrId));
        }
        if (conversation == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "会话不存在");
        }
        return conversation;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Conversation updateConversation(Long id, ConversationUpdateRequest request) {
        Conversation conversation = this.getById(id);
        if (conversation == null ) {
            throw new BusinessException(ResultCode.NOT_FOUND, "会话不存在");
        }

        if (StringUtils.hasText(request.getTitle())) {
            conversation.setTitle(request.getTitle());
        }

        boolean updated = this.updateById(conversation);
        if (request.getSessionMetadata() != null) {
            if (!updated) {
                throw new BusinessException(ResultCode.INTERNAL_ERROR, "更新会话失败");
            }
        }
        return conversation;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteConversation(Long id) {
        Conversation conversation = this.getById(id);
        if (conversation == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "会话不存在");
        }

        boolean removed = this.removeById(id);
        if (!removed) {
            throw new BusinessException(ResultCode.INTERNAL_ERROR, "删除会话失败");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void batchDeleteConversation(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "批量删除 id 列表不能为空");
        }

        boolean removed = this.removeByIds(ids);
        if (!removed) {
            throw new BusinessException(ResultCode.INTERNAL_ERROR, "批量删除会话失败");
        }
    }


}

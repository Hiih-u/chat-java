package com.ai.chat.service.impl;

import com.ai.chat.common.entity.Conversation;
import com.ai.chat.common.enums.ResultCode;
import com.ai.chat.common.exception.BusinessException;
import com.ai.chat.common.mapper.ConversationMapper;
import com.ai.chat.common.config.CacheConfig;
import com.ai.chat.converter.ConversationConverter;
import com.ai.chat.dto.request.ConversationCreateRequest;
import com.ai.chat.dto.request.ConversationUpdateRequest;
import com.ai.chat.dto.response.ConversationResponse;
import com.ai.chat.service.IConversationService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.List;

@Service
public class ConversationServiceImpl extends ServiceImpl<ConversationMapper, Conversation>
        implements IConversationService {


    @Override
    @Transactional(rollbackFor = Exception.class)
    @Caching(evict = {
            @CacheEvict(value = CacheConfig.CONVERSATION_LIST_CACHE, allEntries = true),
            @CacheEvict(value = CacheConfig.CONVERSATION_PAGE_CACHE, allEntries = true)
    })
    public ConversationResponse createConversation(ConversationCreateRequest request) {
        boolean exist = this.lambdaQuery()
                .eq(Conversation::getConversationId, request.getConversationId())
                .exists();
        if (exist) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "conversationId 已存在");
        }

        Conversation conversation = ConversationConverter.toEntity(request);
        boolean saved = this.save(conversation);
        if (!saved) {
            throw new BusinessException(ResultCode.INTERNAL_ERROR, "创建会话失败");
        }
        return ConversationConverter.toResponse(conversation);
    }

    @Override
    @Cacheable(value = CacheConfig.CONVERSATION_CACHE, key = "#conversationId", unless = "#result == null")
    public ConversationResponse getByConversationId(String conversationId) {
        Conversation conversation = this.lambdaQuery()
                .eq(Conversation::getConversationId, conversationId)
                .one();
        return ConversationConverter.toResponse(conversation);
    }

    @Override
    @Cacheable(value = CacheConfig.CONVERSATION_PAGE_CACHE, key = "#current + '-' + #size + '-' + (#keyword != null ? #keyword : 'all')")
    public Page<ConversationResponse> pageQuery(int current, int size, String keyword) {
        Page<Conversation> page = new Page<>(current, size);
        LambdaQueryWrapper<Conversation> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Conversation::getTitle, keyword)
                    .or()
                    .like(Conversation::getConversationId, keyword));
        }

        wrapper.orderByDesc(Conversation::getCreatedAt);
        Page<Conversation> entityPage = this.page(page, wrapper);

        // 转换为 DTO Page
        Page<ConversationResponse> responsePage = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        responsePage.setRecords(ConversationConverter.toResponseList(entityPage.getRecords()));
        return responsePage;
    }

    @Override
    @Cacheable(value = CacheConfig.CONVERSATION_CACHE, key = "#conversationIdOrId", unless = "#result == null")
    public ConversationResponse getDetails(String conversationIdOrId) {
        Conversation conversation = this.lambdaQuery()
                .eq(Conversation::getConversationId, conversationIdOrId)
                .one();
        if (conversation == null && conversationIdOrId != null && conversationIdOrId.matches("\\d+")) {
            conversation = this.getById(Long.parseLong(conversationIdOrId));
        }
        if (conversation == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "会话不存在");
        }
        return ConversationConverter.toResponse(conversation);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Caching(evict = {
            @CacheEvict(value = CacheConfig.CONVERSATION_CACHE, key = "#id"),
            @CacheEvict(value = CacheConfig.CONVERSATION_LIST_CACHE, allEntries = true),
            @CacheEvict(value = CacheConfig.CONVERSATION_PAGE_CACHE, allEntries = true)
    })
    public ConversationResponse updateConversation(Long id, ConversationUpdateRequest request) {
        Conversation conversation = this.getById(id);
        if (conversation == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "会话不存在");
        }

        if (StringUtils.hasText(request.getTitle())) {
            conversation.setTitle(request.getTitle());
        }
        if (request.getSessionMetadata() != null) {
            conversation.setSessionMetadata(request.getSessionMetadata());
        }

        boolean updated = this.updateById(conversation);
        if (!updated) {
            throw new BusinessException(ResultCode.INTERNAL_ERROR, "更新会话失败");
        }
        return ConversationConverter.toResponse(conversation);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Caching(evict = {
            @CacheEvict(value = CacheConfig.CONVERSATION_CACHE, allEntries = true),
            @CacheEvict(value = CacheConfig.CONVERSATION_LIST_CACHE, allEntries = true),
            @CacheEvict(value = CacheConfig.CONVERSATION_PAGE_CACHE, allEntries = true)
    })
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
    @Caching(evict = {
            @CacheEvict(value = CacheConfig.CONVERSATION_CACHE, allEntries = true),
            @CacheEvict(value = CacheConfig.CONVERSATION_LIST_CACHE, allEntries = true),
            @CacheEvict(value = CacheConfig.CONVERSATION_PAGE_CACHE, allEntries = true)
    })
    public void batchDeleteConversation(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "批量删除 id 列表不能为空");
        }

        boolean removed = this.removeByIds(ids);
        if (!removed) {
            throw new BusinessException(ResultCode.INTERNAL_ERROR, "批量删除会话失败");
        }
    }

    @Override
    @Cacheable(value = CacheConfig.CONVERSATION_LIST_CACHE, key = "'all'")
    public List<ConversationResponse> listAll() {
        List<Conversation> list = this.list();
        return ConversationConverter.toResponseList(list);
    }

}

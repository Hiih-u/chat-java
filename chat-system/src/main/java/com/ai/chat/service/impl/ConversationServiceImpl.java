package com.ai.chat.service.impl;

import com.ai.chat.pojo.entity.Conversation;
import com.ai.chat.common.enums.ResultCode;
import com.ai.chat.common.exception.BusinessException;
import com.ai.chat.mapper.ConversationMapper;
import com.ai.chat.common.config.CacheConfig;
import com.ai.chat.wrapper.ConversationWrapper;
import com.ai.chat.pojo.dto.ConversationDTO;
import com.ai.chat.pojo.dto.ConversationUpdateDTO;
import com.ai.chat.pojo.vo.ConversationVo;
import com.ai.chat.service.IConversationService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
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
    public ConversationVo createConversation(ConversationDTO dto) {
        boolean exist = this.lambdaQuery()
                .eq(Conversation::getConversationId, dto.getConversationId())
                .exists();
        if (exist) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "conversationId 已存在");
        }

        Conversation conversation = ConversationWrapper.toEntity(dto);
        boolean saved = this.save(conversation);
        if (!saved) {
            throw new BusinessException(ResultCode.INTERNAL_ERROR, "创建会话失败");
        }
        return ConversationWrapper.toResponse(conversation);
    }

    @Override
    @Cacheable(value = CacheConfig.CONVERSATION_CACHE, key = "#conversationId", unless = "#result == null")
    public ConversationVo getByConversationId(String conversationId) {
        Conversation conversation = this.lambdaQuery()
                .eq(Conversation::getConversationId, conversationId)
                .one();
        return ConversationWrapper.toResponse(conversation);
    }

    @Override
    @Cacheable(value = CacheConfig.CONVERSATION_PAGE_CACHE, 
            key = "#current + '-' + #size + '-' + (#keyword != null ? #keyword : 'all')")
    public IPage<ConversationVo> pageQuery(int current, int size, String keyword) {
        // 1. 创建分页对象
        IPage<Conversation> page = new Page<>(current, size);
        
        // 2. 构建查询条件
        LambdaQueryWrapper<Conversation> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Conversation::getTitle, keyword)
                    .or()
                    .like(Conversation::getConversationId, keyword));
        }
        wrapper.orderByDesc(Conversation::getCreatedAt);
        
        // 3. 执行分页查询
        IPage<Conversation> entityPage = this.page(page, wrapper);
        
        // 4. 使用 convert() 方法转换（一行代码搞定！）
        return entityPage.convert(ConversationWrapper::toResponse);
    }

    @Override
    @Cacheable(value = CacheConfig.CONVERSATION_CACHE, key = "#conversationId", unless = "#result == null")
    public ConversationVo getDetails(String conversationId) {
        Conversation conversation = this.lambdaQuery()
                .eq(Conversation::getConversationId, conversationId)
                .one();
        if (conversation == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "会话不存在");
        }
        return ConversationWrapper.toResponse(conversation);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Caching(evict = {
            // 精准删除：方法执行成功后，根据返回值的 conversationId 清除缓存
            @CacheEvict(value = CacheConfig.CONVERSATION_CACHE,
                        key = "#result.conversationId",
                        beforeInvocation = false),
            // 清除列表和分页缓存
            @CacheEvict(value = CacheConfig.CONVERSATION_LIST_CACHE, allEntries = true),
            @CacheEvict(value = CacheConfig.CONVERSATION_PAGE_CACHE, allEntries = true)
    })
    public ConversationVo updateConversation(Long id, ConversationUpdateDTO dto) {
        Conversation conversation = this.getById(id);
        if (conversation == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "会话不存在");
        }

        if (StringUtils.hasText(dto.getTitle())) {
            conversation.setTitle(dto.getTitle());
        }
        if (dto.getSessionMetadata() != null) {
            conversation.setSessionMetadata(dto.getSessionMetadata());
        }

        boolean updated = this.updateById(conversation);
        if (!updated) {
            throw new BusinessException(ResultCode.INTERNAL_ERROR, "更新会话失败");
        }
        return ConversationWrapper.toResponse(conversation);
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
    public List<ConversationVo> listAll() {
        List<Conversation> list = this.list();
        return ConversationWrapper.toResponseList(list);
    }

}

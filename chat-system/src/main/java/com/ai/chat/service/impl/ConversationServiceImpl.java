package com.ai.chat.service.impl;

import com.ai.chat.common.entity.Conversation;
import com.ai.chat.common.mapper.ConversationMapper;
import com.ai.chat.service.IConversationService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ConversationServiceImpl extends ServiceImpl<ConversationMapper, Conversation>
        implements IConversationService {


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
            wrapper.like(Conversation::getTitle, keyword)
                    .or()
                    .like(Conversation::getConversationId, keyword);
        }

        wrapper.orderByDesc(Conversation::getCreatedAt);
        return this.page(page, wrapper);
    }
}

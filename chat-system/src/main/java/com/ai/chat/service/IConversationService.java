package com.ai.chat.service;

import com.ai.chat.common.entity.Conversation;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;


public interface IConversationService extends IService<Conversation> {


    Conversation getByConversationId(String conversationId);


    Page<Conversation> pageQuery(int current, int size, String keyword);
}

package com.ai.chat.service;

import com.ai.chat.system.pojo.entity.Conversation;
import com.ai.chat.system.pojo.dto.ConversationDTO;
import com.ai.chat.system.pojo.dto.ConversationUpdateDTO;
import com.ai.chat.system.pojo.vo.ConversationVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Collection;
import java.util.List;


public interface IConversationService extends IService<Conversation> {

    ConversationVo createConversation(ConversationDTO dto);

    ConversationVo getByConversationId(String conversationId);

    IPage<ConversationVo> pageQuery(int current, int size, String keyword);

    ConversationVo getDetails(String conversationId);

    ConversationVo updateConversation(Long id, ConversationUpdateDTO dto);

    void deleteConversation(Long id);

    void batchDeleteConversation(Collection<Long> ids);

    List<ConversationVo> listAll();

}

package com.ai.chat.common.mapper;

import com.ai.chat.common.pojo.entity.Task;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TaskMapper extends BaseMapper<Task> {

    // 根据 batchId 查询任务列表
    @Select("SELECT * FROM ai_tasks WHERE batch_id = #{batchId} ORDER BY created_at ASC")
    List<Task> selectByBatchId(@Param("batchId") String batchId);


    // 根据 conversationId 查询历史任务
    @Select("SELECT * FROM ai_tasks WHERE conversation_id = #{conversationId} ORDER BY created_at ASC")
    List<Task> selectByConversationId(@Param("conversationId") String conversationId);

    // 根据 taskId 查询单个任务
    @Select("SELECT * FROM ai_tasks WHERE task_id = #{taskId} LIMIT 1")
    Task selectByTaskId(@Param("taskId") String taskId);
}

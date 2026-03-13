package com.ai.chat.common.mapper;

import com.ai.chat.common.entity.GeminiServiceNode;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface GeminiServiceNodeMapper extends BaseMapper<GeminiServiceNode> {

    // 查询所有活跃的服务节点 ( 状态为 active )
    @Select("SELECT * FROM gemini_service_nodes WHERE status = 'active' ORDER BY weight DESC, current_tasks ASC")
    List<GeminiServiceNode> selectActiveNodes();

    // 根据 nodeUrl 查询节点信息
    @Select("SELECT * FROM gemini_service_nodes WHERE node_url = #{nodeUrl} LIMIT 1")
    GeminiServiceNode selectByNodeUrl(@Param("nodeUrl") String nodeUrl);

    // 增加节点的当前任务数
    @Update("UPDATE gemini_service_nodes SET current_tasks = current_tasks + 1, dispatched_tasks = dispatched_tasks + 1 WHERE node_url = #{nodeUrl}")
    int incrementCurrentTasks(@Param("nodeUrl") String nodeUrl);

    // 减少节点的当前任务数
    @Update("UPDATE gemini_service_nodes SET current_tasks = GREATEST(current_tasks - 1, 0) WHERE node_url = #{nodeUrl}")
    int decrementTaskCount(@Param("nodeUrl") String nodeUrl);

    // 更新节点心跳时间
    @Update("UPDATE gemini_service_nodes SET last_heartbeat = NOW() WHERE node_url = #{nodeUrl}")
    int updateHeartbeat(@Param("nodeUrl") String nodeUrl);

}

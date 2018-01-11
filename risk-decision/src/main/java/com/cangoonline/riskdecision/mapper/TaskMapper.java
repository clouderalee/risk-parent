package com.cangoonline.riskdecision.mapper;

import com.cangoonline.riskdecision.entity.RelativeData;
import com.cangoonline.riskdecision.entity.TaskObject;
import com.cangoonline.riskdecision.mapper.dto.ResultNotifyDataDto;
import org.apache.ibatis.annotations.Param;

/**
 * Created by Administrator on 2017\12\11 0011.
 */
public interface TaskMapper {

    void saveTaskObject(TaskObject taskObject);

    TaskObject getTaskObjectById(String taskId);

    int updateTaskObject(TaskObject taskObject);

    int updateTaskStatusById(@Param("taskId")String taskId,@Param("taskStatus")int taskStatus);



    void saveRelativeData(RelativeData relativeData);

    RelativeData getRelativeData(@Param("taskId") String taskId,@Param("type") int type);

    RelativeData getRiskDecisionTaskData(String taskId);

    RelativeData getRiskDecisionResultData(String taskId);

    ResultNotifyDataDto getResultNotifyDataDtoByTranscationNo(String transcationNo);

    ResultNotifyDataDto getResultNotifyDataDtoByTaskId(String taskId);

    String getNotifyAddress(@Param("channel")String channel,@Param("serviceCode") String serviceCode);
}

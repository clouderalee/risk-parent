package com.cangoonline.riskdecision.common;

/**
 * Created by Administrator on 2017\12\11 0011.
 */
public class TaskConstants {

    /**
     * 任务类型，对应与消息类型
     */
    public static int TASK_TYPE_TASK = 1;
    public static String TASK_TYPE_TASK_S = "1";

    /**
     * 风险决策申请数据类型
     */
    public static int DATA_TYPE_RISK_DECISION_APPLY = 1;
    /**
     * 风险决策申请的结果数据类型
     */
    public static int DATA_TYPE_RISK_DECISION_APPLY_RESULT = 2;


    public static final int INITIAL = 0;//初始化状态
    public static final int WAIT_FOR_PUBLISH = 1;//等待发布
    public static final int PUBLISH_SUCCESS = 2;//发布成功
    public static final int PUBLISH_FAILURE = 3;//发布失败
    public static final int HANDLING = 4;//处理中
    public static final int HANDLE_COMPLETE = 5;//处理完成
    public static final int HUMAN_EFFECT = 6;//人工干预
    public static final int MISS_MESSAGE = 7;//丢弃

    public static final int NOTIFY_SUCCESS = 20;//结果通知成功
    public static final int NOTIFY_FAILURE = 30;//结果通知失败


}

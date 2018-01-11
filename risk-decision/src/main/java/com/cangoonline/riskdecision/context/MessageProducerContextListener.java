package com.cangoonline.riskdecision.context;

import com.cangoonline.disruptor.producer.MessageProducer;
import com.cangoonline.disruptor.service.WorkerService;
import com.cangoonline.riskdecision.service.local.MessageProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Created by Administrator on 2017\12\11 0011.
 */
public class MessageProducerContextListener implements ServletContextListener {

    private WorkerService workerService;
    private MessageProducerService messageProducerService;

    public void contextInitialized(ServletContextEvent event) {

        WebApplicationContext ac = WebApplicationContextUtils.getWebApplicationContext(event.getServletContext());

        workerService = ac.getBean(WorkerService.class);
        messageProducerService = ac.getBean(MessageProducerService.class);

        workerService.startService();

        //给MessageProducerService设置属性
        MessageProducer messageProducer = new MessageProducer(workerService.getRingBuffer());
        messageProducerService.setProducer(messageProducer);
        System.out.println("========> MessageProducerService 初始化完成...");

    }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        workerService.stopService();
    }
}

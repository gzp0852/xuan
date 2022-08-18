package com.glx.xuan.consumer.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    /**
     * 审批结果消息通知 - fanout交换机
     */
    public static final String PROCESS_NOTICE_FANOUT_EXCHANGE = "process_notice_exchange";

    /**
     * 审批结果消息通知 - direct交换机
     */
    public static final String PROCESS_NOTICE_DIRECT_EXCHANGE = "process_notice_direct_exchange";

    /**
     * 审批结果消息通知 - 队列
     */
    public static final String PROCESS_NOTICE_QUEUE = "process_notice_queue";

    /**
     * 审批结果消息通知 - 路由
     */
    public static final String PROCESS_NOTICE_ROUTING = "processNoticeRouting";

    @Bean
    public FanoutExchange processNoticeFanoutExchange() {
        return new FanoutExchange(PROCESS_NOTICE_FANOUT_EXCHANGE, true, false);
    }

    @Bean
    public DirectExchange processNoticeDirectExchange() {
        return new DirectExchange(PROCESS_NOTICE_DIRECT_EXCHANGE, true, false);
    }

    @Bean
    public Queue processNoticeQueue() {
        return new Queue(PROCESS_NOTICE_QUEUE, true, false, false);
    }

    @Bean
    public Binding bingPosterQueue2Exchange() {
        return BindingBuilder.bind(processNoticeQueue()).to(processNoticeDirectExchange()).with(PROCESS_NOTICE_ROUTING);
    }
}
package com.glx.xuan.producer.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String TEST_FANOUT_EXCHANGE = "test_fanout_exchange1";

    public static final String TEST_DIRECT_EXCHANGE = "test_direct_exchange1";

    public static final String TEST_QUEUE = "test_queue1";

    public static final String TEST_DERECT_ROUTING = "testDerectRouting1";

    @Bean
    public FanoutExchange testFanoutExchange() {
        return new FanoutExchange(TEST_FANOUT_EXCHANGE, true, false);
    }

    @Bean
    public DirectExchange testDirectExchange() {
        return new DirectExchange(TEST_DIRECT_EXCHANGE, true, false);
    }

    @Bean
    public Queue testQueue() {
        return new Queue(TEST_QUEUE, true, false, false);
    }

    @Bean
    public Binding bingTestQueue2Exchange() {
        return BindingBuilder.bind(testQueue()).to(testDirectExchange()).with(TEST_DERECT_ROUTING);
    }
}
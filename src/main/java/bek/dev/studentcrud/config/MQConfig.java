package bek.dev.studentcrud.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MQConfig {

    public  static final String USER1="user1";
    public  static final String USER2="user2";
    public  static final String VISIT_COME="VISIT_COME";
    public  static final String VISIT_BACK="VISIT_BACK";
    public  static final String DIRECT_EXCHANGE="DIRECT_EXCHANGE";
    public  static final String TOPIC_EXCHANGE="TOPIC_EXCHANGE";
    public  static final String FANOUT_EXCHANGE="FANOUT_EXCHANGE";
    public  static final String USER1_ROUTER_KEY="USER1_ROUTER_KEY";
    public  static final String USER2_ROUTER_KEY="USER2_ROUTER_KEY";
    public  static final String VISIT_COME_KEY="VISIT_COME_KEY";
    public  static final String VISIT_BACK_KEY="VISIT_BACK_KEY";

    @Bean
    public Exchange exchange(){
        return ExchangeBuilder.fanoutExchange(FANOUT_EXCHANGE).build();
    }


    @Bean
    public Exchange directExchange(){
        return ExchangeBuilder.directExchange(DIRECT_EXCHANGE).build();
    }


    //send message to user1 nad user2
    @Bean
    public Queue fooQueue(){
        return QueueBuilder.durable(USER1).build();
    }

    @Bean
    public Queue barQueue(){
        return QueueBuilder.durable(USER2).build();
    }



    @Bean
    public Queue visitBack(){
        return QueueBuilder.durable(VISIT_BACK).build();
    }


    @Bean
    public Queue visitCome(){
        return QueueBuilder.durable(VISIT_COME).build();
    }




    //binding exchange and queue
    @Bean
    public Binding bindingExchangeUSER1(Queue fooQueue, FanoutExchange exchange){
        return BindingBuilder.bind(fooQueue).to(exchange);//.with(USER1_ROUTER_KEY);

    }
    @Bean
    public Binding bindingExchangeUSER2(Queue barQueue, FanoutExchange fanoutExchange){
        return BindingBuilder.bind(barQueue).to(fanoutExchange);//.with(USER2_ROUTER_KEY);
    }


    //studentlar keldi kettisi uchun exchange va queue larni bind qilish
    @Bean
    public Binding bindingExchangeComeVisit(Queue visitCome, DirectExchange directExchange){
        return BindingBuilder.bind(visitCome).to(directExchange).with(VISIT_COME_KEY);
    }

     @Bean
    public Binding bindingExchangeBackVisit(Queue visitBack, DirectExchange directExchange){
        return BindingBuilder.bind(visitBack).to(directExchange).with(VISIT_BACK_KEY);
    }



    //messageConventorni bean qilib oldik
    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }
}

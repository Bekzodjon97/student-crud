package bek.dev.studentcrud;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProducerService {

    private final RabbitTemplate rabbitTemplate;

    public void sendMessage(String exchange, String routerKey, String message){
        rabbitTemplate.convertAndSend(exchange, routerKey, message);
    }

}

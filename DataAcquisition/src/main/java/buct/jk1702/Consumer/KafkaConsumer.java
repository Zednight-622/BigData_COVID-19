package buct.jk1702.Consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author Zednight
 * @Date 2020/10/1 16:07
 * 
 */
//@Component
//@Slf4j
public class KafkaConsumer {
    //@KafkaListener(topics = "COVID_19")
    public void consumer(ConsumerRecord<String, String> consumerRecord) {
        //判断是否为null
        Optional<?> kafkaMessage = Optional.ofNullable(consumerRecord.value());
        if (kafkaMessage.isPresent()) {
            //得到Optional实例中的值
            Object message = kafkaMessage.get();
            System.err.println("消费消息:" + message);
        }
    }
}
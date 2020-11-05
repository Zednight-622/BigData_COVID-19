package buct.jk1702.Producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
* @author Zednight
* @date 2020/10/1 16:06
*/
@Component
public class KafkaProducer {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendMsg(String topic,String s) {
        kafkaTemplate.send(topic, s);
    }

}

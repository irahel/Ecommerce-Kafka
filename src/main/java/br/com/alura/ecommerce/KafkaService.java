package br.com.alura.ecommerce;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

class KafkaService {
    private final KafkaConsumer<String, String> consumer;
    private final ConsumerFunction parse;

    KafkaService(String groupId, String topic, ConsumerFunction parse ) {
         this.parse = parse;
        this.consumer = new KafkaConsumer<String, String>(properties(groupId));   
        consumer.subscribe(Collections.singletonList(topic));        
    }

    static void main(String[] args) {
        
    }

    void run() {
        while(true) {            
            var records = consumer.poll(Duration.ofMillis(100));
            
            if(!records.isEmpty()){
                System.out.println("I did find "+ records.count() +" records");                            
                for(var record : records){
                    parse.consume(record); 
                }           
            }
        } 
    }
 
    private static Properties properties(String groupId) {
        var properties = new Properties();

        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());        
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "1");
        return properties;
    }
}

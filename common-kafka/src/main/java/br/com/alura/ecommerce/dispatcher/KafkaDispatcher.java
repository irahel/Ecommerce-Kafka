package br.com.alura.ecommerce.dispatcher;

import java.io.Closeable;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import br.com.alura.ecommerce.CorrelationId;
import br.com.alura.ecommerce.Message;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

public class KafkaDispatcher<T> implements Closeable {

    private final KafkaProducer<String, Message<T>> producer;
    public KafkaDispatcher() {
        this.producer = new KafkaProducer<>(properties());
    }

    private static Properties properties() {
        var properties = new Properties();

        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, GsonSerializer.class.getName());
        properties.setProperty(ProducerConfig.ACKS_CONFIG, "all");
        return properties;
    }


    public void send(String topic, String key, T payload, CorrelationId id) throws ExecutionException, InterruptedException {
        Future<RecordMetadata> future = sendAsync(topic, key, payload, id);
        future.get();
    }

    Future<RecordMetadata> sendAsync(String topic, String key, T payload, CorrelationId id) {
        var value = new Message<>(id.continueWith("_" +topic), payload);
        var record = new ProducerRecord<>(topic, key, value);
        Callback callback = (data, ex) -> {
            if (ex != null) {
                ex.printStackTrace();
                return;
            }
            System.out.println("success:::" + data.topic() + ":::" + data.partition() + "/" + data.offset() + "/" + data.timestamp());
        };

        return this.producer.send(record, callback);
    }

    @Override
    public void close() {
        this.producer.close();

    }
}

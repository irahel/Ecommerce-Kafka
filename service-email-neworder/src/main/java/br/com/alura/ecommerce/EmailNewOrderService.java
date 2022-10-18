package br.com.alura.ecommerce;

import br.com.alura.ecommerce.consumer.ConsumerService;
import br.com.alura.ecommerce.consumer.ServiceRunner;
import br.com.alura.ecommerce.dispatcher.KafkaDispatcher;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.concurrent.ExecutionException;

public class EmailNewOrderService implements ConsumerService<Order> {
    private static final int THREADS = 1;
    private final KafkaDispatcher<Email> emailDispatcher = new KafkaDispatcher<>();

    public static void main(String[] args) {
        new ServiceRunner<>(EmailNewOrderService::new).start(THREADS);
    }

    @Override
    public String getConsumerGroup() {
        return EmailNewOrderService.class.getSimpleName();
    }

    @Override
    public String getTopic() {
        return "ECOMMERCE_NEWORDER";
    }

    public void parse(ConsumerRecord<String, Message<Order>> record) throws ExecutionException, InterruptedException {
        System.out.println("\n----------------------");
        System.out.println("Processing new order, preparing email");
        System.out.println(record.value());

        var order = record.value().getPayload();
        var id = record.value().getId();
        var emailCode = new Email("New order processing", "Thank you for your order! We are processing your order!");

        emailDispatcher.send("ECOMMERCE_SENDEMAIL", order.getEmail(), emailCode, id.continueWith(EmailNewOrderService.class.getSimpleName()));
        System.out.println("\n----------------------");
    }
}

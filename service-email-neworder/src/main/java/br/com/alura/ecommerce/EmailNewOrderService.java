package br.com.alura.ecommerce;

import br.com.alura.ecommerce.consumer.KafkaService;
import br.com.alura.ecommerce.dispatcher.KafkaDispatcher;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class EmailNewOrderService {
    private final KafkaDispatcher<Email> emailDispatcher = new KafkaDispatcher<>();

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        var emailService = new EmailNewOrderService();
        var service = new KafkaService<>(EmailNewOrderService.class.getSimpleName(),
                "ECOMMERCE_NEWORDER",
                emailService::parse,
                Map.of());
        service.run();
    }

    private void parse(ConsumerRecord<String, Message<Order>> record) throws ExecutionException, InterruptedException {
        System.out.println("\n----------------------");
        System.out.println("Processing new order, preparing email");
        System.out.println(record.value());

        var order = record.value().getPayload();
        var id = record.value().getId();
        var emailCode = new Email("New order processing", "Thank you for your order! We are processing your order!");

        emailDispatcher.send("ECOMMERCE_SENDEMAIL",
                order.getEmail(), emailCode, id.continueWith(EmailNewOrderService.class.getSimpleName()));
        System.out.println("\n----------------------");

    }
}

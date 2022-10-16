package br.com.alura.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class FraudDetectorService {
    private final KafkaDispatcher<Order> orderKafkaDispatcher = new KafkaDispatcher<>();

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        var fraudDetectorService = new FraudDetectorService();
        var service = new KafkaService<>(FraudDetectorService.class.getSimpleName(),
                "ECOMMERCE_NEWORDER",
                fraudDetectorService::parse,
                Map.of());
        service.run();
    }

    private void parse(ConsumerRecord<String, Message<Order>> record) throws ExecutionException, InterruptedException {
        System.out.println("\n----------------------");
        System.out.println("Processing new order, checking for fraud");
        System.out.println(record.key());
        System.out.println(record.value());
        System.out.println(record.partition());
        System.out.println(record.offset());

        var message = record.value();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        var order = message.getPayload();
        if (isFraud(order)) {
            System.out.println("Order is a fraud!!!");
            orderKafkaDispatcher.send("ECOMMERCE_ORDERREJECTED", order.getEmail(), order,
                    message.getId().continueWith(FraudDetectorService.class.getSimpleName()));
        } else {
            System.out.println("Order approved successfully: " + order);
            orderKafkaDispatcher.send("ECOMMERCE_ORDERAPPROVED", order.getEmail(), order,
                    message.getId().continueWith(FraudDetectorService.class.getSimpleName()));
        }
        System.out.println("\n----------------------");

    }

    private boolean isFraud(Order order) {
        return order.getValue().compareTo(new BigDecimal("4500")) >= 0;
        //Fraud happens whe the value is >= 4500
    }

}

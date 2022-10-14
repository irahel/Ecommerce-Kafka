package br.com.alura.ecommerce;

import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public class EmailService {
    public static void main(String[] args) {
        var emailService = new EmailService();
        var service = new KafkaService<>(EmailService.class.getSimpleName(),
                "ECOMMERCE_SENDEMAIL",
                emailService::parse,
                Map.of());
        service.run();
    }

    private void parse(ConsumerRecord<String, Message<Email>> record) {

        System.out.println("----------------------");
        System.out.println("Sending Email");
        System.out.println(record.key());
        System.out.println(record.value());
        System.out.println(record.partition());
        System.out.println(record.offset());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Email sent successfully");
    }

}

package br.com.alura.ecommerce;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrderMain {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        try (var orderDispatcher = new KafkaDispatcher<Order>()) {
            try (var emailDispatcher = new KafkaDispatcher<Email>()) {
                var email = Math.random() + "@email.com";
                for (var i = 0; i < 10; i++) {

                    var orderID = UUID.randomUUID().toString();
                    var value = BigDecimal.valueOf(Math.random() * 5000 + 1);

                    var order = new Order(orderID, value, email);
                    orderDispatcher.send("ECOMMERCE_NEWORDER", email, order);

                    var emailCode = new Email("New order processing", "Thank you for your order! We are processing your order!");
                    emailDispatcher.send("ECOMMERCE_SENDEMAIL", email, emailCode);
                }
            }
        }        
    }
}

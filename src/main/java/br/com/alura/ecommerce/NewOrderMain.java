package br.com.alura.ecommerce;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrderMain {

    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException {
        try (var orderDispatcher = new KafkaDispatcher<Order>()) {
            try (var emailDispatcher = new KafkaDispatcher<String>()) {
                for(var i = 0; i < 10; i++){
                    var userID = UUID.randomUUID().toString();
                    var orderID = UUID.randomUUID().toString();
                    var value = new BigDecimal(Math.random() * 5000 +1);                
                    var order = new Order(userID, orderID, value);
                    
                    
                    orderDispatcher.send("ECOMMERCE_NEWORDER", userID, order);

                    var email = "Thank you for your order! We are processing your order!";            
                    emailDispatcher.send("ECOMMERCE_SENDEMAIL", userID, email);
                }
            }
        }        
    }
}

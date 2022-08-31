package br.com.alura.ecommerce;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrderMain {

    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException {
        try (var dispatcher = new KafkaDispatcher()) {
            for(var i = 0; i < 10; i++){
                var key = UUID.randomUUID().toString();
                var value = key + ", 37.00, 1212";
                
                dispatcher.send("ECOMMERCE_NEWORDER", key, value);

                var email = "Thank you for your order! We are processing your order!";            
                dispatcher.send("ECOMMERCE_SENDEMAIL", key, email);
            }
        }        
    }
}

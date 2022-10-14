package br.com.alura.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class BatchSendMessageService {
    private final Connection connection;

    BatchSendMessageService() throws SQLException {
        String url = "jdbc:sqlite:target/users_database.db";
        connection = DriverManager.getConnection(url);
        try {
            connection.createStatement().execute("create table Users(" +
                    "uuid varchar(200) primary key," +
                    "email varchar( 200))");
        }catch (SQLException ex){
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) throws SQLException {

        var batchService = new BatchSendMessageService();
        var service = new KafkaService<>(BatchSendMessageService.class.getSimpleName(),
                "ECOMMERCE_SEND_MESSAGE_TO_ALL_USERS",
                batchService::parse,
                Map.of());
        service.run( );
    }

    private final KafkaDispatcher<User> userDispatcher = new KafkaDispatcher<>();
    private void parse(ConsumerRecord<String, Message<String>> record) throws SQLException, ExecutionException, InterruptedException {
        System.out.println("\n----------------------");
        System.out.println("Processing new batch");
        var message = record.value();
        System.out.println("Topic: "+message.getPayload());

        for (User user : getAllUsers()){
            userDispatcher.send(message.getPayload(), user.getUuid(), user,
                    message.getId().continueWith(BatchSendMessageService.class.getSimpleName()));
        }

    }

    private List<User> getAllUsers() throws SQLException {
        var result = connection.prepareStatement("select uuid from Users").executeQuery();
        List<User> users = new ArrayList<>();
        while (result.next()){
            users.add(new User(result.getString(1)));

        }
        return users;
    }
}

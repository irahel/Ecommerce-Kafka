package br.com.alura.ecommerce;

import java.math.BigDecimal;

public class Order {
    private final String userID, orderID;
    private final BigDecimal value;

    public Order(String userID, String orderID, BigDecimal value) {
        this.userID = userID;
        this.orderID = orderID;
        this.value = value;
    }

    public BigDecimal getValue() {
        return value;
    }

    public String getUserID() {
        return userID;
    }

    @Override
    public String toString() {
        return "Order{" +
                "userID='" + userID + '\'' +
                ", orderID='" + orderID + '\'' +
                ", value=" + value +
                '}';
    }
}

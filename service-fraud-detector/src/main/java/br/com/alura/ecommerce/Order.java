package br.com.alura.ecommerce;

import java.math.BigDecimal;

public class Order {
    private final String orderID;
    private final BigDecimal value;
    private final String email;

    public Order(String orderID, BigDecimal value, String email) {
        this.orderID = orderID;
        this.value = value;
        this.email = email;
    }

    public BigDecimal getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Order{" + "orderID='" + orderID + '\'' + ", value=" + value + ", email='" + email + '\'' + '}';
    }

    public String getEmail() {
        return email;
    }
}

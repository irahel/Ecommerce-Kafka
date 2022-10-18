package br.com.alura.ecommerce;

import br.com.alura.ecommerce.dispatcher.KafkaDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.math.BigDecimal;

public class NewOrderServlet extends HttpServlet {

    private final KafkaDispatcher<Order> orderDispatcher = new KafkaDispatcher<>();

    @Override
    public void destroy() {
        super.destroy();
        orderDispatcher.close();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
        try {
            var email = req.getParameter("email");
            var orderID = req.getParameter("uuid");
            var value = new BigDecimal(req.getParameter("value"));
            var order = new Order(orderID, value, email);
            try (var database = new OrdersDatabase()) {
                if (database.saveNew(order)) {
                    orderDispatcher.send("ECOMMERCE_NEWORDER", email, order, new CorrelationId(NewOrderServlet.class.getSimpleName()));
                    System.out.println("New order sent successfully! --<" + orderID + ">");
                    resp.setStatus(200);
                    resp.getWriter().println("New order sent successfully! --<" + orderID + ">--<" + email + ">--<" + value + ">");
                } else {
                    System.out.println("Old order received! --<" + orderID + ">");
                    resp.setStatus(200);
                    resp.getWriter().println("Old order received! --<" + orderID + ">--<" + email + ">--<" + value + ">");
                }
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}

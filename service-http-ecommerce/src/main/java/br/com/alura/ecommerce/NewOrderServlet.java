package br.com.alura.ecommerce;

import br.com.alura.ecommerce.dispatcher.KafkaDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.math.BigDecimal;
import java.util.UUID;

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

            var orderID = UUID.randomUUID().toString();
            var value = new BigDecimal(req.getParameter("value"));

            var order = new Order(orderID, value, email);
            orderDispatcher.send("ECOMMERCE_NEWORDER", email, order, new CorrelationId(NewOrderServlet.class.getSimpleName()));

            System.out.println("New order sent successfuly! --<" + orderID + ">");

            resp.setStatus(200);
            resp.getWriter().println("New order sent successfuly! --<" + orderID + ">--<" + email + ">--<" + value + ">");
        } catch (Exception e) {
            throw new ServletException(e);
        }


    }
}

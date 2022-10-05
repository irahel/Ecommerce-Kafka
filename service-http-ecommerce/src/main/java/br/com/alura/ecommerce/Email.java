package br.com.alura.ecommerce;

public class Email {
    private final String subject, body;

    Email(String subject, String body) {
        this.subject = subject;
        this.body = body;
    }

    @Override
    public String toString() {
        return "[" + subject + "]: " + body;
    }
}

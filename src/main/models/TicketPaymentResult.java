package models;

public final class TicketPaymentResult {
    public PaymentResponseCode paymentResponseCode;
    public TicketPaymentResult(PaymentResponseCode paymentResponseCode) {
        this.paymentResponseCode = paymentResponseCode;
    }
}

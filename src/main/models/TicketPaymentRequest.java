package models;

import java.math.BigDecimal;

public final class TicketPaymentRequest {
    public final BigDecimal amount;
    public final String ccNo;
    public TicketPaymentRequest(BigDecimal amount, String ccNo) {
        this.amount = amount;
        this.ccNo = ccNo;
    }
}

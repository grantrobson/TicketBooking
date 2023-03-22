package connectors;

import models.TicketPaymentRequest;
import models.TicketPaymentResult;

public interface TicketPaymentConnector {
    TicketPaymentResult makePayment(TicketPaymentRequest ticketPaymentRequest);
}

package connectors;

import models.TicketPaymentRequest;
import models.TicketPaymentResult;

public interface TicketPaymentConnector {
    // Presumably this would be calling the external service via a HTTP GET or POST: Could use a Future here
    TicketPaymentResult makePayment(TicketPaymentRequest ticketPaymentRequest);
}

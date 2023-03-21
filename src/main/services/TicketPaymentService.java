package services;

import models.TicketPaymentRequest;
import models.TicketPaymentResult;

public interface TicketPaymentService {
    TicketPaymentResult makePayment(TicketPaymentRequest ticketPaymentRequest);
}

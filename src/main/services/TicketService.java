package services;

import models.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static models.ReservationResponseCode.*;
import static models.TicketType.*;


public class TicketService {
    private TicketPaymentService ticketPaymentService;
    public TicketService(TicketPaymentService ticketPaymentService) {
        this.ticketPaymentService = ticketPaymentService;
    }

    private static Map<TicketType, BigDecimal> ticketPrices = Map.of(
            Adult, new BigDecimal(20),
            Child, new BigDecimal(10)
    );

    public TicketReservationResult reserve(List<TicketTypeRequest> ttrList, String ccNo) {
        if (ttrList.stream().anyMatch(m -> m.ticketType == Infant || m.ticketType == Child) &&
                ttrList.stream().noneMatch(m -> m.ticketType == Adult)) {
            return new TicketReservationResult(new BigDecimal(0), ChildAndInfantTicketsMustBeWithAdult);
        } else {

            int totalTicketsRequested = ttrList.stream().filter(tr -> tr.ticketType != Infant).mapToInt(tr -> tr.howMany).sum();
            if (totalTicketsRequested > 20) {
                return new TicketReservationResult(new BigDecimal(0), FailureTooManyTicketsInOneGo);
            } else {
                BigDecimal totalCost = ttrList.stream()
                        .map(a -> ticketPrices.getOrDefault(a.ticketType, BigDecimal.ZERO)
                                .multiply(new BigDecimal(a.howMany)))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                TicketPaymentRequest tpr = new TicketPaymentRequest(totalCost, ccNo);

                TicketPaymentResult paymentResult = ticketPaymentService.makePayment(tpr);

                if (paymentResult.paymentResponseCode == PaymentResponseCode.Success) {
                    return new TicketReservationResult(totalCost, Success);
                } else {
                    return new TicketReservationResult(totalCost, PaymentFailed);
                }
            }
        }
    }
}

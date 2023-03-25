package services;

import connectors.SeatReservationConnector;
import connectors.TicketPaymentConnector;
import models.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static models.ReservationResponseCode.*;
import static models.TicketType.*;


public final class TicketService {
    // TODO: Move external services to connectors package and make them return Futures
    private static final int MaxTicketsPerRequest = 20;

    private static final Map<TicketType, BigDecimal> TicketPrices = Map.of(
            Adult, new BigDecimal(20),
            Child, new BigDecimal(10)
    );
    private final TicketPaymentConnector ticketPaymentConnector;
    private final SeatReservationConnector seatReservationConnector;

    public TicketService(TicketPaymentConnector ticketPaymentConnector, SeatReservationConnector seatReservationConnector) {
        this.ticketPaymentConnector = ticketPaymentConnector;
        this.seatReservationConnector = seatReservationConnector;
    }

    public TicketReservationResult reserve(List<TicketTypeRequest> ttrList, String ccNo) {
        if (ttrList.stream().anyMatch(m -> m.ticketType == Infant || m.ticketType == Child) &&
                ttrList.stream().noneMatch(m -> m.ticketType == Adult)) {
            return new TicketReservationResult(new BigDecimal(0), ChildAndInfantTicketsMustBeWithAdult);
        } else {
            final int totalTicketsRequested = ttrList.stream().filter(tr -> tr.ticketType != Infant).mapToInt(tr -> tr.howMany).sum();
            if (totalTicketsRequested > MaxTicketsPerRequest) {
                return new TicketReservationResult(new BigDecimal(0), FailureTooManyTicketsInOneGo);
            } else {
                final BigDecimal totalCost = ttrList.stream()
                        .map(a -> TicketPrices.getOrDefault(a.ticketType, BigDecimal.ZERO)
                                .multiply(new BigDecimal(a.howMany)))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                SeatReservationResult seatReservationResult = seatReservationConnector.reserve(ttrList);
                if (seatReservationResult.seatReservationResponseCode == SeatReservationResponseCode.Success) {
                    final TicketPaymentRequest tpr = new TicketPaymentRequest(totalCost, ccNo);
                    final TicketPaymentResult paymentResult = ticketPaymentConnector.makePayment(tpr);
                    if (paymentResult.paymentResponseCode == PaymentResponseCode.Success) {
                        return new TicketReservationResult(totalCost, Success);
                    } else {
                        final SeatReservationResponseCode seatCancellationResponseCode =
                                seatReservationConnector.cancelReservations(seatReservationResult.seatsReserved);
                        if (seatCancellationResponseCode == SeatReservationResponseCode.Success) {
                            return new TicketReservationResult(totalCost, PaymentFailed);
                        } else {
                            // TODO: Check requirements:-
                            //   - could perhaps email someone internally about cancelling seats manually or put on a queue to auto-retry later
                            //   - or if one of the services (payment service maybe?) is capable of 2 phase commit then could roll back.
                            return new TicketReservationResult(totalCost, PaymentFailed);
                        }
                    }
                } else {
                    return new TicketReservationResult(totalCost, UnableToReserveSeats);
                }
            }
        }
    }
}

import models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import services.SeatReservationService;
import services.TicketPaymentService;
import services.TicketService;

import java.math.BigDecimal;
import java.util.List;

import static models.ReservationResponseCode.*;
import static models.TicketType.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TicketServiceTest {
    @Mock
    private TicketPaymentService ticketPaymentService;
    @Mock
    private SeatReservationService seatReservationService;

    private final String ccNo = "123456789";

    private void assertEqualResults(TicketReservationResult expectedResult, TicketReservationResult actual) {
        assertEquals(actual.totalCost, expectedResult.totalCost, "total costs don't match");
        assertEquals(actual.responseCode, expectedResult.responseCode, "response codes don't match");
    }

    private static final TicketPaymentResult TicketPaymentResultSuccess = new TicketPaymentResult(PaymentResponseCode.Success);
    private static final TicketPaymentResult TicketPaymentResultFailureCCDeclined = new TicketPaymentResult(PaymentResponseCode.CreditCardDeclined);
    private static final SeatReservationResult SeatReservationResultSuccess =
            new SeatReservationResult(SeatReservationResponseCode.Success, List.of("E4", "E5"));

    private TicketService ticketService() { return new TicketService(ticketPaymentService, seatReservationService); };

    @BeforeEach
    public void beforeEach() {
        reset(ticketPaymentService, seatReservationService);
    }

    @Test
    public void testOneInfantNoAdult() {
        List<TicketTypeRequest> requests = List.of(
                new TicketTypeRequest(Infant, 1)
        );

        TicketReservationResult result = ticketService().reserve(requests,ccNo);
        TicketReservationResult expectedResult = new TicketReservationResult(BigDecimal.valueOf(0), ChildAndInfantTicketsMustBeWithAdult);
        assertEqualResults(expectedResult, result);
    }

    @Test
    public void testOneChildNoAdult() {
        List<TicketTypeRequest> requests = List.of(new TicketTypeRequest(Child, 1));
        TicketReservationResult result = ticketService().reserve(requests, ccNo);
        TicketReservationResult expectedResult = new TicketReservationResult(BigDecimal.valueOf(0), ChildAndInfantTicketsMustBeWithAdult);
        assertEqualResults(expectedResult, result);
    }

    @Test
    public void testTooManyTickets() {
        List<TicketTypeRequest> requests = List.of(
                new TicketTypeRequest(Child, 11),
                new TicketTypeRequest(Adult, 10),
                new TicketTypeRequest(Infant, 1)
        );

        TicketReservationResult result = ticketService().reserve(requests, ccNo);
        TicketReservationResult expectedResult = new TicketReservationResult(BigDecimal.valueOf(0), FailureTooManyTicketsInOneGo);
        assertEqualResults(expectedResult, result);
    }

    @Test
    public void testMax20Tickets20AdultsAndChildrenAndOneInfant() {
        List<TicketTypeRequest> requests = List.of(
                new TicketTypeRequest(Child, 10),
                new TicketTypeRequest(Adult, 10),
                new TicketTypeRequest(Infant, 1)
        );
        when(ticketPaymentService.makePayment(any())).thenReturn(TicketPaymentResultSuccess);
        when(seatReservationService.reserve(any())).thenReturn(SeatReservationResultSuccess);
        TicketReservationResult result = ticketService().reserve(requests, ccNo);
        TicketReservationResult expectedResult = new TicketReservationResult(BigDecimal.valueOf(300), Success);
        assertEqualResults(expectedResult, result);
        verify(ticketPaymentService, times(1)).makePayment(any());
        verify(seatReservationService, times(1)).reserve(any());
    }

    @Test
    public void testMax20Tickets20AdultsAndChildrenAndOneInfantPaymentFails() {
        List<TicketTypeRequest> requests = List.of(
                new TicketTypeRequest(Child, 10),
                new TicketTypeRequest(Adult, 10),
                new TicketTypeRequest(Infant, 1)
        );
        when(ticketPaymentService.makePayment(any())).thenReturn(TicketPaymentResultFailureCCDeclined);
        when(seatReservationService.reserve(any())).thenReturn(SeatReservationResultSuccess);
        when(seatReservationService.cancelReservations(any())).thenReturn(SeatReservationResponseCode.Success);
        TicketReservationResult result = ticketService().reserve(requests, ccNo);
        TicketReservationResult expectedResult = new TicketReservationResult(BigDecimal.valueOf(300), PaymentFailed);
        verify(ticketPaymentService, times(1)).makePayment(any());
        verify(seatReservationService, times(1)).reserve(any());
        verify(seatReservationService, times(1)).cancelReservations(any());
    }

    @Test
    public void testMax20Tickets20AdultsAndChildrenAndOneInfantPaymentFailsUnableCancelSeats() {
        List<TicketTypeRequest> requests = List.of(
                new TicketTypeRequest(Child, 10),
                new TicketTypeRequest(Adult, 10),
                new TicketTypeRequest(Infant, 1)
        );
        when(ticketPaymentService.makePayment(any())).thenReturn(TicketPaymentResultFailureCCDeclined);
        when(seatReservationService.reserve(any())).thenReturn(SeatReservationResultSuccess);
        when(seatReservationService.cancelReservations(any())).thenReturn(SeatReservationResponseCode.SeatReservationServiceUnavailable);
        TicketReservationResult result = ticketService().reserve(requests, ccNo);
        TicketReservationResult expectedResult = new TicketReservationResult(BigDecimal.valueOf(300), PaymentFailed);
        verify(ticketPaymentService, times(1)).makePayment(any());
        verify(seatReservationService, times(1)).reserve(any());
        verify(seatReservationService, times(1)).cancelReservations(any());
        // TODO: Could verify email service called to inform someone that unable to cancel seat reservations
    }

}

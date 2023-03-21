import models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import services.TicketPaymentService;
import services.TicketService;

import java.math.BigDecimal;
import java.util.List;
import static models.ReservationResponseCode.*;
import static models.TicketType.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
//@ExtendWith(MockitoExtension.class)
//@RunWith(MockitoJUnitRunner.class)
@ExtendWith(MockitoExtension.class)
public class TicketServiceTest {

    @Mock
    static TicketPaymentService ticketPaymentService;

    static TicketPaymentRequest ticketPaymentRequest = new TicketPaymentRequest(new BigDecimal(0), "");

    private final String ccNo = "123456789";

    private void assertEqualResults(TicketReservationResult expectedResult, TicketReservationResult actual) {
        assertEquals(actual.totalCost, expectedResult.totalCost, "total costs don't match");
        assertEquals(actual.responseCode, expectedResult.responseCode, "response codes don't match");
    }

    private final static TicketPaymentResult ticketPaymentResultSuccess = new TicketPaymentResult(PaymentResponseCode.Success);

    @BeforeEach
    public void beforeEach() {
    }

    @Test
    public void testOneAdult() {
        TicketService ts = new TicketService(ticketPaymentService);
        List<TicketTypeRequest> requests = List.of(new TicketTypeRequest(Adult, 1));
        when(ticketPaymentService.makePayment(any())).thenReturn(ticketPaymentResultSuccess);
        TicketReservationResult result = ts.reserve(requests, ccNo);
        TicketReservationResult expectedResult = new TicketReservationResult(BigDecimal.valueOf(20), Success);
        assertEqualResults(expectedResult, result);
    }

    @Test
    public void testOneInfantNoAdult() {
        TicketService ts = new TicketService(ticketPaymentService);

        List<TicketTypeRequest> requests = List.of(
                new TicketTypeRequest(Infant, 1)
        );

        TicketReservationResult result = ts.reserve(requests,ccNo);
        TicketReservationResult expectedResult = new TicketReservationResult(BigDecimal.valueOf(0), ChildAndInfantTicketsMustBeWithAdult);
        assertEqualResults(expectedResult, result);
    }

    @Test
    public void testOneChildNoAdult() {
        TicketService ts = new TicketService(ticketPaymentService);

        List<TicketTypeRequest> requests = List.of(
                new TicketTypeRequest(Child, 1)
        );

        TicketReservationResult result = ts.reserve(requests, ccNo);
        TicketReservationResult expectedResult = new TicketReservationResult(BigDecimal.valueOf(0), ChildAndInfantTicketsMustBeWithAdult);
        assertEqualResults(expectedResult, result);
    }

    @Test
    public void testMax2021AdultsTickets() {
        TicketService ts = new TicketService(ticketPaymentService);

        List<TicketTypeRequest> requests = List.of(
                new TicketTypeRequest(Child, 11),
                new TicketTypeRequest(Adult, 10),
                new TicketTypeRequest(Infant, 1)
        );

        TicketReservationResult result = ts.reserve(requests, ccNo);
        TicketReservationResult expectedResult = new TicketReservationResult(BigDecimal.valueOf(0), FailureTooManyTicketsInOneGo);
        assertEqualResults(expectedResult, result);
    }

    @Test
    public void testMax20Tickets20AdultsAndChildrenAndOneInfant() {
        TicketService ts = new TicketService(ticketPaymentService);

        List<TicketTypeRequest> requests = List.of(
                new TicketTypeRequest(Child, 10),
                new TicketTypeRequest(Adult, 10),
                new TicketTypeRequest(Infant, 1)
        );
        when(ticketPaymentService.makePayment(any())).thenReturn(ticketPaymentResultSuccess);
        TicketReservationResult result = ts.reserve(requests, ccNo);
        TicketReservationResult expectedResult = new TicketReservationResult(BigDecimal.valueOf(300), Success);
        assertEqualResults(expectedResult, result);
    }

}

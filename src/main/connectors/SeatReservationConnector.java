package connectors;

import models.*;

import java.util.List;

public interface SeatReservationConnector {
    // Presumably this would be calling the external service via a HTTP GET or POST: Could use a Future here
    SeatReservationResult reserve(List<TicketTypeRequest> ttrList);
    SeatReservationResponseCode cancelReservations(List<String> seats);

}

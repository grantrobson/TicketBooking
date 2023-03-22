package connectors;

import models.*;

import java.util.List;

public interface SeatReservationConnector {
    SeatReservationResult reserve(List<TicketTypeRequest> ttrList);
    SeatReservationResponseCode cancelReservations(List<String> seats);

}

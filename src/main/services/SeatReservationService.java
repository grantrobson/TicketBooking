package services;

import models.*;

import java.util.List;

public interface SeatReservationService {
    SeatReservationResult reserve(List<TicketTypeRequest> ttrList);
    SeatReservationResponseCode cancelReservations(List<String> seats);

}

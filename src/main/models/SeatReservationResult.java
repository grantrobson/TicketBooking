package models;

import java.math.BigDecimal;
import java.util.List;

public final class SeatReservationResult {
    public final SeatReservationResponseCode seatReservationResponseCode;
    public final List<String> seatsReserved;
    public SeatReservationResult(SeatReservationResponseCode seatReservationResponseCode, List<String> seatsReserved) {
        this.seatReservationResponseCode = seatReservationResponseCode;
        this.seatsReserved = seatsReserved;
    }
}

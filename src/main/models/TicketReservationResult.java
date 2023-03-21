package models;

import java.math.BigDecimal;

public final class TicketReservationResult {
    public final BigDecimal totalCost;
    public final ReservationResponseCode responseCode;
    public TicketReservationResult(BigDecimal totalCost, ReservationResponseCode responseCode) {
        this.totalCost = totalCost;
        this.responseCode = responseCode;
    }
}

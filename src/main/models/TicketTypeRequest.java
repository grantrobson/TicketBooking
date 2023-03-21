package models;

import services.TicketService;

import java.math.BigDecimal;

public final class TicketTypeRequest {
    public final TicketType ticketType;
    public final int howMany;

    public TicketTypeRequest(TicketType ticketType, int howMany) {
        this.ticketType = ticketType;
        this.howMany = howMany;
    }
}
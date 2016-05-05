package de.htwdd.htwdresden.events;

/**
 * Event über neue Mensaspeisepläne
 *
 * @author Kay Förster
 */
public class UpdateMensaEvent {
    private final int forModus;

    public UpdateMensaEvent(final int forModus) {
        this.forModus = forModus;
    }

    public int getForModus() {
        return forModus;
    }
}

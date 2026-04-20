package pl.edu.agh.kis.pz1.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a hotel room with basic attributes and occupancy state.
 *
 * <p>The Room holds metadata (number, description, price, capacity) and current
 * stay state (guests, check-in date, planned checkout, notes). It provides simple
 * check-in and checkout operations and a textual representation suitable for CLI output.</p>
 */
public class Room {
    private final int roomNumber;
    private final String description;
    private final double price;
    private final int capacity;

    private List<Guest> guests = new ArrayList<>();
    private LocalDate checkinDate;
    private LocalDate checkoutDate;
    private int days;
    private boolean occupied;
    private String notes;

    /**
     * Creates a new Room with the given fixed attributes.
     *
     * @param roomNumber room number (e.g. 101)
     * @param description textual description of the room
     * @param price nightly price
     * @param capacity maximum number of guests
     */
    public Room(int roomNumber, String description, double price, int capacity) {
        this.roomNumber = roomNumber;
        this.description = description;
        this.price = price;
        this.capacity = capacity;
    }

    /**
     * Checks whether the room is currently available for check-in.
     *
     * @return true when the room is not occupied
     */
    public boolean isAvailable() {
        return !occupied;
    }

    /**
     * Marks the room as occupied by the provided guests starting at {@code checkinDate}
     * for the given number of {@code days} and stores optional {@code notes}.
     *
     * <p>The method copies the guest list to avoid external mutation and computes the
     * planned checkout date as {@code checkinDate.plusDays(days)}.</p>
     *
     * @param guests list of guests (firstName/lastName)
     * @param checkinDate start date of the stay
     * @param days planned length of stay in days
     * @param notes optional notes (may be null)
     */
    public void checkin(List<Guest> guests, LocalDate checkinDate, int days, String notes) {
        this.guests = new ArrayList<>(guests);
        this.checkinDate = checkinDate;
        this.days = days;
        this.notes = notes;
        this.occupied = true;
        this.checkoutDate = checkinDate.plusDays(days);
    }

    /**
     * Performs checkout: calculates total due based on nights spent (difference between
     * check-in date and current date) multiplied by nightly price, clears occupancy state
     * and returns the computed total.
     *
     * <p>If the room is not occupied this method returns 0.0.</p>
     *
     * @return total amount due for the stay, or 0.0 if room was not occupied
     */
    public double checkout() {
        if (isAvailable()) return 0.0;
        long nights = ChronoUnit.DAYS.between(checkinDate, LocalDate.now());
        double total = nights * price;
        guests.clear();
        checkinDate = null;
        checkoutDate = null;
        notes = null;
        days = 0;
        occupied = false;
        return total;
    }

    /**
     * @return room number
     */
    public int getRoomNumber() {
        return roomNumber;
    }

    /**
     * @return room description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return nightly price
     */
    public double getPrice() {
        return price;
    }

    /**
     * @return room capacity (max guests)
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Returns a live list of guests currently registered for the room.
     *
     * @return list of Guest instances (may be empty)
     */
    public List<Guest> getGuests() {
        return guests;
    }

    /**
     * @return check-in date or null if not occupied
     */
    public LocalDate getCheckinDate() {
        return checkinDate;
    }

    /**
     * @return planned checkout date or null if not occupied
     */
    public LocalDate getCheckoutDate() {
        return checkoutDate;
    }

    /**
     * @return planned number of days for the stay (0 when not occupied)
     */
    public int getDays() {
        return days;
    }

    /**
     * @return true if the room is currently occupied
     */
    public boolean isOccupied() {
        return occupied;
    }

    /**
     * @return notes associated with the current stay or null
     */
    public String getNotes() {
        return notes;
    }

    /**
     * Returns a multi-line textual representation of the room suitable for CLI output.
     * Includes occupancy details when the room is occupied.
     *
     * @return formatted string describing the room
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("room ").append(roomNumber)
                .append(" (").append(description).append("), price: ").append(price)
                .append(", capacity: ").append(capacity)
                .append(", occupied: ").append(occupied);

        if (occupied) {
            sb.append("\n  guests: ");
            for (Guest g : guests) {
                sb.append(g.getFirstName()).append(" ").append(g.getLastName()).append("; ");
            }
            sb.append("\n  check-in: ").append(checkinDate);
            sb.append("\n  planned checkout: ").append(checkoutDate);
            if (notes != null && !notes.isEmpty()) {
                sb.append("\n  notes: ").append(notes);
            }
        }

        return sb.toString();
    }
}

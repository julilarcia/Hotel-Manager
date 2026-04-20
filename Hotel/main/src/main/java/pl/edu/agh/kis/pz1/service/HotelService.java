package pl.edu.agh.kis.pz1.service;

import pl.edu.agh.kis.pz1.model.Guest;
import pl.edu.agh.kis.pz1.model.Hotel;
import pl.edu.agh.kis.pz1.model.Room;

import java.time.LocalDate;
import java.util.List;

/**
 * Service layer encapsulating hotel business logic.
 *
 * <p>Provides safe operations for check-in, checkout and read-only queries.
 * The class delegates storage to {@link Hotel} and room-level operations to {@link Room}.</p>
 */
public class HotelService {
    private final Hotel hotel;

    /**
     * Constructs a service for the provided hotel.
     *
     * @param hotel hotel instance to operate on (must not be null)
     */
    public HotelService(Hotel hotel) {
        this.hotel = hotel;
    }

    /**
     * Convenience check-in without explicit check-in date or notes.
     * Uses the current date as check-in date and empty notes.
     *
     * @param roomNumber room number to check into
     * @param guests     list of guests (firstName/lastName pairs)
     * @param days       planned number of days
     * @return true if check-in succeeded, false otherwise (invalid room, not available or over capacity)
     */
    public boolean checkin(int roomNumber, List<Guest> guests, int days) {
        return checkin(roomNumber, guests, LocalDate.now(), days, "");
    }

    /**
     * Full check-in operation with explicit check-in date and optional notes.
     *
     * <p>Validations performed:
     * - room exists,
     * - room is available (not currently occupied),
     * - number of guests does not exceed room capacity.</p>
     *
     * @param roomNumber  room number to check into
     * @param guests      list of guests
     * @param checkinDate start date of the stay
     * @param days        planned number of days
     * @param notes       optional notes (may be empty)
     * @return true when check-in completed successfully; false on validation failure
     */
    public boolean checkin(int roomNumber, List<Guest> guests, LocalDate checkinDate, int days, String notes) {
        Room room = hotel.getRoom(roomNumber);
        if (room == null || !room.isAvailable()) {
            return false;
        }

        if (guests.size() > room.getCapacity()) {
            return false;
        }

        room.checkin(guests, checkinDate, days, notes);
        return true;
    }

    /**
     * Performs checkout for the given room.
     *
     * <p>If the room does not exist or is not occupied the method returns 0.0.
     * Otherwise it delegates to {@link Room#checkout()} which clears occupancy
     * and returns the total amount due.</p>
     *
     * @param roomNumber room number to check out
     * @return total amount due for the stay or 0.0 when checkout failed / room not occupied
     */
    public double checkout(int roomNumber) {
        Room room = hotel.getRoom(roomNumber);
        if (room == null || room.isAvailable()) {
            return 0.0;
        }
        return room.checkout();
    }

    /**
     * Returns a textual view of the room state.
     *
     * @param roomNumber number of the room to view
     * @return room description string or "Room not found" if the room does not exist
     */
    public String view(int roomNumber) {
        Room room = hotel.getRoom(roomNumber);
        return room != null ? room.toString() : "Room not found";
    }

    /**
     * Lists all rooms in the hotel.
     *
     * @return list of all Room instances (may be empty)
     */
    public List<Room> listAllRooms() {
        return hotel.listRooms();
    }

    /**
     * Lists all currently available (vacant) rooms.
     *
     * @return list of available Room instances (may be empty)
     */
    public List<Room> listAvailableRooms() {
        return hotel.listAvailableRooms();
    }

    /**
     * Returns current nightly prices for all rooms.
     *
     * @return list of prices (double) for each room; order corresponds to {@link #listAllRooms()}
     */
    public List<Double> listPrices() {
        return hotel.listRooms().stream()
                .map(Room::getPrice)
                .toList();
    }

    /**
     * Exposes the underlying Hotel instance.
     *
     * @return hotel operated by this service
     */
    public Hotel getHotel() {
        return hotel;
    }
}

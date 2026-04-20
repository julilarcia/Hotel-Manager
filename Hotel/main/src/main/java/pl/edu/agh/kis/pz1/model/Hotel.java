package pl.edu.agh.kis.pz1.model;

import pl.edu.agh.kis.pz1.hotel.utils.MyMap;

import java.util.List;

/**
 * Represents a hotel as a collection of rooms indexed by room number.
 *
 * <p>Internally uses the project's {@link MyMap} implementation to store {@link Room}
 * instances. Exposes operations to add rooms, query rooms, list all rooms or only
 * available rooms, and retrieve basic hotel metadata.</p>
 */
public class Hotel {
    private String name;
    private MyMap<Integer, Room> rooms = new MyMap<>();

    /**
     * Creates a Hotel with the given name.
     *
     * @param name hotel name
     */
    public Hotel(String name) {
        this.name = name;
    }

    /**
     * Adds a room to the hotel.
     *
     * <p>If a room with the same number already exists the behavior depends on
     * {@link MyMap#put} (typically it will replace the previous entry).</p>
     *
     * @param room room to add
     */
    public void addRoom(Room room) {
        rooms.put(room.getRoomNumber(), room);
    }

    /**
     * Returns the Room with the specified room number or {@code null} if not found.
     *
     * @param roomNumber room number
     * @return Room instance or null when absent
     */
    public Room getRoom(int roomNumber) {
        return rooms.get(roomNumber);
    }

    /**
     * Returns whether the hotel contains a room with the given number.
     *
     * @param roomNumber room number
     * @return true if the room exists, false otherwise
     */
    public boolean hasRoom(int roomNumber) {
        return rooms.get(roomNumber) != null;
    }

    /**
     * Returns a list of all rooms in the hotel.
     *
     * @return list of Room instances; never null (may be empty)
     */
    public List<Room> listRooms() {
        return rooms.keys().stream()
                .map(rooms::get)
                .toList();
    }

    /**
     * Returns a list of available (vacant) rooms.
     *
     * @return list of available Room instances; may be empty
     */
    public List<Room> listAvailableRooms() {
        return listRooms().stream()
                .filter(Room::isAvailable)
                .toList();
    }

    /**
     * Returns the hotel name.
     *
     * @return hotel name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the total number of rooms registered in the hotel.
     *
     * @return number of rooms
     */
    public int getTotalRooms() {
        return rooms.keys().size();
    }

    /**
     * Returns a short human-readable description of the hotel.
     *
     * @return descriptive string for the hotel
     */
    @Override
    public String toString() {
        return "hotel '" + name + "' with " + getTotalRooms() + " rooms";
    }
}

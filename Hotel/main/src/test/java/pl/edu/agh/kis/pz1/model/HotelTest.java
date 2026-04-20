package pl.edu.agh.kis.pz1.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HotelTest {

    private Hotel hotel;

    @BeforeEach
    void setup() {
        hotel = new Hotel("TestHotel");
        hotel.addRoom(new Room(101, "single", 100.0, 1));
        hotel.addRoom(new Room(102, "double", 150.0, 2));
    }

    @Test
    void testGetName() {
        assertEquals("TestHotel", hotel.getName());
    }

    @Test
    void testAddAndGetRoom() {
        Room room = hotel.getRoom(101);
        assertNotNull(room);
        assertEquals(101, room.getRoomNumber());
        assertEquals("single", room.getDescription());
    }

    @Test
    void testHasRoom() {
        assertTrue(hotel.hasRoom(101));
        assertFalse(hotel.hasRoom(999));
    }

    @Test
    void testListRooms() {
        List<Room> rooms = hotel.listRooms();
        assertEquals(2, rooms.size());
        assertTrue(rooms.stream().anyMatch(r -> r.getRoomNumber() == 101));
        assertTrue(rooms.stream().anyMatch(r -> r.getRoomNumber() == 102));
    }

    @Test
    void testListAvailableRooms() {
        Room room = hotel.getRoom(101);
        room.checkin(List.of(new Guest("Pan", "Włodzimierz")), java.time.LocalDate.now(), 2, "");
        List<Room> available = hotel.listAvailableRooms();
        assertEquals(1, available.size());
        assertEquals(102, available.get(0).getRoomNumber());
    }

    @Test
    void testGetTotalRooms() {
        assertEquals(2, hotel.getTotalRooms());
    }

    @Test
    void testToString() {
        String output = hotel.toString();
        assertTrue(output.contains("hotel 'TestHotel'"));
        assertTrue(output.contains("2 rooms"));
    }
}
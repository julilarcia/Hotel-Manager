package pl.edu.agh.kis.pz1.model;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RoomTest {

    private Room room;
    private Guest guest;

    @BeforeEach
    void setup() {
        room = new Room(101, "single", 100.0, 1);
        guest = new Guest("Pan", "Włodzimierz");
    }

    @Test
    void testConstructorAndGetters() {
        assertEquals(101, room.getRoomNumber());
        assertEquals("single", room.getDescription());
        assertEquals(100.0, room.getPrice());
        assertEquals(1, room.getCapacity());
        assertTrue(room.getGuests().isEmpty());
        assertTrue(room.isAvailable());
    }

    @Test
    void testCheckinSetsFieldsCorrectly() {
        LocalDate date = LocalDate.of(2025, 11, 1);
        room.checkin(List.of(guest), date, 3, "preferuje ciszę");

        assertFalse(room.isAvailable());
        assertEquals(List.of(guest), room.getGuests());
        assertEquals(date, room.getCheckinDate());
        assertEquals(date.plusDays(3), room.getCheckoutDate());
        assertEquals(3, room.getDays());
        assertEquals("preferuje ciszę", room.getNotes());
    }

    @Test
    void testCheckoutClearsFieldsAndReturnsTotal() {
        LocalDate date = LocalDate.now().minusDays(2);
        room.checkin(List.of(guest), date, 2, "Test");
        double total = room.checkout();

        assertTrue(total >= 200.0);
        assertTrue(room.isAvailable());
        assertTrue(room.getGuests().isEmpty());
        assertNull(room.getCheckinDate());
        assertNull(room.getCheckoutDate());
        assertNull(room.getNotes());
        assertEquals(0, room.getDays());
    }

    @Test
    void testCheckoutWhenAvailableReturnsZero() {
        double total = room.checkout();
        assertEquals(0.0, total);
    }

    @Test
    void testToStringWhenAvailable() {
        String output = room.toString();
        assertTrue(output.contains("room 101"));
        assertTrue(output.contains("occupied: false"));
    }

    @Test
    void testToStringWhenOccupied() {
        room.checkin(List.of(guest), LocalDate.now(), 2, "preferuje ciszę");
        String output = room.toString();
        assertTrue(output.contains("Pan Włodzimierz"));
        assertTrue(output.contains("notes: preferuje ciszę"));
        assertTrue(output.contains("occupied: true"));
    }
}
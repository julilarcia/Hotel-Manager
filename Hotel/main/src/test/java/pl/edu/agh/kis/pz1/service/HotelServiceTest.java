package pl.edu.agh.kis.pz1.service;

import pl.edu.agh.kis.pz1.model.Guest;
import pl.edu.agh.kis.pz1.model.Hotel;
import pl.edu.agh.kis.pz1.model.Room;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HotelServiceTest {
    private HotelService service;

    @BeforeEach
    void setup() {
        Hotel hotel = new Hotel("TestHotel");
        hotel.addRoom(new Room(101, "single", 100.0, 1));
        hotel.addRoom(new Room(102, "double", 150.0, 2));
        service = new HotelService(hotel);
    }

    @Test
    void testCheckinSuccess() {
        Guest guest = new Guest("Pan", "Włodzimierz");
        boolean result = service.checkin(101, List.of(guest), LocalDate.now(), 2, "preferuje ciszę");
        assertTrue(result);
        assertTrue(service.view(101).contains("Pan Włodzimierz"));
    }

    @Test
    void testCheckinTooManyGuests() {
        List<Guest> guests = List.of(
                new Guest("A", "B"),
                new Guest("C", "D"),
                new Guest("E", "F")
        );
        boolean result = service.checkin(101, guests, LocalDate.now(), 2, "");
        assertFalse(result);
    }

    @Test
    void testCheckinToNonexistentRoom() {
        Guest guest = new Guest("Pan", "Włodzimierz");
        boolean result = service.checkin(999, List.of(guest), LocalDate.now(), 2, "");
        assertFalse(result);
    }

    @Test
    void testCheckoutSuccess() {
        Guest guest = new Guest("Pani", "Kajak");
        service.checkin(102, List.of(guest), LocalDate.now().minusDays(2), 2, "");
        double total = service.checkout(102);
        assertTrue(total >= 300.0);
        assertTrue(service.view(102).contains("occupied: false"));
    }

    @Test
    void testCheckoutFailure() {
        double total = service.checkout(999);
        assertEquals(0.0, total);
    }

    @Test
    void testViewOccupiedRoom() {
        Guest guest = new Guest("Pan", "Włodzimierz");
        service.checkin(101, List.of(guest), 1);
        String view = service.view(101);
        assertTrue(view.contains("Pan Włodzimierz"));
    }

    @Test
    void testViewInvalidRoom() {
        String view = service.view(999);
        assertEquals("Room not found", view);
    }

    @Test
    void testListAllRooms() {
        List<Room> rooms = service.listAllRooms();
        assertEquals(2, rooms.size());
    }

    @Test
    void testListAvailableRooms() {
        Guest guest = new Guest("Pan", "Włodzimierz");
        service.checkin(101, List.of(guest), 1);
        List<Room> available = service.listAvailableRooms();
        assertEquals(1, available.size());
        assertEquals(102, available.get(0).getRoomNumber());
    }

    @Test
    void testListPrices() {
        List<Double> prices = service.listPrices();
        assertEquals(List.of(100.0, 150.0), prices);
    }

}

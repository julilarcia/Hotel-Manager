package pl.edu.agh.kis.pz1.io;

import pl.edu.agh.kis.pz1.model.Guest;
import pl.edu.agh.kis.pz1.model.Hotel;
import pl.edu.agh.kis.pz1.model.Room;
import org.junit.jupiter.api.Test;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;



import static org.junit.jupiter.api.Assertions.*;

class HotelDataLoaderTest {

    @Test
    void testLoadFromCsvWithGuestsAndNotes() throws Exception {
        Path tempFile = Files.createTempFile("hotel_test", ".csv");

        try (PrintWriter writer = new PrintWriter(new FileWriter(tempFile.toFile()))) {
            writer.println("roomNumber,description,price,capacity,guests,checkinDate,notes");
            writer.println("101,Single,100.0,1,Pan Włodzimierz,2025-11-01,preferuje ciszę");
            writer.println("102,Double,150.0,2,,,");

            writer.flush();
        }

        Hotel hotel = HotelDataLoader.loadFromCsv(tempFile.toString(), "TestHotel");

        assertEquals("TestHotel", hotel.getName());
        assertEquals(2, hotel.getTotalRooms());

        Room room101 = hotel.getRoom(101);
        assertNotNull(room101);
        assertFalse(room101.isAvailable());
        assertEquals("preferuje ciszę", room101.getNotes());
        assertEquals(LocalDate.of(2025, 11, 1), room101.getCheckinDate());
        assertEquals(1, room101.getGuests().size());
        Guest guest = room101.getGuests().get(0);
        assertEquals("Pan", guest.getFirstName());
        assertEquals("Włodzimierz", guest.getLastName());

        Room room102 = hotel.getRoom(102);
        assertNotNull(room102);
        assertTrue(room102.isAvailable());
        assertEquals(0, room102.getGuests().size());
    }

    @Test
    void testLoadFromCsvHandlesException() {
        String invalidPath = "/nonexistent/path/hotel.csv";
        Hotel hotel = HotelDataLoader.loadFromCsv(invalidPath, "BrokenHotel");
        assertNotNull(hotel, "hotel should not be null even if loading failed");
        assertTrue(hotel.listRooms().isEmpty(), "hotel should have no rooms if loading failed");
    }
}

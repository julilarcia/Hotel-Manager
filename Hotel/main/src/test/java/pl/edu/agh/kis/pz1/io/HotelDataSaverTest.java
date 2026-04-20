package pl.edu.agh.kis.pz1.io;

import pl.edu.agh.kis.pz1.model.Guest;
import pl.edu.agh.kis.pz1.model.Hotel;
import pl.edu.agh.kis.pz1.model.Room;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HotelDataSaverTest {

    @Test
    void testSaveToCsv() throws Exception {

        Hotel hotel = new Hotel("TestHotel");
        Room room1 = new Room(101, "single", 100.0, 1);
        room1.checkin(List.of(new Guest("Pan", "Włodzimierz")), LocalDate.of(2025, 11, 1), 2, "preferuje ciszę");
        Room room2 = new Room(102, "double", 150.0, 2);
        hotel.addRoom(room1);
        hotel.addRoom(room2);


        Path tempFile = Files.createTempFile("hotel_save_test", ".csv");
        HotelDataSaver.saveToCsv(hotel, tempFile.toString());


        try (BufferedReader reader = new BufferedReader(new FileReader(tempFile.toFile()))) {
            String header = reader.readLine();
            assertEquals("roomNumber,description,price,capacity,guests,checkinDate,notes", header);

            String line1 = reader.readLine();
            assertTrue(line1.contains("101"));
            assertTrue(line1.contains("Pan Włodzimierz"));
            assertTrue(line1.contains("2025-11-01"));
            assertTrue(line1.contains("preferuje ciszę"));

            String line2 = reader.readLine();
            assertTrue(line2.contains("102"));
            assertFalse(line2.contains("Pan"));
        }
    }

    @Test
    void testSaveToCsvHandlesException() {
        Hotel hotel = new Hotel("BrokenHotel");
        hotel.addRoom(new Room(101, "single", 100.0, 1));
        String invalidPath = "/root/forbidden/hotel.csv";
        HotelDataSaver.saveToCsv(hotel, invalidPath);
        File file = new File(invalidPath);
        assertFalse(file.exists(), "file cannot be created with the wrong path");
    }
}
package pl.edu.agh.kis.pz1.io;

import pl.edu.agh.kis.pz1.model.Guest;
import pl.edu.agh.kis.pz1.model.Hotel;
import pl.edu.agh.kis.pz1.model.Room;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class responsible for loading hotel configuration and state from CSV files.
 *
 * <p>Behavioral notes:
 * - The first line of the CSV is treated as a header and skipped.
 * - Each subsequent line is split by comma (with {@code split(",", -1)} to preserve empty fields).
 * - Expected columns (by index): roomNumber, description, price, capacity, guests, checkinDate, notes.
 * - If the guests field is non-empty, guests are parsed (pipe-delimited), check-in date is parsed
 *   (or set to now if blank), and {@link Room#checkin} is called with the parsed data.
 * - On any I/O or parsing error the method logs the exception and returns a {@link Hotel} instance
 *   (possibly empty) instead of throwing.</p>
 */
public class HotelDataLoader {
    private static final Logger logger = LoggerFactory.getLogger(HotelDataLoader.class);

    private HotelDataLoader() {
        // utility class — no instances allowed
    }

    /**
     * Parse room basic attributes from CSV columns.
     *
     * <p>Expects parts array with at least 4 elements:
     * parts[0] = roomNumber, parts[1] = description, parts[2] = price, parts[3] = capacity.</p>
     *
     * @param parts CSV-split token array for a single row
     * @return new Room instance constructed from parsed values
     * @throws NumberFormatException if numeric fields cannot be parsed
     * @throws ArrayIndexOutOfBoundsException if parts has fewer than 4 elements
     */
    private static Room parseRoom(String[] parts) {
        int roomNumber = Integer.parseInt(parts[0]);
        String description = parts[1];
        double price = Double.parseDouble(parts[2]);
        int capacity = Integer.parseInt(parts[3]);
        return new Room(roomNumber, description, price, capacity);
    }

    /**
     * Parse the guests field into a list of {@link Guest} objects.
     *
     * <p>The guests field is expected to contain zero or more guest entries separated by '|'.
     * Each guest entry is expected to contain at least two tokens (firstName and lastName)
     * separated by whitespace; additional name tokens are ignored.</p>
     *
     * Examples:
     * - "Jan Kowalski" -> Guest("Jan","Kowalski")
     * - "A B|C D" -> two guests</p>
     *
     * @param guestField raw guests cell from CSV (may be empty)
     * @return list of parsed Guest objects (empty if input is blank or no valid names)
     */
    private static List<Guest> parseGuests(String guestField) {
        List<Guest> guests = new ArrayList<>();
        String[] guestTokens = guestField.split("\\|");
        for (String token : guestTokens) {
            String[] nameParts = token.trim().split(" ");
            if (nameParts.length >= 2) {
                guests.add(new Guest(nameParts[0], nameParts[1]));
            }
        }
        return guests;
    }

    /**
     * Parse check-in date field.
     *
     * <p>If the dateField is blank, returns {@link LocalDate#now()}; otherwise parses ISO date
     * string {@code yyyy-MM-dd} via {@link LocalDate#(String)}.</p>
     *
     * @param dateField raw date cell from CSV (may be blank)
     * @return parsed LocalDate (current date when blank)
     * @throws java.time.format.DateTimeParseException if non-blank date is not ISO format
     */
    private static LocalDate parseCheckinDate(String dateField) {
        return dateField.isBlank() ? LocalDate.now() : LocalDate.parse(dateField);
    }

    /**
     * Extract notes value from CSV parts array if present.
     *
     * @param parts CSV-split token array for a single row
     * @return notes string or empty string when not provided
     */
    private static String parseNotes(String[] parts) {
        return parts.length > 6 ? parts[6] : "";
    }

    /**
     * Load hotel configuration and state from a CSV file.
     *
     * <p>The method constructs a {@link Hotel} named {@code hotelName} and populates it with rooms
     * parsed from the file. If a guests field is present and non-empty, the room will be marked
     * as occupied via {@link Room#checkin(List, LocalDate, int, String)}; note that this loader
     * currently passes a fixed {@code 3} as the days parameter to {@code checkin}.</p>
     *
     * <p>On any exception (I/O or parsing) the method logs the problem and returns the Hotel instance
     * constructed so far (may be empty).</p>
     *
     * @param filePath  path to the CSV file
     * @param hotelName name for the created Hotel instance
     * @return populated Hotel instance (never null)
     */
    public static Hotel loadFromCsv(String filePath, String hotelName) {
        Hotel hotel = new Hotel(hotelName);
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String header = reader.readLine();
            logger.debug("CSV header skipped: {}", header);
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                Room room = parseRoom(parts);

                if (!parts[4].isBlank()) {
                    List<Guest> guests = parseGuests(parts[4]);
                    LocalDate checkinDate = parseCheckinDate(parts[5]);
                    String notes = parseNotes(parts);
                    room.checkin(guests, checkinDate, 3, notes);
                }

                hotel.addRoom(room);
            }
        } catch (Exception e) {
            logger.error("error while loading the file: {}", e.getMessage(), e);
        }
        return hotel;
    }
}

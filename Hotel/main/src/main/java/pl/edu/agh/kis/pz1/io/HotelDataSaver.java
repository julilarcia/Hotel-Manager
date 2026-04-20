package pl.edu.agh.kis.pz1.io;

import pl.edu.agh.kis.pz1.model.Guest;
import pl.edu.agh.kis.pz1.model.Hotel;
import pl.edu.agh.kis.pz1.model.Room;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Utility class responsible for saving hotel state to a CSV file.
 *
 * <p>The CSV header and row layout match the format consumed by {@link HotelDataLoader}:
 * roomNumber,description,price,capacity,guests,checkinDate,notes</p>
 *
 * <p>Guests are serialized as "FirstName LastName" entries joined with '|' when there are
 * multiple guests. Dates are formatted as ISO_LOCAL_DATE (yyyy-MM-dd). Empty fields are
 * emitted as empty CSV cells.</p>
 */
public class HotelDataSaver {
    private static final Logger logger = LoggerFactory.getLogger(HotelDataSaver.class);

    private HotelDataSaver() {
        // utility class — no instances allowed
    }

    /**
     * Saves the current state of the given hotel into a CSV file at {@code filePath}.
     *
     * <p>Each room produces one CSV row. If a room has guests, the guests column contains
     * guest entries like "FirstName LastName" joined by '|' and checkinDate and notes are
     * written; otherwise guests and checkinDate cells are left empty.</p>
     *
     * @param hotel    hotel instance to persist (expected non-null)
     * @param filePath output file path
     */
    public static void saveToCsv(Hotel hotel, String filePath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("roomNumber,description,price,capacity,guests,checkinDate,notes");

            for (Room room : hotel.listRooms()) {
                StringBuilder line = new StringBuilder();
                line.append(room.getRoomNumber()).append(",");
                line.append(escapeCsvField(room.getDescription())).append(",");
                line.append(room.getPrice()).append(",");
                line.append(room.getCapacity()).append(",");

                List<Guest> guests = room.getGuests();
                if (guests != null && !guests.isEmpty()) {
                    String guestString = guests.stream()
                            .map(g -> escapeCsvField(g.getFirstName() + " " + g.getLastName()))
                            .reduce((a, b) -> a + "|" + b)
                            .orElse("");
                    line.append(guestString).append(",");
                    line.append(room.getCheckinDate().format(DateTimeFormatter.ISO_LOCAL_DATE)).append(",");
                    line.append(escapeCsvField(room.getNotes() != null ? room.getNotes() : ""));
                } else {
                    // guests empty, checkinDate empty, notes empty
                    line.append(",").append(",");
                    line.append("");
                }

                writer.println(line);
            }

            logger.info("hotel saved to a file: {}", filePath);

        } catch (Exception e) {
            logger.error("error while saving the file: {}", e.getMessage(), e);
        }
    }

    /**
     * Minimal CSV field escaper used to avoid breaking CSV when fields contain commas or newlines.
     *
     * <p>If a field contains comma, quote or newline, the field is wrapped in double quotes
     * and internal double quotes are escaped by doubling them, following basic CSV quoting rules.</p>
     *
     * @param field raw field value
     * @return escaped field safe for naive CSV writing
     */
    private static String escapeCsvField(String field) {
        if (field == null || field.isEmpty()) {
            return "";
        }
        boolean needsQuoting = field.contains(",") || field.contains("\"") || field.contains("\n") || field.contains("\r");
        if (!needsQuoting) {
            return field;
        }
        String escaped = field.replace("\"", "\"\"");
        return "\"" + escaped + "\"";
    }
}
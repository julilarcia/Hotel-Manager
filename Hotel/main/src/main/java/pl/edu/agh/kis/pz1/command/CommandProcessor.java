package pl.edu.agh.kis.pz1.command;

import pl.edu.agh.kis.pz1.model.Guest;
import pl.edu.agh.kis.pz1.model.Room;
import pl.edu.agh.kis.pz1.service.HotelService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Processes textual commands from the REPL and delegates actions to {@link HotelService}.
 *
 * <p>Supported commands: checkin, checkout, view, list, listAvailable, prices, save.</p>
 */
public class CommandProcessor {

    private final HotelService hotelService;
    private static final String USAGE = "usage: checkin <roomNumber> <firstName> <lastName> [...more guests] <days> [checkinDate] [notes]";
    private static final String INVALID_ROOM = "invalid room number";
    private static final String INVALID_DAYS = "invalid number of days";
    private static final String MISSING_DAYS = "missing number of days";
    private static final String CHECKIN_FAILED = "check-in failed";
    private static final String CHECKOUT_FAILED = "checkout failed";
    private static final String USAGE_VIEW = "usage: view <roomNumber>";

    /**
     * Creates a CommandProcessor that uses the provided {@link HotelService}.
     *
     * @param hotelService service handling hotel operations
     */
    public CommandProcessor(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    /**
     * Processes a raw input string (single command) and returns a textual response.
     *
     * <p>Input is trimmed and tokenized by whitespace. The method handles parsing,
     * validation and invokes appropriate handler methods. Any unexpected exception
     * is caught and returned as an error message.</p>
     *
     * @param input raw command line supplied by the user
     * @return human-readable result or error string
     */
    public String process(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "empty command";
        }
        String[] parts = input.trim().split("\\s+");
        String command = parts[0].toLowerCase();

        try {
            switch (command) {
                case "checkin":
                    return handleCheckin(parts);
                case "checkout":
                    return handleCheckout(parts);
                case "view":
                    return handleView(parts);
                case "list":
                    return handleList();
                case "listavailable":
                    return handleListAvailable();
                case "prices":
                    return handlePrices();
                case "save":
                    return handleSave();
                default:
                    return "unknown command: " + command;
            }
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }

    /**
     * Handles the "save" command by persisting current hotel state to CSV.
     *
     * @return message confirming save
     */
    private String handleSave() {
        pl.edu.agh.kis.pz1.io.HotelDataSaver.saveToCsv(hotelService.getHotel(), "hotel_saved.csv");
        return "hotel zapisany do hotel_saved.csv";
    }

    /**
     * Finds the index of the days token and (optionally) index of the date token.
     *
     * <p>Searches tokens starting at index 2 because parts[0] is the command and parts[1]
     * is expected to be the room number. The first token that parses as an integer is
     * treated as the days count. If the following token matches ISO date pattern
     * (yyyy-mm-dd), its index is returned as dateIndex; otherwise -1.</p>
     *
     * @param parts tokenized input
     * @return int array of length two: [daysIndex, dateIndex] (or [-1, -1] if not found)
     */
    private int[] findDaysAndDateIndex(String[] parts) {
        for (int i = 2; i < parts.length; i++) {
            if (parseDays(parts[i]) != null) {
                int dateIndex = (i + 1 < parts.length && parts[i + 1].matches("\\d{4}-\\d{2}-\\d{2}")) ? i + 1 : -1;
                return new int[]{i, dateIndex};
            }
        }
        return new int[]{-1, -1};
    }

    /**
     * Tries to parse an integer number of days from a token.
     *
     * @param value token to parse
     * @return Integer days or null if parsing fails
     */
    private Integer parseDays(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Extracts a list of Guest objects from token array given the index where days begin.
     *
     * <p>Guests are expected in consecutive pairs: firstName lastName. Validates name tokens
     * against a basic letter-only regex (including Polish diacritics). Throws
     * {@link IllegalArgumentException} on malformed input.</p>
     *
     * @param parts     tokenized input
     * @param daysIndex index where the days token is located (exclusive upper bound for guests)
     * @return list of parsed Guest instances
     * @throws IllegalArgumentException when a guest name is incomplete or invalid
     */
    private List<Guest> extractGuests(String[] parts, int daysIndex) {
        List<Guest> guests = new ArrayList<>();
        for (int i = 2; i < daysIndex; i += 2) {
            if (i + 1 >= daysIndex) {
                throw new IllegalArgumentException("incomplete guest name at position " + i);
            }

            if (!parts[i].matches("[A-Za-zżźćńółęąśŻŹĆĄŚĘŁÓŃ]+") ||
                    !parts[i + 1].matches("[A-Za-zżźćńółęąśŻŹĆĄŚĘŁÓŃ]+")) {
                throw new IllegalArgumentException("incomplete guest name at position " + i);
            }

            guests.add(new Guest(parts[i], parts[i + 1]));
        }
        return guests;
    }

    /**
     * Parses room number from token.
     *
     * @param value token to parse
     * @return Integer room number or null if parsing fails
     */
    private Integer parseRoomNumber(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Handles the "checkin" command.
     *
     * <p>Expected form:
     * checkin &lt;roomNumber&gt; &lt;firstName&gt; &lt;lastName&gt; [...more guests] &lt;days&gt; [checkinDate] [notes]</p>
     *
     * <p>Performs validation, builds Guest list, parses optional date and notes, and invokes
     * {@link HotelService#checkin}.</p>
     *
     * @param parts tokenized input
     * @return result message
     */
    private String handleCheckin(String[] parts) {
        if (parts.length < 2) return USAGE;

        Integer roomNumber = parseRoomNumber(parts[1]);
        if (roomNumber == null) return INVALID_ROOM;

        int[] indices = findDaysAndDateIndex(parts);
        int daysIndex = indices[0];
        int dateIndex = indices[1];

        if (daysIndex == -1) return MISSING_DAYS;

        Integer days = parseDays(parts[daysIndex]);
        if (days == null) return INVALID_DAYS;

        LocalDate checkinDate = (dateIndex != -1) ? LocalDate.parse(parts[dateIndex]) : LocalDate.now();

        List<Guest> guests;
        try {
            guests = extractGuests(parts, daysIndex);
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }

        int noteStart = (dateIndex != -1) ? dateIndex + 1 : daysIndex + 1;
        String notes = (noteStart < parts.length) ? String.join(" ", Arrays.copyOfRange(parts, noteStart, parts.length)) : "";

        boolean success = hotelService.checkin(roomNumber, guests, checkinDate, days, notes);
        return success ? "check-in successful for " + guests.size() + " guest(s)" : CHECKIN_FAILED;
    }

    /**
     * Handles the "checkout" command.
     *
     * <p>Expects: checkout &lt;roomNumber&gt;</p>
     *
     * @param parts tokenized input
     * @return result message with total or failure message
     */
    private String handleCheckout(String[] parts) {
        Integer roomNumber;
        try {
            roomNumber = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            return INVALID_ROOM;
        }

        double total = hotelService.checkout(roomNumber);
        if (total <= 0) return CHECKOUT_FAILED;
        return "checkout complete. total: " + total;
    }

    /**
     * Handles the "view" command.
     *
     * <p>Expects: view &lt;roomNumber&gt;</p>
     *
     * @param parts tokenized input
     * @return textual room view or error message
     */
    private String handleView(String[] parts) {
        if (parts.length < 2) return USAGE_VIEW;

        Integer roomNumber;
        try {
            roomNumber = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            return INVALID_ROOM;
        }

        return hotelService.view(roomNumber);
    }

    /**
     * Handles the "list" command returning all rooms as a newline-separated string.
     *
     * @return listing or 'no rooms found' message
     */
    private String handleList() {
        List<Room> rooms = hotelService.listAllRooms();
        if (rooms.isEmpty()) return "no rooms found";
        List<String> output = new ArrayList<>();
        for (Room room : rooms) {
            output.add(room.toString());
        }
        return String.join("\n", output);
    }

    /**
     * Handles the "listAvailable" command returning available rooms.
     *
     * @return listing or 'no available rooms' message
     */
    private String handleListAvailable() {
        List<Room> rooms = hotelService.listAvailableRooms();
        if (rooms.isEmpty()) return "no available rooms";
        List<String> output = new ArrayList<>();
        for (Room room : rooms) {
            output.add(room.toString());
        }
        return String.join("\n", output);
    }

    /**
     * Handles the "prices" command returning current room prices.
     *
     * @return textual prices list or 'no prices available' message
     */
    private String handlePrices() {
        List<Double> prices = hotelService.listPrices();
        if (prices.isEmpty()) return "no prices available";
        return "room prices: " + prices.toString();
    }
}

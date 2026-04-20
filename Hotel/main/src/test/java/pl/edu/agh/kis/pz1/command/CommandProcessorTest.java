package pl.edu.agh.kis.pz1.command;

import pl.edu.agh.kis.pz1.model.Guest;
import pl.edu.agh.kis.pz1.model.Hotel;
import pl.edu.agh.kis.pz1.model.Room;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.edu.agh.kis.pz1.service.HotelService;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CommandProcessorTest {

    private CommandProcessor processor;

    @BeforeEach
    void setup() {
        Hotel hotel = new Hotel("TestHotel");
        hotel.addRoom(new Room(101, "single", 100.0, 1));
        hotel.addRoom(new Room(102, "double", 150.0, 2));
        processor = new CommandProcessor(new HotelService(hotel));
    }

    @Test
    void testEmptyCommand() {
        String result = processor.process("   ");
        assertEquals("empty command", result);
    }

    @Test
    void testUnknownCommand() {
        String result = processor.process("flyaway");
        assertEquals("unknown command: flyaway", result);
    }

    @Test
    void testCheckinSuccess() {
        String result = processor.process("checkin 101 Pan Włodzimierz 1");
        assertTrue(result.startsWith("check-in successful"), "expected check-in confirmation");
    }

    @Test
    void testCheckinTooManyGuests() {
        String result = processor.process("checkin 101 Pan Włodzimierz Pani Kajak 1");
        assertEquals("check-in failed", result);
    }

    @Test
    void testCheckinWithDateAndNote() {
        String result = processor.process("checkin 102 Pan Włodzimierz Pani Kajak 3 2025-11-01 preferuje_ciszę");
        assertTrue(result.startsWith("check-in successful"), "expected check-in confirmation with date and note");
    }

    @Test
    void testCheckoutSuccess() {
        String checkin = processor.process("checkin 101 Pan Włodzimierz 1 2025-11-01");
        assertTrue(checkin.startsWith("check-in successful"), "check-in should succeed before checkout");

        String result = processor.process("checkout 101");
        assertTrue(result.startsWith("checkout complete"), "expected checkout confirmation");
    }

    @Test
    void testCheckoutFailure() {
        String result = processor.process("checkout 999");
        assertEquals("checkout failed", result);
    }

    @Test
    void testViewOccupiedRoom() {
        processor.process("checkin 101 Pan Włodzimierz 1");
        String result = processor.process("view 101");
        assertTrue(result.contains("Pan Włodzimierz"), "expected guest name in room view");
    }

    @Test
    void testViewInvalidRoom() {
        String result = processor.process("view 999");
        assertEquals("Room not found", result);
    }

    @Test
    void testListRooms() {
        String result = processor.process("list");
        assertTrue(result.contains("room 101"), "expected Room 101 in list");
        assertTrue(result.contains("room 102"), "expected Room 102 in list");
    }

    @Test
    void testListAvailableRooms() {
        processor.process("checkin 101 Pan Włodzimierz 1");
        String result = processor.process("listavailable");
        assertTrue(result.contains("room 102"), "expected Room 102 to be available");
        assertFalse(result.contains("room 101"), "expected Room 101 to be occupied");
    }

    @Test
    void testPrices() {
        String result = processor.process("prices");
        assertTrue(result.contains("room prices"), "Expected price header");
        assertTrue(result.contains("100.0"), "Expected price for Room 101");
        assertTrue(result.contains("150.0"), "Expected price for Room 102");
    }

    @Test
    void testSaveCommand() {
        String result = processor.process("save");
        assertEquals("hotel zapisany do hotel_saved.csv", result);
    }

    @Test
    void testCheckinMissingDays() {
        String result = processor.process("checkin 101 Pan Włodzimierz");
        assertEquals("missing number of days", result);
    }

    @Test
    void testCheckinInvalidRoomNumber() {
        String result = processor.process("checkin abc Pan Włodzimierz 1");
        assertEquals("invalid room number", result);
    }

    @Test
    void testViewMissingRoomNumber() {
        String result = processor.process("view");
        assertEquals("usage: view <roomNumber>", result);
    }

    @Test
    void testNullCommand() {
        String result = processor.process(null);
        assertEquals("empty command", result);
    }

    @Test
    void testListEmptyHotel() {
        Hotel emptyHotel = new Hotel("EmptyHotel");
        CommandProcessor emptyProcessor = new CommandProcessor(new HotelService(emptyHotel));
        String result = emptyProcessor.process("list");
        assertEquals("no rooms found", result);
    }

    @Test
    void testCheckinIncompleteGuestName() {
        String result = processor.process("checkin 101 Pan 1");
        assertTrue(result.contains("incomplete guest name"), "expected error for incomplete guest name");
    }

    @Test
    void testProcessHandlesUnexpectedException() {
        HotelService faultyService = new HotelService(new Hotel("Faulty")) {
            @Override
            public boolean checkin(int roomNumber, List<Guest> guests, LocalDate date, int days, String notes) {
                throw new RuntimeException("simulated failure");
            }
        };
        CommandProcessor faultyProcessor = new CommandProcessor(faultyService);

        String result = faultyProcessor.process("checkin 101 Pan Kajak 1");
        assertTrue(result.startsWith("error: simulated failure"), "expected error message from catch block");
    }


    @Test
    void testCheckinFailsWhenTooManyGuests() {
        String result = processor.process("checkin 101 Pan Kajak Pani Woda 1");
        assertEquals("check-in failed", result);
    }

    @Test
    void testListRoomsEmptyHotel() {
        CommandProcessor emptyProcessor = new CommandProcessor(new HotelService(new Hotel("Empty")));
        String result = emptyProcessor.process("list");
        assertEquals("no rooms found", result);
    }

    @Test
    void testListAvailableRoomsNoneAvailable() {
        processor.process("checkin 101 Pan Kajak 1");
        processor.process("checkin 102 Pani Woda 1");
        String result = processor.process("listavailable");
        assertEquals("no available rooms", result);
    }

    @Test
    void testPricesEmptyHotel() {
        CommandProcessor emptyProcessor = new CommandProcessor(new HotelService(new Hotel("Empty")));
        String result = emptyProcessor.process("prices");
        assertEquals("no prices available", result);
    }

    @Test
    void testCheckinWithNonNumericDays() {
        String result = processor.process("checkin 101 Pan Kajak abc");
        assertEquals("missing number of days", result);
    }

    @Test
    void testCheckoutWithInvalidRoomNumber() {
        String input = "checkout abc";
        String result = processor.process(input);
        assertEquals("invalid room number", result);
    }

    @Test
    void testExtractGuestsThrowsWhenIncompleteName() throws Exception {
        String[] parts = {"checkin", "101", "Pan"};
        int daysIndex = 3;

        Method method = CommandProcessor.class.getDeclaredMethod("extractGuests", String[].class, int.class);
        method.setAccessible(true);
        InvocationTargetException ex = assertThrows(
                InvocationTargetException.class,
                () -> method.invoke(processor, (Object) parts, daysIndex)
        );

        Throwable cause = ex.getCause();
        assertTrue(cause instanceof IllegalArgumentException);
        assertEquals("incomplete guest name at position 2", cause.getMessage());

    }
    @Test
    void testHandleViewHandlesNumberFormatException() {
        String input = "view abc";
        String result = processor.process(input);
        assertEquals("invalid room number", result);
    }

    @Test
    void testCheckinWithInvalidGuestCharacters() {
        String result = processor.process("checkin 101 P@n Kajak 1");
        assertTrue(result.contains("incomplete guest name at position 2"));
    }
}
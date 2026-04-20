package pl.edu.agh.kis.pz1.model;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GuestTest {

    @Test
    void testBasicConstructor() {
        Guest guest = new Guest("Pan", "Włodzimierz");
        assertEquals("Pan", guest.getFirstName());
        assertEquals("Włodzimierz", guest.getLastName());
        assertNull(guest.getEmail());
        assertNull(guest.getPhoneNumber());
        assertNull(guest.getNotes());
    }

    @Test
    void testFullConstructor() {
        Guest guest = new Guest("Pani", "Kajak", "pani@example.com", "696969699", "alergia na pierze");
        assertEquals("Pani", guest.getFirstName());
        assertEquals("Kajak", guest.getLastName());
        assertEquals("pani@example.com", guest.getEmail());
        assertEquals("696969699", guest.getPhoneNumber());
        assertEquals("alergia na pierze", guest.getNotes());
    }

    @Test
    void testToStringBasic() {
        Guest guest = new Guest("Pan", "Włodzimierz");
        String output = guest.toString();
        assertEquals("Pan Włodzimierz", output);
    }

    @Test
    void testToStringFull() {
        Guest guest = new Guest("Pani", "Kajak", "pani@example.com", "696969699", "alergia na pierze");
        String output = guest.toString();
        assertTrue(output.contains("Pani Kajak"));
        assertTrue(output.contains("email: pani@example.com"));
        assertTrue(output.contains("tel: 696969699"));
        assertTrue(output.contains("info: alergia na pierze"));
    }
}
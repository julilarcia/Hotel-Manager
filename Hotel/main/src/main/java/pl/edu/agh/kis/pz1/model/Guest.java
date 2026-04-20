package pl.edu.agh.kis.pz1.model;

/**
 * Represents a hotel guest.
 *
 * <p>Holds basic contact details and optional notes. Instances are immutable from the
 * public API perspective (no setters provided) which simplifies usage in the hotel model
 * and tests.</p>
 */
public class Guest {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String notes;

    /**
     * Creates a guest with only a first name and last name.
     *
     * @param firstName guest first name
     * @param lastName  guest last name
     */
    public Guest(String firstName, String lastName) {
        this(firstName, lastName, null, null, null);
    }

    /**
     * Creates a guest with full optional contact information.
     *
     * @param firstName   guest first name
     * @param lastName    guest last name
     * @param email       optional email address (may be null)
     * @param phoneNumber optional phone number (may be null)
     * @param notes       optional notes or metadata (may be null)
     */
    public Guest(String firstName, String lastName, String email, String phoneNumber, String notes) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.notes = notes;
    }

    /**
     * Returns the guest's first name.
     *
     * @return first name (never null for objects created via constructors above)
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Returns the guest's last name.
     *
     * @return last name (never null for objects created via constructors above)
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Returns the guest's email address if provided.
     *
     * @return email or null when not set
     */
    public String getEmail() {
        return email;
    }

    /**
     * Returns the guest's phone number if provided.
     *
     * @return phone number or null when not set
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Returns additional notes associated with the guest.
     *
     * @return notes or null when not set
     */
    public String getNotes() {
        return notes;
    }

    /**
     * Returns a human-readable representation of the guest including any available
     * contact fields and notes.
     *
     * @return formatted guest string
     */
    @Override
    public String toString() {
        return firstName + " " + lastName +
                (email != null ? ", email: " + email : "") +
                (phoneNumber != null ? ", tel: " + phoneNumber : "") +
                (notes != null ? ", info: " + notes : "");
    }
}

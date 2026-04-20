package pl.edu.agh.kis.pz1.app;

import pl.edu.agh.kis.pz1.io.HotelDataLoader;
import pl.edu.agh.kis.pz1.command.CommandProcessor;
import pl.edu.agh.kis.pz1.io.HotelDataSaver;
import pl.edu.agh.kis.pz1.model.Hotel;
import pl.edu.agh.kis.pz1.service.HotelService;

import java.io.File;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * <p>This class loads hotel data (either from a saved CSV if present and accepted by the
 * user, or from the default CSV) and starts an interactive command loop (REPL).
 * It also prints a small banner and available commands on startup.</p>
 *
 * <p>Implementation notes:
 * - Uses a single {@link Scanner} for reading from {@code System.in}.
 * - Uses {@link HotelDataLoader} to load hotel data and {@link HotelDataSaver} (via commands)
 *   to persist state when requested by the user.</p>
 */
public class HotelManagerApp {
    private static final Logger logger = Logger.getLogger(HotelManagerApp.class.getName());

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BOLD = "\u001B[1m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_GREEN = "\u001B[32m";

    /**
     * Application entry point.
     *
     * <p>Start-up behaviour:
     * - checks for presence of {@code hotel_saved.csv} and asks the user whether to load it;
     * - falls back to {@code hotel.csv} if the saved file is absent or the user declines;
     * - initializes service and command processor, prints banner and commands, then runs REPL.</p>
     *
     * @param args command-line arguments (ignored)
     */
    public static void main(String[] args) {
        String savedPath = "hotel_saved.csv";
        String defaultPath = "hotel.csv";
        String sourcePath = defaultPath;

        try (Scanner scanner = new Scanner(System.in)) {
            sourcePath = chooseSourcePathIfExists(scanner, savedPath, defaultPath);
            Hotel hotel = HotelDataLoader.loadFromCsv(sourcePath, "Toto");
            HotelService service = new HotelService(hotel);
            CommandProcessor processor = new CommandProcessor(service);

            printBanner();
            printCommands();

            runRepl(scanner, processor);
        }
    }

    /**
     * If a saved CSV exists asks the user whether to load it; otherwise returns the default path.
     *
     * <p>Prompts accept {@code y} / {@code yes} (case-insensitive) as affirmative answer.
     * Any other input is treated as negative. In case of input read error the method returns the
     * {@code defaultPath}.</p>
     *
     * @param scanner     scanner to read user input from (should wrap System.in)
     * @param savedPath   path to saved CSV file
     * @param defaultPath fallback CSV path
     * @return selected path to load
     */
    private static String chooseSourcePathIfExists(Scanner scanner, String savedPath, String defaultPath) {
        File savedFile = new File(savedPath);
        if (!savedFile.exists() || !savedFile.isFile()) {
            return defaultPath;
        }

        System.out.print("found saved data (" + savedPath + "). load it? (yes/no): ");
        String answer = null;
        try {
            answer = scanner.nextLine();
        } catch (Exception e) {
            logger.warning("No input received, using default file.");
            return defaultPath;
        }

        if (answer == null) return defaultPath;
        String normalized = answer.trim().toLowerCase();
        return (normalized.equals("y") || normalized.equals("yes")) ? savedPath : defaultPath;
    }

    /**
     * Runs the interactive REPL: reads commands, dispatches them to {@link CommandProcessor},
     * and prints responses until the user issues the {@code exit} command or input closes.
     *
     * @param scanner   scanner to read user input
     * @param processor command processor that handles parsed commands
     */
    private static void runRepl(Scanner scanner, CommandProcessor processor) {
        while (true) {
            System.out.print(ANSI_GREEN + "> " + ANSI_RESET);
            String input;
            try {
                input = scanner.nextLine();
            } catch (Exception e) {
                logger.info("input closed, exiting.");
                break;
            }

            if (input == null) break;
            String trimmed = input.trim();
            if (trimmed.isEmpty()) continue;
            if ("exit".equalsIgnoreCase(trimmed)) {
                printlnUser("byebye~. u MUST come back!");
                break;
            }

            String output;
            try {
                output = processor.process(trimmed);
            } catch (Exception e) {
                logger.severe("error while processing command: " + e.getMessage());
                output = "an error occurred while processing the command.";
            }
            printlnUser(output);
        }
    }

    /**
     * Prints ASCII banner at application start.
     *
     * <p>Banner is centered according to a fixed width and uses ANSI color codes
     * defined in class constants.</p>
     */
    private static void printBanner() {
        String title = "Hotel Toto";
        String subtitle = "goodmorning folks, this is hotel system Toto.";
        int width = 64;
        String border = "=".repeat(width);

        System.out.println(ANSI_CYAN + ANSI_BOLD + border + ANSI_RESET);
        System.out.println(ANSI_CYAN + ANSI_BOLD + center(title, width) + ANSI_RESET);
        System.out.println(ANSI_CYAN + center(subtitle, width) + ANSI_RESET);
        System.out.println(ANSI_CYAN + ANSI_BOLD + border + ANSI_RESET);
        System.out.println();
    }

    /**
     * Prints available commands list to standard output.
     */
    private static void printCommands() {
        String[] commands = new String[] {
                "checkin <roomNumber> <firstName> <lastName> <days>",
                "checkout <roomNumber>",
                "view <roomNumber>",
                "list",
                "listAvailable",
                "prices",
                "save",
                "exit"
        };

        System.out.println(ANSI_BOLD + "commands:" + ANSI_RESET);
        for (String c : commands) {
            System.out.println("  " + c);
        }
        System.out.println();
    }

    /**
     * Prints a message to the user with bold styling if the message is non-empty.
     *
     * @param message message to print; if null or empty a blank line is printed
     */
    private static void printlnUser(String message) {
        if (message == null || message.isEmpty()) {
            System.out.println();
        } else {
            System.out.println(ANSI_BOLD + message + ANSI_RESET);
        }
    }

    /**
     * Centers a string within a field of specified width by prefixing it with spaces.
     *
     * @param s     string to center
     * @param width total field width
     * @return centered string (left-padded)
     */
    private static String center(String s, int width) {
        if (s == null) return "";
        if (s.length() >= width) return s;
        int left = (width - s.length()) / 2;
        return " ".repeat(left) + s;
    }
}

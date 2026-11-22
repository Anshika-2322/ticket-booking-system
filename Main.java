import java.util.*;

public class Main {
    public static void main(String[] args) {
        BookingSystem sys = new BookingSystem(10);
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== TICKET BOOKING SYSTEM =====");
            System.out.println("1. Book ticket");
            System.out.println("2. Cancel ticket");
            System.out.println("3. Undo last cancellation");
            System.out.println("4. Search by booking ID");
            System.out.println("5. Check a seat");
            System.out.println("6. View bookings (sorted by seat)");
            System.out.println("7. View waiting list");
            System.out.println("8. Seat map");
            System.out.println("0. Exit");
            System.out.print("Choice: ");

            if (!sc.hasNextLine()) return;
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1": {
                    System.out.print("Name: ");
                    String name = sc.nextLine().trim();
                    int seat = readInt(sc, "Seat number (1-10): ");
                    int pr = readInt(sc, "Priority (0=Normal, 1=Senior, 2=VIP): ");
                    if (pr < 0 || pr > 2) pr = 0;
                    System.out.println(sys.book(name, seat, pr));
                    break;
                }
                case "2":
                    System.out.println(sys.cancel(readInt(sc, "Booking ID: ")));
                    break;
                case "3":
                    System.out.println(sys.undoCancel());
                    break;
                case "4":
                    System.out.println(sys.search(readInt(sc, "Booking ID: ")));
                    break;
                case "5":
                    System.out.println(sys.searchSeat(readInt(sc, "Seat number: ")));
                    break;
                case "6": {
                    List<Booking> list = sys.sortedBookings();
                    if (list.isEmpty()) System.out.println("No bookings yet.");
                    for (Booking b : list) System.out.println(b);
                    break;
                }
                case "7": {
                    List<String> w = sys.waitingList();
                    if (w.isEmpty()) System.out.println("Waiting list is empty.");
                    for (String s : w) System.out.println(s);
                    break;
                }
                case "8":
                    System.out.println(sys.seatMap());
                    break;
                case "0":
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private static int readInt(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            if (!sc.hasNextLine()) return 0;
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }
}

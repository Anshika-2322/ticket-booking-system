import java.util.*;

/**
 * Core logic. Data structures used:
 *  - HashMap      : booking ID -> Booking (fast lookup)              [Unit III]
 *  - HashSet      : booked seat numbers (prevents double booking)    [Unit IV]
 *  - SeatBST      : bookings sorted by seat number                   [Unit IV]
 *  - PriorityQueue: waiting list, VIP/Senior served first, then FIFO [Unit V]
 *  - Stack(Deque) : cancelled bookings, used for undo                [Unit I]
 */
public class BookingSystem {

    /** A passenger waiting for a seat. */
    private static class Request implements Comparable<Request> {
        String name;
        int priority;
        long order; // arrival order, keeps FIFO among equal priority

        Request(String name, int priority, long order) {
            this.name = name;
            this.priority = priority;
            this.order = order;
        }

        public int compareTo(Request o) {
            if (priority != o.priority) return Integer.compare(o.priority, priority); // higher first
            return Long.compare(order, o.order);                                      // earlier first
        }
    }

    private final int totalSeats;
    private int nextId = 1;
    private long arrivalCounter = 0;

    private final Map<Integer, Booking> byId = new HashMap<>();
    private final Set<Integer> bookedSeats = new HashSet<>();
    private final SeatBST tree = new SeatBST();
    private final PriorityQueue<Request> waiting = new PriorityQueue<>();
    private final Deque<Booking> cancelStack = new ArrayDeque<>();

    public BookingSystem(int totalSeats) { this.totalSeats = totalSeats; }

    public int getTotalSeats() { return totalSeats; }

    public boolean isBooked(int seat) { return bookedSeats.contains(seat); }

    /** Book a seat; if it is taken, the passenger joins the waiting list. */
    public String book(String name, int seat, int priority) {
        if (seat < 1 || seat > totalSeats) return "Invalid seat. Choose 1-" + totalSeats + ".";
        if (bookedSeats.contains(seat)) {
            waiting.add(new Request(name, priority, arrivalCounter++));
            return "Seat " + seat + " is taken. " + name + " added to waiting list ("
                    + Booking.priorityName(priority) + ").";
        }
        Booking b = confirm(name, seat, priority, nextId++);
        return "Booked! " + b;
    }

    private Booking confirm(String name, int seat, int priority, int id) {
        Booking b = new Booking(id, name, seat, priority);
        byId.put(id, b);
        bookedSeats.add(seat);
        tree.insert(b);
        return b;
    }

    /** Cancel a booking; the freed seat goes to the top of the waiting list. */
    public String cancel(int id) {
        Booking b = byId.remove(id);
        if (b == null) return "No booking with ID " + id + ".";
        bookedSeats.remove(b.seat);
        tree.delete(b.seat);
        cancelStack.push(b);
        String msg = "Cancelled: " + b;
        if (!waiting.isEmpty()) {
            Request r = waiting.poll();
            Booking nb = confirm(r.name, b.seat, r.priority, nextId++);
            msg += "\nSeat given to waiting passenger -> " + nb;
        }
        return msg;
    }

    /** Undo the most recent cancellation, if its seat is still free. */
    public String undoCancel() {
        if (cancelStack.isEmpty()) return "Nothing to undo.";
        Booking b = cancelStack.pop();
        if (bookedSeats.contains(b.seat)) {
            return "Cannot undo: seat " + b.seat + " was given to a waiting passenger.";
        }
        confirm(b.name, b.seat, b.priority, b.id);
        return "Restored: " + b;
    }

    public String search(int id) {
        Booking b = byId.get(id);
        return b == null ? "No booking with ID " + id + "." : b.toString();
    }

    public String searchSeat(int seat) {
        Booking b = tree.search(seat);
        return b == null ? "Seat " + seat + " is free." : b.toString();
    }

    public List<Booking> sortedBookings() {
        List<Booking> list = new ArrayList<>();
        tree.inorder(list);
        return list;
    }

    public List<String> waitingList() {
        List<String> out = new ArrayList<>();
        PriorityQueue<Request> copy = new PriorityQueue<>(waiting);
        int pos = 1;
        while (!copy.isEmpty()) {
            Request r = copy.poll();
            out.add(pos++ + ". " + r.name + " (" + Booking.priorityName(r.priority) + ")");
        }
        return out;
    }

    public String seatMap() {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= totalSeats; i++) {
            sb.append(bookedSeats.contains(i) ? "[X]" : "[" + i + "]");
            sb.append(i % 5 == 0 ? "\n" : " ");
        }
        return sb.toString();
    }
}
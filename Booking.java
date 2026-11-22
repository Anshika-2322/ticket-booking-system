/** A confirmed booking. */
public class Booking {
    int id;
    String name;
    int seat;
    int priority; // 0 = normal, 1 = senior citizen, 2 = VIP

    public Booking(int id, String name, int seat, int priority) {
        this.id = id;
        this.name = name;
        this.seat = seat;
        this.priority = priority;
    }

    static String priorityName(int p) {
        return p == 2 ? "VIP" : p == 1 ? "Senior" : "Normal";
    }

    @Override
    public String toString() {
        return "ID " + id + " | " + name + " | Seat " + seat + " | " + priorityName(priority);
    }
}
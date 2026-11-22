import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

/**
 * Launcher: a small home window with two buttons.
 * Click a button to open that window; open them one by one, as you need.
 * Both windows share ONE BookingSystem, so a change in one shows up in the other.
 * Run this file (java App).
 */
public class App {
    static final BookingSystem sys = new BookingSystem(10);
    private static final List<Runnable> listeners = new ArrayList<>();

    private static CustomerWindow customer;
    private static AdminWindow admin;

    /** Windows register a refresh action here. */
    static void onChange(Runnable r) { listeners.add(r); }

    static void offChange(Runnable r) { listeners.remove(r); }

    /** Call after any change so every open window redraws. */
    static void changed() {
        for (Runnable r : new ArrayList<>(listeners)) r.run();
    }

    private static void openCustomer() {
        if (customer == null || !customer.isDisplayable()) {
            customer = new CustomerWindow();
            customer.setLocation(60, 200);
            customer.setVisible(true);
        } else {
            customer.toFront(); // already open: just bring it forward
        }
    }

    private static void openAdmin() {
        if (admin == null || !admin.isDisplayable()) {
            admin = new AdminWindow();
            admin.setLocation(480, 120);
            admin.setVisible(true);
        } else {
            admin.toFront();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame home = new JFrame("Ticket Booking System");
            home.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // closing home exits everything
            home.setLayout(new BorderLayout(10, 10));
            ((JComponent) home.getContentPane())
                    .setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

            JLabel title = new JLabel("Ticket Booking System", SwingConstants.CENTER);
            title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
            home.add(title, BorderLayout.NORTH);

            JPanel buttons = new JPanel(new GridLayout(2, 1, 10, 10));
            JButton c = new JButton("Open Customer Window");
            JButton a = new JButton("Open Admin Window");
            c.setPreferredSize(new Dimension(240, 45));
            a.setPreferredSize(new Dimension(240, 45));
            c.addActionListener(e -> openCustomer());
            a.addActionListener(e -> openAdmin());
            buttons.add(c);
            buttons.add(a);
            home.add(buttons, BorderLayout.CENTER);

            home.pack();
            home.setLocation(60, 40);
            home.setVisible(true);
        });
    }
}
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

/**
 * Full-Screen Dashboard Launcher:
 * - Opens maximized across the whole screen.
 * - Opens Customer Window or Admin Window one by one (hiding the dashboard while active,
 *   and returning to the full-screen dashboard when closed).
 * - Both windows share ONE BookingSystem instance.
 */
public class App {
    static final BookingSystem sys = new BookingSystem(10);
    private static final List<Runnable> listeners = new ArrayList<>();

    private static JFrame home;
    private static CustomerWindow customer;
    private static AdminWindow admin;

    static void onChange(Runnable r) { listeners.add(r); }

    static void offChange(Runnable r) { listeners.remove(r); }

    static void changed() {
        for (Runnable r : new ArrayList<>(listeners)) r.run();
    }

    static void makeFullScreen(JFrame frame) {
        GraphicsConfiguration gc = frame.getGraphicsConfiguration();
        Rectangle bounds = (gc != null)
                ? gc.getBounds()
                : new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        Insets insets = (gc != null)
                ? Toolkit.getDefaultToolkit().getScreenInsets(gc)
                : new Insets(0, 0, 0, 0);

        int width = Math.max(900, bounds.width - insets.left - insets.right);
        int height = Math.max(600, bounds.height - insets.top - insets.bottom);

        frame.setBounds(bounds.x + insets.left, bounds.y + insets.top, width, height);
        frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
    }

    static void showHome() {
        if (home != null) {
            home.setVisible(true);
            makeFullScreen(home);
            home.toFront();
        }
    }

    private static void openCustomer() {
        if (admin != null && admin.isDisplayable()) {
            admin.dispose();
        }
        if (home != null) {
            home.setVisible(false);
        }
        if (customer == null || !customer.isDisplayable()) {
            customer = new CustomerWindow();
            customer.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    showHome();
                }
            });
            customer.setVisible(true);
            makeFullScreen(customer);
        } else {
            customer.setVisible(true);
            makeFullScreen(customer);
            customer.toFront();
        }
    }

    private static void openAdmin() {
        if (customer != null && customer.isDisplayable()) {
            customer.dispose();
        }
        if (home != null) {
            home.setVisible(false);
        }
        if (admin == null || !admin.isDisplayable()) {
            admin = new AdminWindow();
            admin.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    showHome();
                }
            });
            admin.setVisible(true);
            makeFullScreen(admin);
        } else {
            admin.setVisible(true);
            makeFullScreen(admin);
            admin.toFront();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            home = new JFrame("Ticket Booking System - Dashboard");
            home.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            home.setLayout(new BorderLayout(24, 24));
            home.getContentPane().setBackground(new Color(241, 245, 249));
            ((JComponent) home.getContentPane())
                    .setBorder(BorderFactory.createEmptyBorder(36, 48, 36, 48));

            JPanel header = new JPanel();
            header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
            header.setBackground(new Color(23, 37, 84));
            header.setBorder(BorderFactory.createEmptyBorder(34, 36, 34, 36));

            JLabel title = new JLabel("Ticket Booking System");
            title.setFont(new Font("SansSerif", Font.BOLD, 40));
            title.setForeground(Color.WHITE);
            title.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel subtitle = new JLabel("Interactive Seat Reservation & Management Dashboard");
            subtitle.setFont(new Font("SansSerif", Font.PLAIN, 18));
            subtitle.setForeground(new Color(191, 219, 254));
            subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

            header.add(title);
            header.add(Box.createVerticalStrut(10));
            header.add(subtitle);
            home.add(header, BorderLayout.NORTH);

            JPanel cardsPanel = new JPanel(new GridLayout(1, 2, 36, 0));
            cardsPanel.setOpaque(false);

            JPanel custCard = createDashboardCard(
                    "Customer Window",
                    "Open the seat booking screen to check live seat availability, pick your seat, select priority (Normal, Senior, VIP), or join the waiting list.",
                    "Open Customer Window",
                    new Color(22, 163, 74),
                    e -> openCustomer()
            );

            JPanel adminCard = createDashboardCard(
                    "Admin Window",
                    "Open the management console to inspect bookings sorted by seat, monitor the waiting list, cancel tickets, and undo actions.",
                    "Open Admin Window",
                    new Color(37, 99, 235),
                    e -> openAdmin()
            );

            cardsPanel.add(custCard);
            cardsPanel.add(adminCard);
            home.add(cardsPanel, BorderLayout.CENTER);

            JLabel footer = new JLabel(
                    "Each screen opens one at a time and returns to this dashboard when closed.",
                    SwingConstants.CENTER
            );
            footer.setFont(new Font("SansSerif", Font.PLAIN, 15));
            footer.setForeground(new Color(71, 85, 105));
            home.add(footer, BorderLayout.SOUTH);

            home.setVisible(true);
            makeFullScreen(home);
        });
    }

    private static JPanel createDashboardCard(
            String heading,
            String description,
            String buttonText,
            Color accentColor,
            java.awt.event.ActionListener action
    ) {
        JPanel card = new JPanel(new BorderLayout(18, 18));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 2, true),
                BorderFactory.createEmptyBorder(36, 36, 36, 36)
        ));

        JLabel hLabel = new JLabel(heading);
        hLabel.setFont(new Font("SansSerif", Font.BOLD, 30));
        hLabel.setForeground(new Color(15, 23, 42));
        card.add(hLabel, BorderLayout.NORTH);

        JTextArea descArea = new JTextArea(description);
        descArea.setFont(new Font("SansSerif", Font.PLAIN, 18));
        descArea.setForeground(new Color(51, 65, 85));
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setEditable(false);
        descArea.setOpaque(false);
        card.add(descArea, BorderLayout.CENTER);

        JButton btn = new JButton(buttonText);
        btn.setFont(new Font("SansSerif", Font.BOLD, 19));
        btn.setBackground(accentColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(280, 64));
        btn.addActionListener(action);
        card.add(btn, BorderLayout.SOUTH);

        return card;
    }
}
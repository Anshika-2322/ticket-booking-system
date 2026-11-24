import java.awt.*;
import javax.swing.*;

/**
 * Customer Window: large form layout for booking a seat and checking a booking ID.
 */
public class CustomerWindow extends JFrame {

    private final BookingSystem sys = App.sys;

    private final JTextField nameField = new JTextField();
    private final JSpinner seatSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
    private final JComboBox<String> priorityBox =
            new JComboBox<>(new String[]{"Normal", "Senior", "VIP"});
    private final JTextField bookingIdField = new JTextField();
    private final JTextArea messagesArea = new JTextArea(6, 50);
    private final JLabel statusLabel = new JLabel();

    public CustomerWindow() {
        super("Customer Window - Book a Ticket");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(18, 18));
        getContentPane().setBackground(new Color(241, 245, 249));
        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));

        add(buildTopBar(), BorderLayout.NORTH);
        add(buildBookingCard(), BorderLayout.CENTER);
        add(buildMessagesPanel(), BorderLayout.SOUTH);

        final Runnable refresher = this::refresh;
        App.onChange(refresher);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                App.offChange(refresher);
            }
        });
        refresh();
    }

    private JPanel buildTopBar() {
        JPanel top = new JPanel(new BorderLayout(12, 12));
        top.setBackground(new Color(22, 101, 52));
        top.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));

        JButton backBtn = new JButton("← Back to Dashboard");
        backBtn.setFont(new Font("SansSerif", Font.BOLD, 15));
        backBtn.setFocusPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> dispose());
        top.add(backBtn, BorderLayout.WEST);

        JLabel title = new JLabel("Customer Ticket Booking", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        top.add(title, BorderLayout.CENTER);

        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        statusLabel.setForeground(new Color(220, 252, 231));
        statusLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        top.add(statusLabel, BorderLayout.EAST);

        return top;
    }

    private JPanel buildBookingCard() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setOpaque(false);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(203, 213, 225), 2, true),
                        "Book a ticket",
                        javax.swing.border.TitledBorder.LEFT,
                        javax.swing.border.TitledBorder.TOP,
                        new Font("SansSerif", Font.BOLD, 18),
                        new Color(15, 23, 42)
                ),
                BorderFactory.createEmptyBorder(28, 36, 28, 36)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 14, 12, 14);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("SansSerif", Font.BOLD, 18);
        Font inputFont = new Font("SansSerif", Font.PLAIN, 18);
        Font buttonFont = new Font("SansSerif", Font.BOLD, 18);

        Dimension fieldSize = new Dimension(320, 44);
        Dimension buttonSize = new Dimension(320, 52);

        nameField.setFont(inputFont);
        nameField.setPreferredSize(fieldSize);

        seatSpinner.setFont(inputFont);
        seatSpinner.setPreferredSize(fieldSize);
        JComponent editor = seatSpinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            ((JSpinner.DefaultEditor) editor).getTextField().setFont(inputFont);
        }

        priorityBox.setFont(inputFont);
        priorityBox.setPreferredSize(fieldSize);

        bookingIdField.setFont(inputFont);
        bookingIdField.setPreferredSize(fieldSize);

        // Row 0: Name
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        JLabel nameLbl = new JLabel("Name");
        nameLbl.setFont(labelFont);
        card.add(nameLbl, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        card.add(nameField, gbc);

        // Row 1: Seat
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        JLabel seatLbl = new JLabel("Seat");
        seatLbl.setFont(labelFont);
        card.add(seatLbl, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        card.add(seatSpinner, gbc);

        // Row 2: Priority
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        JLabel prioLbl = new JLabel("Priority");
        prioLbl.setFont(labelFont);
        card.add(prioLbl, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        card.add(priorityBox, gbc);

        // Row 3: Book ticket button
        JButton bookBtn = new JButton("Book ticket");
        bookBtn.setFont(buttonFont);
        bookBtn.setPreferredSize(buttonSize);
        bookBtn.setBackground(new Color(22, 163, 74));
        bookBtn.setForeground(Color.WHITE);
        bookBtn.setFocusPainted(false);
        bookBtn.setOpaque(true);
        bookBtn.setBorderPainted(false);
        bookBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bookBtn.addActionListener(e -> handleBookTicket());

        gbc.gridx = 1; gbc.gridy = 3;
        card.add(bookBtn, gbc);

        // Row 4: Booking ID
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
        JLabel idLbl = new JLabel("Booking ID");
        idLbl.setFont(labelFont);
        card.add(idLbl, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        card.add(bookingIdField, gbc);

        // Row 5: Check my booking button
        JButton checkBtn = new JButton("Check my booking");
        checkBtn.setFont(buttonFont);
        checkBtn.setPreferredSize(buttonSize);
        checkBtn.setBackground(new Color(37, 99, 235));
        checkBtn.setForeground(Color.WHITE);
        checkBtn.setFocusPainted(false);
        checkBtn.setOpaque(true);
        checkBtn.setBorderPainted(false);
        checkBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        checkBtn.addActionListener(e -> handleCheckBooking());

        gbc.gridx = 1; gbc.gridy = 5;
        card.add(checkBtn, gbc);

        GridBagConstraints outerGbc = new GridBagConstraints();
        outerGbc.gridx = 0;
        outerGbc.gridy = 0;
        outerGbc.weightx = 1.0;
        outerGbc.weighty = 1.0;
        outerGbc.fill = GridBagConstraints.BOTH;
        outer.add(card, outerGbc);

        return outer;
    }

    private JScrollPane buildMessagesPanel() {
        messagesArea.setEditable(false);
        messagesArea.setFont(new Font("Monospaced", Font.PLAIN, 15));
        JScrollPane sp = new JScrollPane(messagesArea);
        sp.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1, true),
                "Messages",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 15),
                new Color(15, 23, 42)
        ));
        return sp;
    }

    private void handleBookTicket() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            say("Please enter your name before booking.");
            return;
        }
        int seat = (Integer) seatSpinner.getValue();
        int priority = priorityBox.getSelectedIndex(); // 0 = Normal, 1 = Senior, 2 = VIP
        say(sys.book(name, seat, priority));
        nameField.setText("");
        App.changed();
    }

    private void handleCheckBooking() {
        String raw = bookingIdField.getText().trim();
        if (raw.isEmpty()) {
            say("Please enter a Booking ID to check.");
            return;
        }
        try {
            int id = Integer.parseInt(raw);
            Booking found = null;
            for (Booking b : sys.sortedBookings()) {
                if (b.id == id) {
                    found = b;
                    break;
                }
            }
            if (found != null) {
                say("Booking found -> " + found.toString());
            } else {
                say("No active booking found with ID " + id + ".");
            }
        } catch (NumberFormatException ex) {
            say("Please enter a valid numeric Booking ID.");
        }
    }

    private void say(String msg) {
        messagesArea.append(msg + "\n");
        messagesArea.setCaretPosition(messagesArea.getDocument().getLength());
    }

    private void refresh() {
        int bookedCount = sys.sortedBookings().size();
        int waitCount = sys.waitingList().size();
        statusLabel.setText("Booked: " + bookedCount + "   |   Waiting List: " + waitCount);
    }
}
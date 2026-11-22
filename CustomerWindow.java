import java.awt.*;
import javax.swing.*;

/** Customer view: see free seats, book a ticket, look up a booking. */
public class CustomerWindow extends JFrame {

    private static final Color FREE = new Color(76, 175, 80);
    private static final Color TAKEN = new Color(229, 57, 53);

    private final BookingSystem sys = App.sys;
    private final JButton[] seatButtons;
    private final JTextField nameField = new JTextField(12);
    private final JSpinner seatSpinner;
    private final JComboBox<String> priorityBox =
            new JComboBox<>(new String[]{"Normal", "Senior", "VIP"});
    private final JTextField idField = new JTextField(6);
    private final JTextArea log = new JTextArea(6, 30);

    public CustomerWindow() {
        super("Customer - Book Tickets");
        int seats = sys.getTotalSeats();
        seatSpinner = new JSpinner(new SpinnerNumberModel(1, 1, seats, 1));
        seatButtons = new JButton[seats + 1];

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(buildSeatMap(seats), BorderLayout.NORTH);
        add(buildForm(), BorderLayout.CENTER);
        add(buildLog(), BorderLayout.SOUTH);

        final Runnable refresher = this::refresh;
        App.onChange(refresher);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosed(java.awt.event.WindowEvent e) { App.offChange(refresher); }
        });
        refresh();
        pack();
        setResizable(false);
    }

    private JPanel buildSeatMap(int seats) {
        JPanel grid = new JPanel(new GridLayout(2, (seats + 1) / 2, 8, 8));
        grid.setBorder(BorderFactory.createTitledBorder("Seat map (green = free, red = booked)"));
        for (int i = 1; i <= seats; i++) {
            final int seat = i;
            JButton b = new JButton(String.valueOf(i));
            b.setOpaque(true);
            b.setContentAreaFilled(true);
            b.setBorderPainted(false);
            b.setForeground(Color.WHITE);
            b.setFont(b.getFont().deriveFont(Font.BOLD, 16f));
            b.setPreferredSize(new Dimension(60, 50));
            b.addActionListener(e -> {
                if (sys.isBooked(seat)) say(sys.searchSeat(seat));
                else seatSpinner.setValue(seat);
            });
            seatButtons[i] = b;
            grid.add(b);
        }
        return grid;
    }

    private JPanel buildForm() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createTitledBorder("Book a ticket"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridy = 0; c.gridx = 0; p.add(new JLabel("Name"), c);
        c.gridx = 1; p.add(nameField, c);
        c.gridy = 1; c.gridx = 0; p.add(new JLabel("Seat"), c);
        c.gridx = 1; p.add(seatSpinner, c);
        c.gridy = 2; c.gridx = 0; p.add(new JLabel("Priority"), c);
        c.gridx = 1; p.add(priorityBox, c);

        JButton book = new JButton("Book ticket");
        book.addActionListener(e -> onBook());
        c.gridy = 3; c.gridx = 0; c.gridwidth = 2; p.add(book, c);

        c.gridy = 4; c.gridwidth = 1; c.gridx = 0; p.add(new JLabel("Booking ID"), c);
        c.gridx = 1; p.add(idField, c);

        JButton search = new JButton("Check my booking");
        search.addActionListener(e -> onSearch());
        c.gridy = 5; c.gridx = 0; c.gridwidth = 2; p.add(search, c);
        return p;
    }

    private JScrollPane buildLog() {
        log.setEditable(false);
        JScrollPane sp = new JScrollPane(log);
        sp.setBorder(BorderFactory.createTitledBorder("Messages"));
        return sp;
    }

    private void onBook() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            say("Please enter your name.");
            return;
        }
        say(sys.book(name, (Integer) seatSpinner.getValue(), priorityBox.getSelectedIndex()));
        nameField.setText("");
        App.changed(); // refresh both windows
    }

    private void onSearch() {
        try {
            say(sys.search(Integer.parseInt(idField.getText().trim())));
        } catch (NumberFormatException ex) {
            say("Please enter a valid booking ID (a number).");
        }
    }

    private void say(String msg) {
        log.append(msg + "\n");
        log.setCaretPosition(log.getDocument().getLength());
    }

    private void refresh() {
        for (int i = 1; i < seatButtons.length; i++) {
            seatButtons[i].setBackground(sys.isBooked(i) ? TAKEN : FREE);
        }
    }
}
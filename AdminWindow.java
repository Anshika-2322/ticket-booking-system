import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/** Admin view: all bookings, waiting list, cancel and undo. */
public class AdminWindow extends JFrame {

    private final BookingSystem sys = App.sys;
    private final JTextField idField = new JTextField(12);
    private final DefaultTableModel tableModel =
            new DefaultTableModel(new String[]{"Seat", "Booking ID", "Name", "Priority"}, 0) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };
    private final DefaultListModel<String> waitModel = new DefaultListModel<>();
    private final JTextArea log = new JTextArea(7, 40);
    private final JLabel statsLabel = new JLabel();

    public AdminWindow() {
        super("Admin Window - Manage Bookings");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(16, 16));
        getContentPane().setBackground(new Color(241, 245, 249));
        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        add(buildTopBar(), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("SansSerif", Font.BOLD, 16));

        JTable bookingTable = new JTable(tableModel);
        bookingTable.setFont(new Font("SansSerif", Font.PLAIN, 16));
        bookingTable.setRowHeight(34);
        bookingTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 16));
        bookingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookingTable.getSelectionModel().addListSelectionListener(e -> {
            int row = bookingTable.getSelectedRow();
            if (!e.getValueIsAdjusting() && row >= 0) {
                idField.setText(String.valueOf(tableModel.getValueAt(row, 1)));
            }
        });

        JList<String> waitList = new JList<>(waitModel);
        waitList.setFont(new Font("SansSerif", Font.PLAIN, 16));
        waitList.setFixedCellHeight(32);

        tabs.addTab("Bookings (Sorted by Seat)", new JScrollPane(bookingTable));
        tabs.addTab("Waiting List", new JScrollPane(waitList));
        add(tabs, BorderLayout.CENTER);

        log.setEditable(false);
        log.setFont(new Font("Monospaced", Font.PLAIN, 15));
        JScrollPane sp = new JScrollPane(log);
        sp.setBorder(BorderFactory.createTitledBorder("Messages"));
        add(sp, BorderLayout.SOUTH);

        final Runnable refresher = this::refresh;
        App.onChange(refresher);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosed(java.awt.event.WindowEvent e) { App.offChange(refresher); }
        });
        refresh();
    }

    private JPanel buildTopBar() {
        JPanel wrapper = new JPanel(new BorderLayout(12, 12));
        wrapper.setOpaque(false);

        JPanel navBar = new JPanel(new BorderLayout(12, 12));
        navBar.setBackground(new Color(23, 37, 84));
        navBar.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));

        JButton backBtn = new JButton("← Back to Dashboard");
        backBtn.setFont(new Font("SansSerif", Font.BOLD, 15));
        backBtn.setFocusPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> dispose());
        navBar.add(backBtn, BorderLayout.WEST);

        statsLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        statsLabel.setForeground(Color.WHITE);
        statsLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        navBar.add(statsLabel, BorderLayout.EAST);

        wrapper.add(navBar, BorderLayout.NORTH);

        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 12));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createTitledBorder("Admin actions"));

        JLabel idLbl = new JLabel("Booking ID");
        idLbl.setFont(new Font("SansSerif", Font.BOLD, 16));
        idField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        p.add(idLbl);
        p.add(idField);

        JButton cancel = new JButton("Cancel ticket");
        cancel.setFont(new Font("SansSerif", Font.BOLD, 16));
        cancel.setBackground(new Color(220, 38, 38));
        cancel.setForeground(Color.WHITE);
        cancel.setFocusPainted(false);
        cancel.setOpaque(true);
        cancel.setBorderPainted(false);
        cancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancel.addActionListener(e -> {
            try {
                say(sys.cancel(Integer.parseInt(idField.getText().trim())));
                idField.setText("");
                App.changed();
            } catch (NumberFormatException ex) {
                say("Please enter a valid booking ID (a number).");
            }
        });
        p.add(cancel);

        JButton undo = new JButton("Undo last cancellation");
        undo.setFont(new Font("SansSerif", Font.BOLD, 16));
        undo.setBackground(new Color(37, 99, 235));
        undo.setForeground(Color.WHITE);
        undo.setFocusPainted(false);
        undo.setOpaque(true);
        undo.setBorderPainted(false);
        undo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        undo.addActionListener(e -> {
            say(sys.undoCancel());
            App.changed();
        });
        p.add(undo);

        wrapper.add(p, BorderLayout.CENTER);
        return wrapper;
    }

    private void say(String msg) {
        log.append(msg + "\n");
        log.setCaretPosition(log.getDocument().getLength());
    }

    private void refresh() {
        tableModel.setRowCount(0);
        int count = 0;
        for (Booking b : sys.sortedBookings()) {
            tableModel.addRow(new Object[]{b.seat, b.id, b.name, Booking.priorityName(b.priority)});
            count++;
        }
        waitModel.clear();
        int wCount = 0;
        for (String s : sys.waitingList()) {
            waitModel.addElement(s);
            wCount++;
        }
        statsLabel.setText("Active Bookings: " + count + "   |   Waiting List: " + wCount);
    }
}
import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/** Admin view: all bookings (BST), waiting list (heap), cancel and undo (stack). */
public class AdminWindow extends JFrame {

    private final BookingSystem sys = App.sys;
    private final JTextField idField = new JTextField(6);
    private final DefaultTableModel tableModel =
            new DefaultTableModel(new String[]{"Seat", "Booking ID", "Name", "Priority"}, 0) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };
    private final DefaultListModel<String> waitModel = new DefaultListModel<>();
    private final JTextArea log = new JTextArea(6, 30);

    public AdminWindow() {
        super("Admin - Manage Bookings");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(buildControls(), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Bookings (sorted by seat)", new JScrollPane(new JTable(tableModel)));
        tabs.addTab("Waiting list", new JScrollPane(new JList<>(waitModel)));
        add(tabs, BorderLayout.CENTER);

        log.setEditable(false);
        JScrollPane sp = new JScrollPane(log);
        sp.setBorder(BorderFactory.createTitledBorder("Messages"));
        add(sp, BorderLayout.SOUTH);

        final Runnable refresher = this::refresh;
        App.onChange(refresher);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosed(java.awt.event.WindowEvent e) { App.offChange(refresher); }
        });
        refresh();
        setPreferredSize(new Dimension(520, 520));
        pack();
    }

    private JPanel buildControls() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        p.setBorder(BorderFactory.createTitledBorder("Admin actions"));
        p.add(new JLabel("Booking ID"));
        p.add(idField);

        JButton cancel = new JButton("Cancel ticket");
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
        undo.addActionListener(e -> {
            say(sys.undoCancel());
            App.changed();
        });
        p.add(undo);
        return p;
    }

    private void say(String msg) {
        log.append(msg + "\n");
        log.setCaretPosition(log.getDocument().getLength());
    }

    private void refresh() {
        tableModel.setRowCount(0);
        for (Booking b : sys.sortedBookings()) { // inorder traversal of the BST
            tableModel.addRow(new Object[]{b.seat, b.id, b.name, Booking.priorityName(b.priority)});
        }
        waitModel.clear();
        for (String s : sys.waitingList()) waitModel.addElement(s);
    }
}
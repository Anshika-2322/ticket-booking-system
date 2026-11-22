import java.util.List;

/** Our own Binary Search Tree, keyed by seat number (Unit IV). */
public class SeatBST {
    private static class Node {
        Booking data;
        Node left, right;
        Node(Booking b) { data = b; }
    }

    private Node root;

    public void insert(Booking b) { root = insert(root, b); }

    private Node insert(Node n, Booking b) {
        if (n == null) return new Node(b);
        if (b.seat < n.data.seat) n.left = insert(n.left, b);
        else if (b.seat > n.data.seat) n.right = insert(n.right, b);
        return n;
    }

    public Booking search(int seat) {
        Node n = root;
        while (n != null) {
            if (seat == n.data.seat) return n.data;
            n = seat < n.data.seat ? n.left : n.right;
        }
        return null;
    }

    public void delete(int seat) { root = delete(root, seat); }

    private Node delete(Node n, int seat) {
        if (n == null) return null;
        if (seat < n.data.seat) n.left = delete(n.left, seat);
        else if (seat > n.data.seat) n.right = delete(n.right, seat);
        else {
            if (n.left == null) return n.right;
            if (n.right == null) return n.left;
            Node succ = n.right;              // inorder successor
            while (succ.left != null) succ = succ.left;
            n.data = succ.data;
            n.right = delete(n.right, succ.data.seat);
        }
        return n;
    }

    /** Inorder traversal gives bookings sorted by seat number. */
    public void inorder(List<Booking> out) { inorder(root, out); }

    private void inorder(Node n, List<Booking> out) {
        if (n == null) return;
        inorder(n.left, out);
        out.add(n.data);
        inorder(n.right, out);
    }
}
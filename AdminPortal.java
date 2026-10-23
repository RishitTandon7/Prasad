import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

/** Admin portal: add, view and delete temples stored in MySQL via JDBC. */
public class AdminPortal extends JFrame {
    private final JTextField tfName = new JTextField();
    private final JTextField tfLocation = new JTextField();
    private final JTextField tfDays = new JTextField();
    private final DefaultTableModel model =
            new DefaultTableModel(new String[] {"ID", "Temple", "Location", "Food days"}, 0) {
                @Override
                public boolean isCellEditable(int row, int col) {
                    return false;
                }
            };
    private final JTable table = new JTable(model);

    public AdminPortal() {
        super("Prasad - Admin Portal");
        Font font = new Font("SansSerif", Font.PLAIN, 15);

        JPanel form = new JPanel(new GridLayout(3, 2, 6, 6));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        form.add(new JLabel("Temple name"));
        form.add(tfName);
        form.add(new JLabel("Google Maps link"));
        form.add(tfLocation);
        form.add(new JLabel("Food days (e.g. Mon, Thu)"));
        form.add(tfDays);
        for (var c : form.getComponents()) c.setFont(font);

        JButton btnAdd = new JButton("Add temple");
        JButton btnDelete = new JButton("Delete selected");
        JButton btnClear = new JButton("Clear");
        btnAdd.addActionListener(e -> addTemple());
        btnDelete.addActionListener(e -> deleteSelected());
        btnClear.addActionListener(e -> clearForm());

        JPanel buttons = new JPanel(new GridLayout(1, 3, 6, 6));
        buttons.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        buttons.add(btnAdd);
        buttons.add(btnDelete);
        buttons.add(btnClear);

        JPanel top = new JPanel(new BorderLayout());
        top.add(form, BorderLayout.CENTER);
        top.add(buttons, BorderLayout.SOUTH);

        setLayout(new BorderLayout());
        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        setSize(760, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        loadTemples();
    }

    private void addTemple() {
        String name = tfName.getText().trim();
        String location = tfLocation.getText().trim();
        String days = tfDays.getText().trim();
        if (name.isEmpty() || location.isEmpty() || days.isEmpty()) {
            showError("Please fill in all three fields.");
            return;
        }
        String sql = "INSERT INTO temples (name, location_url, food_days) VALUES (?, ?, ?)";
        try (Connection conn = Db.connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, location);
            ps.setString(3, days);
            ps.executeUpdate();
            clearForm();
            loadTemples();
        } catch (SQLException ex) {
            showError("Could not save temple: " + ex.getMessage());
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            showError("Select a temple in the table first.");
            return;
        }
        int id = (Integer) model.getValueAt(row, 0);
        try (Connection conn = Db.connect();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM temples WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
            loadTemples();
        } catch (SQLException ex) {
            showError("Could not delete temple: " + ex.getMessage());
        }
    }

    private void loadTemples() {
        model.setRowCount(0);
        String sql = "SELECT id, name, location_url, food_days FROM temples ORDER BY name";
        try (Connection conn = Db.connect(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                model.addRow(new Object[] {
                    rs.getInt("id"), rs.getString("name"), rs.getString("location_url"), rs.getString("food_days")
                });
            }
        } catch (SQLException ex) {
            showError("Could not load temples: " + ex.getMessage());
        }
    }

    private void clearForm() {
        tfName.setText("");
        tfLocation.setText("");
        tfDays.setText("");
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Prasad", JOptionPane.WARNING_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminPortal().setVisible(true));
    }
}

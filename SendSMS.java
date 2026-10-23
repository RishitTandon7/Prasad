import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/** Reads temples and subscribers from MySQL and sends each subscriber the food schedule. */
public class SendSMS {
    public static void main(String[] args) throws SQLException {
        String sid = System.getenv("TWILIO_ACCOUNT_SID");
        String token = System.getenv("TWILIO_AUTH_TOKEN");
        String from = System.getenv("TWILIO_PHONE_NUMBER");
        if (sid == null || token == null || from == null) {
            System.err.println("Set TWILIO_ACCOUNT_SID, TWILIO_AUTH_TOKEN and TWILIO_PHONE_NUMBER first.");
            return;
        }
        Twilio.init(sid, token);

        try (Connection conn = Db.connect(); Statement st = conn.createStatement()) {
            String body = buildMessage(st);
            try (ResultSet rs = st.executeQuery("SELECT phone, channel FROM subscribers")) {
                int sent = 0;
                while (rs.next()) {
                    String to = rs.getString("phone");
                    boolean whatsapp = "WHATSAPP".equals(rs.getString("channel"));
                    Message.creator(
                            new PhoneNumber(whatsapp ? "whatsapp:" + to : to),
                            new PhoneNumber(whatsapp ? "whatsapp:" + from : from),
                            body).create();
                    sent++;
                }
                System.out.println("Sent temple schedule to " + sent + " subscribers.");
            }
        }
    }

    private static String buildMessage(Statement st) throws SQLException {
        StringBuilder sb = new StringBuilder("Temples serving food:\n\n");
        try (ResultSet rs = st.executeQuery("SELECT name, location_url, food_days FROM temples ORDER BY name")) {
            while (rs.next()) {
                sb.append(rs.getString("name")).append('\n')
                  .append("Location: ").append(rs.getString("location_url")).append('\n')
                  .append("Food served: ").append(rs.getString("food_days")).append("\n\n");
            }
        }
        return sb.toString();
    }
}

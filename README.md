# Prasad – Temple Food Notification Platform

Notifies people about temples serving free food, by SMS and WhatsApp. Admins manage temples through a desktop portal; the data lives in MySQL and is read over JDBC.

## Components
| File | What it does |
| --- | --- |
| `AdminPortal.java` | Swing admin portal to add, view and delete temples (JDBC + PreparedStatements) |
| `SendSMS.java` | Reads temples and subscribers from MySQL and sends the schedule via Twilio (SMS or WhatsApp) |
| `ChatBot.java` | Spark Java webhook (`POST /whatsapp`) that replies to incoming WhatsApp messages |
| `Db.java` | Shared JDBC connection helper |
| `schema.sql` | MySQL schema: `temples`, `subscribers` |

## Tech
Java, Spark Java, JDBC, MySQL, Twilio API, Swing

## Setup
1. Create the database:
   ```sh
   mysql -u root -p < schema.sql
   ```
2. Set environment variables (never commit these):
   ```sh
   DB_URL=jdbc:mysql://localhost:3306/prasad_db
   DB_USER=root
   DB_PASS=your_password
   TWILIO_ACCOUNT_SID=your_sid
   TWILIO_AUTH_TOKEN=your_token
   TWILIO_PHONE_NUMBER=+1XXXXXXXXXX
   ```
3. Add the MySQL Connector/J and Twilio Java SDK jars to the classpath.
4. Run the admin portal: `java AdminPortal`
5. Send notifications: `java SendSMS`
6. Start the WhatsApp webhook: `java ChatBot` (expose port 8080 with ngrok and set it as the Twilio webhook URL)

## License
MIT

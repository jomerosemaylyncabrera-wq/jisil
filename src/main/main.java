 package main;

import config.config;
import java.util.Scanner;
import java.util.List;
import java.util.Map;

public class main {

    public static void viewBuses(config db) {
        String qry = "SELECT * FROM bus";
        String[] headers = {"Bus ID", "Bus Number", "Bus Type", "Total Seats", "Price"};
        String[] cols = {"bus_id", "bus_number", "bus_type", "total_seats", "price"};
        db.viewRecords(qry, headers, cols);
    }

 
    public static void viewBookings(config db) {
        String qry = "SELECT * FROM booking";
        String[] headers = {"Booking ID", "Bus ID", "User ID", "Journey Date", "Seat Number", "Fare", "Status", "Booking Time"};
        String[] cols = {"booking_id", "bus_id", "u_id", "journey_date", "seat_number", "fare", "booking_status", "booking_time"};
        db.viewRecords(qry, headers, cols);
    }
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        config db = new config();
        db.connectDB();

        int choice;
        char cont;

        do {
            System.out.println("\n===== BUS TICKETING SYSTEM =====");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Enter choice: ");
            choice = scan.nextInt();

            switch (choice) {
                case 1:
                    System.out.print("Enter email: ");
                    String em = scan.next();
                    System.out.print("Enter password: ");
                    String pas = scan.next();

                    String qry = "SELECT * FROM user WHERE u_email = ? AND password = ?";
                    List<Map<String, Object>> result = db.fetchRecords(qry, em, pas);

                    if (result.isEmpty()) {
                        System.out.println("❌ INVALID CREDENTIALS");
                    } else {
                        Map<String, Object> user = result.get(0);
                        String type = user.get("u_type").toString();

                        System.out.println("✅ LOGIN SUCCESS!");
                        if (type.equalsIgnoreCase("Admin")) {
                            adminMenu(scan, db);
                        } else {
                            passengerMenu(scan, db, user);
                        }
                    }
                    break;

                case 2:
                    System.out.print("Enter full name: ");
                    scan.nextLine();
                    String name = scan.nextLine();

                    System.out.print("Enter email: ");
                    String email = scan.next();

                    while (true) {
                        String checkQry = "SELECT * FROM user WHERE u_email = ?";
                        List<Map<String, Object>> checkResult = db.fetchRecords(checkQry, email);
                        if (checkResult.isEmpty()) break;
                        System.out.print("Email already exists, enter another: ");
                        email = scan.next();
                    }

                    System.out.print("Enter phone number: ");
                    String phone = scan.next();

                    System.out.print("Enter password: ");
                    String pass = scan.next();

                    System.out.print("Register as (1) Admin or (2) Passenger: ");
                    int typeChoice = scan.nextInt();
                    String type = (typeChoice == 1) ? "Admin" : "Passenger";

                    String sql = "INSERT INTO user(u_name, u_email, u_phone_number, password, u_type) VALUES (?, ?, ?, ?, ?)";
                    db.addRecord(sql, name, email, phone, pass, type);

                    System.out.println("✅ Registration successful! You can now log in.");
                    break;

                case 3:
                    System.out.println("Exiting system...");
                    System.exit(0);
                    break;

                default:
                    System.out.println("Invalid choice.");
            }

            System.out.print("Do you want to continue? (Y/N): ");
            cont = scan.next().charAt(0);

        } while (cont == 'Y' || cont == 'y');

        System.out.println("Thank you! Program ended.");
    }
    public static void adminMenu(Scanner scan, config db) {
        int choice;
        do {
            System.out.println("\n===== ADMIN DASHBOARD =====");
            System.out.println("1. Manage Buses (Add/Update/Delete/View)");
            System.out.println("2. View All Bookings");
            System.out.println("3. Logout");
            System.out.print("Enter choice: ");
            choice = scan.nextInt();

            switch (choice) {
                case 1:
                    System.out.println("\n-- Manage Buses --");
                    System.out.println("1. Add Bus");
                    System.out.println("2. Update Bus");
                    System.out.println("3. Delete Bus");
                    System.out.println("4. View Buses");
                    int sub = scan.nextInt();

                    if (sub == 1) {
                        scan.nextLine();
                        System.out.print("Enter Bus Number: ");
                        String number = scan.nextLine();
                        System.out.print("Enter Bus Type: ");
                        String type = scan.nextLine();
                        System.out.print("Enter Total Seats: ");
                        int seats = scan.nextInt();
                        System.out.print("Enter Price per Seat: ");
                        double price = scan.nextDouble();

                        String sql = "INSERT INTO bus(bus_number, bus_type, total_seats, price) VALUES (?, ?, ?, ?)";
                        db.addRecord(sql, number, type, seats, price);
                        System.out.println("✅ Bus added with price!");
                    } else if (sub == 2) {
                        viewBuses(db);
                        System.out.print("Enter Bus ID to Update: ");
                        int id = scan.nextInt();
                        scan.nextLine();
                        System.out.print("Enter new Bus Number: ");
                        String number = scan.nextLine();
                        System.out.print("Enter new Bus Type: ");
                        String type = scan.nextLine();
                        System.out.print("Enter new Total Seats: ");
                        int seats = scan.nextInt();
                        System.out.print("Enter new Price per Seat: ");
                        double price = scan.nextDouble();

                        String sql = "UPDATE bus SET bus_number = ?, bus_type = ?, total_seats = ?, price = ? WHERE bus_id = ?";
                        db.updateRecord(sql, number, type, seats, price, id);
                        System.out.println("✅ Bus updated!");
                    } else if (sub == 3) {
                        viewBuses(db);
                        System.out.print("Enter Bus ID to Delete: ");
                        int id = scan.nextInt();
                        String sql = "DELETE FROM bus WHERE bus_id = ?";
                        db.deleteRecord(sql, id);
                        System.out.println("✅ Bus deleted!");
                    } else if (sub == 4) {
                        viewBuses(db);
                    }
                    break;

                case 2:
                    viewBookings(db);
                    break;

                case 3:
                    System.out.println("Logging out...");
                    return;
            }

        } while (choice != 3);
    }
    public static void passengerMenu(Scanner scan, config db, Map<String, Object> user) {
        int choice;
        do {
            System.out.println("\n===== PASSENGER DASHBOARD =====");
            System.out.println("Welcome " + user.get("u_name"));
            System.out.println("1. View My Profile");
            System.out.println("2. Book Seat");
            System.out.println("3. View My Bookings");
            System.out.println("4. Logout");
            System.out.print("Enter choice: ");
            choice = scan.nextInt();

            switch (choice) {
                case 1:
                    String[] headers = {"User ID", "Name", "Email", "Phone"};
                    String[] cols = {"u_id", "u_name", "u_email", "u_phone_number"};
                    String qry = "SELECT * FROM user WHERE u_email = '" + user.get("u_email").toString() + "'";
                    db.viewRecords(qry, headers, cols);
                    break;

                case 2:
                    viewBuses(db);
                    System.out.print("Enter Bus ID to book: ");
                    int busId = scan.nextInt();
                    System.out.print("Enter Journey Date (YYYY-MM-DD): ");
                    String date = scan.next();

                    int seat;
                    while (true) {
                        System.out.print("Enter Seat Number: ");
                        seat = scan.nextInt();

                        String checkSeat = "SELECT * FROM booking WHERE bus_id = ? AND journey_date = ? AND seat_number = ?";
                        List<Map<String, Object>> seatResult = db.fetchRecords(checkSeat, busId, date, seat);

                        if (seatResult.isEmpty()) break;
                        System.out.println("❌ That seat is already booked! Please choose another seat.");
                    }

            
                    String getFare = "SELECT price FROM bus WHERE bus_id = ?";
                    List<Map<String, Object>> priceResult = db.fetchRecords(getFare, busId);
                    double fare = Double.parseDouble(priceResult.get(0).get("price").toString());

          
                    String sql = "INSERT INTO booking(bus_id, u_id, journey_date, seat_number, fare, booking_status, booking_time) VALUES (?, ?, ?, ?, ?, ?, datetime('now', 'localtime'))";
                    db.addRecord(sql, busId, user.get("u_id"), date, seat, fare, "Booked");
                    System.out.println("✅ Seat booked successfully! Fare: ₱" + fare);
                    break;

                case 3:
                    String[] bHeaders = {"Bus ID", "Journey Date", "Seat Number", "Fare", "Status", "Booking Time"};
                    String[] bCols = {"bus_id", "journey_date", "seat_number", "fare", "booking_status", "booking_time"};
                    String bQry = "SELECT * FROM booking WHERE u_id = " + user.get("u_id").toString();
                    db.viewRecords(bQry, bHeaders, bCols);
                    break;

                case 4:
                    System.out.println("Logging out...");
                    return;
            }
        } while (choice != 4);
    }
}

package finalProject;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Scanner;


import dbconnection.DBConnection;


public class Driver {

	private static final String ALL_USER = "SELECT * FROM User";
	private static final String ALL_RESERVATION = "SELECT * FROM Reservation";
	
	private static final String ADD_USER = 
			"INSERT INTO USER(user_firstName, user_lastName, user_email, user_phoneNumber) VALUES(?, ?, ?, ?)";
	private static final String ADD_RESERVATION = 
			"INSERT INTO RESERVATION(reservation_people, reservation_date, reservation_time, reservation_made_time, user_id) VALUES(?, ?, ?, ?, ?)";

	private static final String UPDATE_RESERVATION = 
			"UPDATE Reservation SET reservation_people = ?, reservation_date = ?, reservation_time = ? WHERE reservation_id = ?";

	private static final String DELETE_RESERVATION = 
			"DELETE FROM Reservation WHERE reservation_id = ?";
	 
	private static final String JOIN_USER_RES = 
			"SELECT Reservation.reservation_id, User.user_firstName, User.user_lastName, "
			+ "Reservation.reservation_people, Reservation.reservation_date, Reservation.reservation_time "
			+ "FROM Reservation "
			+ "INNER JOIN User "
			+ "ON Reservation.user_id = User.user_id";
	
	private static Scanner input = new Scanner(System.in);
	
	// get connection
	private static Connection getConnection() {
		try {
			return DriverManager.getConnection
					(DBConnection.CONN_STRING, DBConnection.USER, DBConnection.PASSWORD);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	// add reservation to database
	public static void addReservation(Connection conn) throws SQLException {
		PreparedStatement addReservation = conn.prepareStatement(ADD_RESERVATION);
		Reservation reservation = getAddReservation();
		
		addReservation.setInt(1, reservation.getReservation_people());
		addReservation.setDate(2, reservation.getReservation_date());
		addReservation.setTime(3, reservation.getReservation_time());
		addReservation.setTimestamp(4, reservation.getReservation_made_time());
		addReservation.setInt(5, reservation.getUser_id());
		addReservation.executeUpdate();
		
		System.out.println("Your reservation is on " + reservation.getReservation_date() + " " + reservation.getReservation_time() 
		+ " for party " + reservation.getReservation_people() + ".");
		
	}
	
	// get reservation infomation from user
	public static Reservation getAddReservation() {
		System.out.print("Enter number of party : ");
		int people = input.nextInt();
		
		System.out.print("When would you like to book? : ");
		String date = input.next();
		
		Date reservation_date = Date.valueOf(date);
		
		System.out.print("What time on " + date + " : ");
		String time = input.next();
		
		Time reservation_time = Time.valueOf(time);
		
        Timestamp timeNow = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd  hh:mm");
        String str = sdf.format(timeNow);

		System.out.print("Enter user id? : ");
		int user_id = input.nextInt();
		
		return new Reservation(people, reservation_date, reservation_time, timeNow, user_id);
	}
	
	// print all users and users reservation	
	public static void printReservation(Connection conn) throws SQLException {
			PreparedStatement printReservation = conn.prepareStatement(JOIN_USER_RES);
			ResultSet reservation = printReservation.executeQuery();
			
			while (reservation.next()) {
				System.out.println("Reservation Id : " + reservation.getInt("reservation_id") + 
						"\nCustomer Name : " + reservation.getString("user_firstName") + " " + reservation.getString("user_lastName") +
						"\nNumber of party : " + reservation.getString("reservation_people") +
						"\nDate : " + reservation.getDate("reservation_date") + " Time : " + reservation.getTime("reservation_time") + "\n");			
			}
	}
	
	public static void findReservation(Connection conn) throws SQLException {
		boolean isFound = false;
		PreparedStatement printReservation = conn.prepareStatement(JOIN_USER_RES);
		ResultSet reservation = printReservation.executeQuery();	
		System.out.print("Enter first name of customer : ");
		String inputFname = input.next();
		
		while (reservation.next()) {
			if(reservation.getString("user_firstName").equalsIgnoreCase(inputFname)) { 
				isFound = true;
				System.out.println("Reservation Id : " + reservation.getInt("reservation_id") + 
						"\nCustomer Name : " + reservation.getString("user_firstName") + " " + reservation.getString("user_lastName") +
						"\nNumber of party : " + reservation.getString("reservation_people") +
						"\nDate : " + reservation.getDate("reservation_date") + " Time : " + reservation.getTime("reservation_time") + "\n");	
			}
		}
		if (!isFound) {
			System.out.println(inputFname + "'s reservation is not found.");
		}
    }
	
	public static void findTodayReservation(Connection conn) throws SQLException {
		boolean isFound = false;
		PreparedStatement printReservation = conn.prepareStatement(JOIN_USER_RES);
		ResultSet reservation = printReservation.executeQuery();
		System.out.print("Enter date of today : ");
        String date = input.next();		
		Date reservation_date = Date.valueOf(date);	
				
		while (reservation.next()) {
			if(reservation.getDate("reservation_date").equals(reservation_date)) { 
				isFound = true;
				System.out.println("Reservation Id : " + reservation.getInt("reservation_id") + 
						"\nCustomer Name : " + reservation.getString("user_firstName") + " " + reservation.getString("user_lastName") +
						"\nNumber of party : " + reservation.getString("reservation_people") +
						"\nDate : " + reservation.getDate("reservation_date") + " Time : " + reservation.getTime("reservation_time") + "\n");	
			}
		}
		if (!isFound) {
			System.out.println("No reservation today.");
		}
    }
	
	// update reservation
	public static void updateReservation(Connection conn) throws SQLException {
		System.out.print("Enter the reservation id you want to update : ");
		int id = input.nextInt();
		System.out.print("Enter final number of party : ");
		int people = input.nextInt();
		System.out.print("Enter final date of reservation : ");
    	String date = input.next();
		Date reservation_date = Date.valueOf(date);
		System.out.print("Enter final time of reservation : ");
		String time = input.next();
		Time reservation_time = Time.valueOf(time);
		PreparedStatement updateReservation = conn.prepareStatement(UPDATE_RESERVATION);
		updateReservation.setInt(1, people);
		updateReservation.setDate(2, reservation_date);
		updateReservation.setTime(3, reservation_time);
		updateReservation.setInt(4, id);
		updateReservation.executeUpdate();
		
		System.out.println("Reservation id #" + id + " has been changed.");
	}
	
	public static void deleteReservation(Connection conn) throws SQLException {
		System.out.print("Enter a reservation id you want to cancel : ");
		int id = input.nextInt();
		PreparedStatement deleteReservation = conn.prepareStatement(DELETE_RESERVATION);
		deleteReservation.setInt(1, id);
		deleteReservation.executeUpdate();
		
		System.out.println("Reservation id #" + id + " has been canceled.");

	}
	
	// add new user to database
	public static void addUser(Connection conn) throws SQLException {
	PreparedStatement addUser = conn.prepareStatement(ADD_USER);
	User user = getAddUser(conn);
	
	addUser.setString(1, user.getUser_firstName());
	addUser.setString(2, user.getUser_lastName());
	addUser.setString(3, user.getUser_email());
	addUser.setString(4, user.getUser_phoneNumber());
	addUser.executeUpdate();

	System.out.println(user.getUser_firstName() + " " + user.getUser_lastName() + " is added.");
    }
	
	// get new user information from user
	public static User getAddUser(Connection conn) throws SQLException {
		System.out.print("Enter first name : ");
		String fname = input.nextLine();
		
		System.out.print("Enter last name : ");
		String lname = input.nextLine();
		
		System.out.print("Enter email address : ");
		String email = input.nextLine();
		
		while (!Validator.EmailValidator(email)) {
			System.err.println("Invalid email address.");
			System.out.print("Enter email address : ");
			email = input.nextLine();
		}
		
		System.out.print("Enter phone number : ");
		String phoneNumber = input.nextLine();
		
		while (!Validator.PhoneValidator(phoneNumber)) {
			System.err.println("Invalid phone number.");
			System.out.print("Enter phone number : ");
			email = input.nextLine();
		}

		return new User(fname, lname, email, phoneNumber);
	}
	
	// print all users and users reservation
	public static void printUser(Connection conn) throws SQLException {
		PreparedStatement printUser = conn.prepareStatement(ALL_USER);
		ResultSet rs = printUser.executeQuery();
		
		while (rs.next()) {
			System.out.println("Id: " + rs.getInt("user_id") + "\nName : " + rs.getString("user_firstName") + " " + rs.getString("user_lastName") +
					"\nEmail : " + rs.getString("user_email") + "\nPhone Number : " + rs.getString("user_phoneNumber"));
			
			
		}
	}
	
	public static void staffPage() {
		System.out.println("1 - check today's reservations"
				+ "\n2 - check all reservations"
				+ "\n3 - find a reservation"
				+ "\n4 - update a reservation"
				+ "\n5 - cancel a reservation"
				+ "\n0 - quit");
	}
	
	public static void process(Connection conn) throws SQLException {

		System.out.println("Would you like to go to [1] staff page [2] customer page?");
		int option = input.nextInt();
		
		switch (option) {
		// staff page
		case 1:
			staffPage();
			boolean quit = false;
			
			while(!quit) {
				System.out.print("What would you like to do? : ");
				int staffOption = input.nextInt();
				
				switch (staffOption) {
				case 1:
					findTodayReservation(conn);
					break;
				case 2:
					printReservation(conn);
					break;
				case 3:
					findReservation(conn);
					break;
				case 4:
					updateReservation(conn);
					break;
				case 5:
					deleteReservation(conn);
					break;
				case 0:
					quit = true;
					System.out.println("Have a good day!");
				default:
					System.err.println("Invalid input");
					break;
				}
			}
			break;
		// customer page
		case 2:
			
			
			break;

		default:
			break;
		}
		
	}




	
	public static void main(String[] args) throws SQLException {
		Connection conn = getConnection();
//		addUser(conn);
//		addReservation(conn);
//		printUser(conn);
//		printReservation(conn);
//		updateReservation(conn);
//		deleteReservation(conn);
		findReservation(conn);
//		findTodayReservation(conn);
//		process(conn);
	}
	
	
	
}

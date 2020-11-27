package finalProject;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
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
	
	// ----------------------------------------------------------------------------------------------------------------------------------------------
	
	// add reservation to database
	public static void addReservation(Connection conn) throws SQLException {
		PreparedStatement addReservation = conn.prepareStatement(ADD_RESERVATION);
		Reservation reservation = getAddReservation(conn);
		
		addReservation.setInt(1, reservation.getReservation_people());
		addReservation.setDate(2, reservation.getReservation_date());
		addReservation.setTime(3, reservation.getReservation_time());
		addReservation.setTimestamp(4, reservation.getReservation_made_time());
		addReservation.setInt(5, reservation.getUser_id());
		addReservation.executeUpdate();
		
		System.out.println("\nYour reservation is on " + reservation.getReservation_date() + " " + reservation.getReservation_time() 
		+ " for party " + reservation.getReservation_people() + ".");
		
	}
	
	public static Date getDate() {
        Date reservation_date = null;
        System.out.print("Enter date (yyyy-mm-dd): ");

        while (true) {
            String date = input.next();
            
            try{
                reservation_date = Date.valueOf(date);
            }catch(Exception e){
                System.err.println("Invalid date");
                System.out.print("Enter date (yyyy-mm-dd): ");
            }
            
            if(reservation_date != null){
                break;
            }
        }
        return reservation_date;
    }

    public static Time getTime() {
        Time reservation_time = null;
        System.out.print("Enter time (HH:MM:SS): ");

        while (true) {
            String time = input.next();
            
            try{
                reservation_time = Time.valueOf(time);
            }catch(Exception e){
                System.err.println("Invalid time");
                System.out.print("Enter time (HH:MM:SS): ");
            }
            
            if(reservation_time != null){
                break;
            }
        }
        return reservation_time;
    }
   
   public static int findId(Connection conn) throws SQLException {
		PreparedStatement user = conn.prepareStatement(ALL_USER);
		ResultSet userResultSet = user.executeQuery();
		
		boolean isFound = false;	
		int user_id = getId(conn);

		while (userResultSet.next()) {
			if(userResultSet.getInt("user_id") == user_id) {
				isFound = true;
				return user_id;
			}
		}
		if (!isFound) {
			System.err.println("Input user id is not found.");
			user_id = getId(conn);
		}
		return user_id;
	}
	
   public static int getId(Connection conn) {
		int user_id;
		do {
			System.out.print("Enter user id : ");
			while(!input.hasNextInt()) {
				System.err.println("Invalid input");
				System.out.print("Enter user id : ");
				input.next();
			}
			user_id = input.nextInt();
		} while(user_id <= 0);
		return user_id;
   }
	
	// get reservation information from user
	public static Reservation getAddReservation(Connection conn) throws SQLException {
		
		// avoid a bug when user inputs not interger
		int people;
		do {
			System.out.print("Enter number of party : ");
			while(!input.hasNextInt()) {
				System.err.println("Invalid input");
				System.out.print("Enter number of party : ");
				input.next();
			}
			people = input.nextInt();
		} while(people <= 0);
		
		Date reservation_date = getDate();
		
		Time reservation_time = getTime();
		
		// get time the reservation is made
        Timestamp timeNow = new Timestamp(System.currentTimeMillis());

		int user_id = findId(conn);
		
		return new Reservation(people, reservation_date, reservation_time, timeNow, user_id);
	}
	
	// ----------------------------------------------------------------------------------------------------------------------------------------------

	
	// print all users and users reservation	
	public static void printReservation(Connection conn) throws SQLException {
			PreparedStatement printReservation = conn.prepareStatement(JOIN_USER_RES);
			ResultSet reservation = printReservation.executeQuery();
			
			while (reservation.next()) {
				System.out.println("\nReservation Id : " + reservation.getInt("reservation_id") + 
						"\nCustomer Name : " + reservation.getString("user_firstName") + " " + reservation.getString("user_lastName") +
						"\nNumber of party : " + reservation.getString("reservation_people") +
						"\nDate : " + reservation.getDate("reservation_date") + " Time : " + reservation.getTime("reservation_time"));			
			}
	}
	
	// find a reservation by first name
	public static void findReservation(Connection conn) throws SQLException {
		boolean isFound = false;
		PreparedStatement printReservation = conn.prepareStatement(JOIN_USER_RES);
		ResultSet reservation = printReservation.executeQuery();	
		System.out.print("Enter first name of customer : ");
		String inputFname = input.next();
		
		while (reservation.next()) {
			if(reservation.getString("user_firstName").equalsIgnoreCase(inputFname)) { 
				isFound = true;
				System.out.println("\nReservation Id : " + reservation.getInt("reservation_id") + 
						"\nCustomer Name : " + reservation.getString("user_firstName") + " " + reservation.getString("user_lastName") +
						"\nNumber of party : " + reservation.getString("reservation_people") +
						"\nDate : " + reservation.getDate("reservation_date") + " Time : " + reservation.getTime("reservation_time"));	
			}
		}
		if (!isFound) {
			System.out.println(inputFname + "'s reservation is not found.");
		}
    }
	
	// find reservations by date
	public static void findTodayReservation(Connection conn) throws SQLException {
		boolean isFound = false;
		PreparedStatement printReservation = conn.prepareStatement(JOIN_USER_RES);
		ResultSet reservation = printReservation.executeQuery();
		Date reservation_date = getDate();
				
		while (reservation.next()) {
			if(reservation.getDate("reservation_date").equals(reservation_date)) { 
				isFound = true;
				System.out.println("\nReservation Id : " + reservation.getInt("reservation_id") + 
						"\nCustomer Name : " + reservation.getString("user_firstName") + " " + reservation.getString("user_lastName") +
						"\nNumber of party : " + reservation.getString("reservation_people") +
						"\nDate : " + reservation.getDate("reservation_date") + " Time : " + reservation.getTime("reservation_time"));	
			}
		}
		if (!isFound) {
			System.out.println("No reservation found.");
		}
    }
	
	// ----------------------------------------------------------------------------------------------------------------------------------------------
	
	
	// find a reservation by id
	public static int findReservationById(Connection conn) throws SQLException {
		PreparedStatement findId = conn.prepareStatement(ALL_RESERVATION);
		ResultSet reservation = findId.executeQuery();
		System.out.print("Enter the reservation id : ");
		
		// remove a bug when user puts not interger
		while (!input.hasNextInt()) {
			System.err.println("Invalid input");
			System.out.print("Enter the reservation id : ");
			input.next();
		}
		int id = input.nextInt();

		while (reservation.next()) {
			if(reservation.getInt("reservation_id") == id) { 
				return id;
		    }
		}
		return -1;
    }
	
	// update reservation
	public static void updateReservation(Connection conn) throws SQLException {
		int id = findReservationById(conn);
		if (id == -1) {
			System.out.println("No reservation found.");
		} else {
			System.out.print("Enter final number of party : ");
			// remove a bug when user puts not interger
			while (!input.hasNextInt()) {
				System.err.println("Invalid input");
				System.out.print("Enter final number of party : ");
				input.next();
			}
			int people = input.nextInt();
			
			Date reservation_date = getDate();
			
			Time reservation_time = getTime();

			PreparedStatement updateReservation = conn.prepareStatement(UPDATE_RESERVATION);
			updateReservation.setInt(1, people);
			updateReservation.setDate(2, reservation_date);
			updateReservation.setTime(3, reservation_time);
			updateReservation.setInt(4, id);
			updateReservation.executeUpdate();
			
			System.out.println("\nReservation id #" + id + " has been changed.");	
		}
	}
	
	// delete a reservation
	public static void deleteReservation(Connection conn) throws SQLException {
		PreparedStatement deleteReservation = conn.prepareStatement(DELETE_RESERVATION);
		int id = findReservationById(conn);
		if (id == -1) {
			System.out.println("No reservation found.");
		} else {
		    deleteReservation.setInt(1, id);
		    deleteReservation.executeUpdate();
		
		    System.out.println("\nReservation id #" + id + " has been canceled.");
		}

	}
	
	// ----------------------------------------------------------------------------------------------------------------------------------------------

	
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

	// find user
	public static ResultSet findUser(Connection conn, String email) throws SQLException {
		PreparedStatement findUser = conn.prepareStatement(ALL_USER);
		ResultSet user = findUser.executeQuery();
		
		while (user.next()) {
			if (user.getString("user_email").equalsIgnoreCase(email)) {
				return user;
			}
		}
		return null;
	}
	
	// find if email address is used or no
	public static String findEmail(Connection conn) throws SQLException {
		System.out.print("Enter email address : ");
		String email = input.nextLine();
		boolean isFound = true;
				
		while (isFound) {
			ResultSet returnUser = findUser(conn, email);
			if (returnUser != null) {
				System.err.println("This email addrese is already used.");
				System.out.print("Enter email address : ");
				email = input.nextLine();
			} else if (returnUser == null) {
				isFound = false;
				return email;
			}
		}
		return null;
	}
	
	
	// get new user information from user
	public static User getAddUser(Connection conn) throws SQLException {
		System.out.print("Enter first name : ");
		String fname = input.nextLine();
		
		System.out.print("Enter last name : ");
		String lname = input.nextLine();
		
		String email = findEmail(conn);	
		while (!Validator.EmailValidator(email)) {
			System.err.println("Invalid email address.");
			email = findEmail(conn);
		}
		
		System.out.print("Enter phone number : ");
		String phoneNumber = input.nextLine();
		
		while (!Validator.PhoneValidator(phoneNumber)) {
			System.err.println("Invalid phone number.");
			System.out.print("Enter phone number : ");
			phoneNumber = input.nextLine();
		}

		return new User(fname, lname, email, phoneNumber);
	}
	
	// print all users and users reservation
	public static void printUser(Connection conn) throws SQLException {
		PreparedStatement printUser = conn.prepareStatement(ALL_USER);
		ResultSet rs = printUser.executeQuery();
		
		while (rs.next()) {
			System.out.println("\nUser id: " + rs.getInt("user_id") + "\nName : " + rs.getString("user_firstName") + " " + rs.getString("user_lastName") +
					"\nEmail : " + rs.getString("user_email") + "\nPhone Number : " + rs.getString("user_phoneNumber"));
		}
	}
	
	// ----------------------------------------------------------------------------------------------------------------------------------------------

	
	public static void staffPage() {
		System.out.println("\n------------OPTION-------------\n"
				+ "\n1 - check today's reservations"
			+ "\n2 - check all reservations"
			+ "\n3 - find a reservation"
			+ "\n4 - update a reservation"
			+ "\n5 - cancel a reservation"
			+ "\n0 - quit");
	}
	
	public static void process(Connection conn) throws SQLException {

		System.out.println("Would you like to go to [1] staff page [2] customer page?");

		// remove a bug when user puts not interger
		while (!input.hasNextInt()) {
			System.err.println("Invalid input");
			System.out.println("Would you like to go to [1] staff page [2] customer page?");
			input.next();
		}
		int option = input.nextInt();

		switch (option) {
		// staff page
		case 1:
			staffPage();
			boolean quit = false;
			
			while(!quit) {
				System.out.println("\n-------------------------------");
				System.out.print("\nWhat would you like to do? : ");
				
				// remove a bug when user puts not interger
				while (!input.hasNextInt()) {
					System.err.println("Invalid input");
					System.out.println("\n-------------------------------");
					System.out.print("\nWhat would you like to do? : ");
					input.next();
				}
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
					break;
				default:
					System.err.println("Invalid input");
					break;
				}
			}
			break;
			
		// customer page
		case 2:
			System.out.println("Welcome to customer page");	
			break;

		default:
			System.err.println("Invalid input");
			process(conn);
			break;
		}
		
	}
	
	// ----------------------------------------------------------------------------------------------------------------------------------------------

	
	public static void main(String[] args) throws SQLException {
		Connection conn = getConnection();

		process(conn);	

	}

}

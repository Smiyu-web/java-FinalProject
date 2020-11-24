package finalProject;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;


public class Reservation {

	private int reservation_id;
	private int reservation_people;
	private Date reservation_date;
	private Time reservation_time;
	private Timestamp reservation_made_time;
	private int user_id;
	
	private Reservation reservations;
	
	public Reservation(int reservation_people, Date reservation_date, Time reservation_time,
			Timestamp reservation_made_time, int user_id) {
		this.reservation_id = reservation_id;
		this.reservation_people = reservation_people;
		this.reservation_date = reservation_date;
		this.reservation_time = reservation_time;
		this.reservation_made_time = reservation_made_time;
		this.user_id = user_id;
		
		this.reservations = reservations;
	}

	public int getReservation_id() {
		return reservation_id;
	}

	public void setReservation_id(int reservation_id) {
		this.reservation_id = reservation_id;
	}

	public int getReservation_people() {
		return reservation_people;
	}

	public void setReservation_people(int reservation_people) {
		this.reservation_people = reservation_people;
	}

	public Date getReservation_date() {
		return reservation_date;
	}

	public void setReservation_date(Date reservation_date) {
		this.reservation_date = reservation_date;
	}

	public Time getReservation_time() {
		return reservation_time;
	}

	public void setReservation_time(Time reservation_time) {
		this.reservation_time = reservation_time;
	}

	public Timestamp getReservation_made_time() {
		return reservation_made_time;
	}

	public void setReservation_made_time(Timestamp reservation_made_time) {
		this.reservation_made_time = reservation_made_time;
	}

	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public Reservation getReservations() {
		return reservations;
	}

	public void setReservations(Reservation reservations) {
		this.reservations = reservations;
	}
	
	
}

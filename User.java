package finalProject;

public class User {
	
	private int user_id;
	private String user_firstName;
	private String user_lastName;
	private String user_email;
	private String user_phoneNumber;
	
	private final String UNKNOWN = "Unknown";
	
	public User(String user_firstName, String user_lastName, String user_email, String user_phoneNumber) {
		this.user_id = user_id;
		this.user_firstName = user_firstName;
		this.user_lastName = user_lastName;
		this.user_email = user_email;
		this.user_phoneNumber = user_phoneNumber;
	}

	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public String getUser_firstName() {
		return user_firstName;
	}

	public void setUser_firstName(String user_firstName) {
		this.user_firstName = user_firstName;
	}

	public String getUser_lastName() {
		return user_lastName;
	}

	public void setUser_lastName(String user_lastName) {
		this.user_lastName = user_lastName;
	}

	public String getUser_email() {
		return user_email;
	}


	public void setUser_email(String user_email) {
		if (!user_email.isEmpty() && !user_email.equalsIgnoreCase(null)) {
			this.user_email = user_email;
		} else {
			this.user_email = UNKNOWN;
		}
	}

	public String getUser_phoneNumber() {
		return user_phoneNumber;
	}

	public void setUser_phoneNumber(String user_phoneNumber) {
		if (!user_phoneNumber.isEmpty() && !user_phoneNumber.equalsIgnoreCase(null)) {
			this.user_phoneNumber = user_phoneNumber;
		} else {
			this.user_phoneNumber = UNKNOWN;
		}	}
	
	
	
	
}

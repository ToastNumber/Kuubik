package jCube;

/**
 * @author Kelsey McKenna
 */
public class Member {
	/**
	 * Stores the ID of the member
	 */
	private int memberID;
	/**
	 * Stores the forenames of the member
	 */
	private String forenames;
	/**
	 * Stores the surname of the member
	 */
	private String surname;
	/**
	 * Stores the gender of the member
	 */
	private String gender;
	/**
	 * Stores the date of birth of the member
	 */
	private String dateOfBirth;
	/**
	 * Stores an email address belonging to the member
	 */
	private String email;
	/**
	 * Stores the form class to which the member belongs
	 */
	private String formClass;

	/**
	 * Constructor - assigns values to the fields
	 * 
	 * @param memberID
	 *            the ID of the member
	 * @param forenames
	 *            the forenames of the member
	 * @param surname
	 *            the surname of the member
	 * @param gender
	 *            the gender of the member ("male" or "female")
	 * @param dateOfBirth
	 *            the date of birth of the member (in form 'dd/MM/YYYY')
	 * @param email
	 *            the email address of the member
	 * @param formClass
	 *            the form class to which the member belongs
	 */
	public Member(int memberID, String forenames, String surname, String gender, String dateOfBirth, String email,
			String formClass) {
		super();
		this.memberID = memberID;
		this.forenames = forenames;
		this.surname = surname;
		this.gender = gender;
		this.dateOfBirth = dateOfBirth;
		this.email = email;
		this.formClass = formClass;
	}

	/**
	 * @return the ID of the member
	 */
	public int getMemberID() {
		return memberID;
	}

	/**
	 * Assigns the specified ID to the member
	 * 
	 * @param memberID
	 *            the new ID to be assigned to the member
	 */
	public void setMemberID(int memberID) {
		this.memberID = memberID;
	}

	/**
	 * @return the forenames of the member
	 */
	public String getForenames() {
		return forenames;
	}

	/**
	 * Assigns the specified forenames to the member
	 * 
	 * @param forenames
	 *            the new forenames to be assigned to the member
	 */
	public void setForenames(String forenames) {
		this.forenames = forenames;
	}

	/**
	 * @return the surname of the member
	 */
	public String getSurname() {
		return surname;
	}

	/**
	 * Assigns the specified surname to the member
	 * 
	 * @param surname
	 *            the new surname to be assigned to the member
	 */
	public void setSurname(String surname) {
		this.surname = surname;
	}

	/**
	 * @return the gender of the member - <b>"male"</b> or <b>"female"</b>
	 */
	public String getGender() {
		return gender;
	}

	/**
	 * Assigns the specified gender to the member
	 * 
	 * @param gender
	 *            the new gender to be assigned to the member - <b>"male"</b> or
	 *            <b>"female"</b>
	 */
	public void setGender(String gender) {
		this.gender = gender;
	}

	/**
	 * @return the date of birth of the member
	 */
	public String getDateOfBirth() {
		return dateOfBirth;
	}

	/**
	 * Assigns the specified date of birth to the member
	 * 
	 * @param dateOfBirth
	 *            the new date of birth for the member
	 */
	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	/**
	 * @return the email address of the member
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Assigns the specified email to the member
	 * 
	 * @param email
	 *            the new email address to be assigned to the member
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the form class to which the member belongs
	 */
	public String getFormClass() {
		return formClass;
	}

	/**
	 * Assigns the specified form class to the member
	 * 
	 * @param formClass
	 *            the new form class to be assigned to the member
	 */
	public void setFormClass(String formClass) {
		this.formClass = formClass;
	}

	/**
	 * Determines whether the specified email is in a valid format or not. This
	 * does not verify that the address exists
	 * 
	 * @param email
	 *            the email to be analysed
	 * @return <b>true</b> if the email address is valid; <br>
	 *         <b>false</b> otherwise
	 */
	public static boolean isValidEmail(String email) {
		TextFile cFile = new TextFile();
		String regex = "";
		try {
			cFile.setFilePath("res/RFC822.txt");
			cFile.setIO(TextFile.READ);
			regex = cFile.readLine();
			cFile.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return email.matches(regex);
	}

	/**
	 * Determines whether the specified form class is valid or not. Valid form
	 * classes are of the form: <br>
	 * 08 | M <br>
	 * 09 | R <br>
	 * 10 | S <br>
	 * 11 | T <br>
	 * 12 | W <br>
	 * 13 | <br>
	 * 14 | <br>
	 * Leading zeros are ignored
	 * 
	 * @param formClass
	 *            the form class to be analysed
	 * @return <b>true</b> if the form class is valid; <br>
	 *         <b>false</b> otherwise
	 */
	public static boolean isValidFormClass(String formClass) {
		return formClass.matches("0*(|8|9|10|11|12|13|14)[MRSTW]");
	}

}

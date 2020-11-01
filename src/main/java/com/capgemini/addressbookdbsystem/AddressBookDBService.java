package com.capgemini.addressbookdbsystem;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class AddressBookDBService {

	private PreparedStatement ContactDataStatement;
	private static AddressBookDBService addressBookDBService;
	private static Logger log = Logger.getLogger(AddressBookDBService.class.getName());

	private AddressBookDBService() {
	}

	// Singleton object creation
	public static AddressBookDBService getInstance() {
		if (addressBookDBService == null)
			addressBookDBService = new AddressBookDBService();
		return addressBookDBService;
	}

	// Creating and returning connection object
	private Connection getConnection() throws AddressBookSystemException {
		String jdbcURL = "jdbc:mysql://localhost:3306/address_book_service?useSSL=false";
		String userName = "root";
		String password = "123qwe";
		Connection connection;
		log.info("connecting to database: " + jdbcURL);
		try {
			connection = DriverManager.getConnection(jdbcURL, userName, password);
		} catch (SQLException e) {
			throw new AddressBookSystemException("Cannot establish connection to Database");
		}
		log.info("connection successful! " + connection);
		return connection;
	}

	// Creating prepared statement for DB service
	private void prepareStatementForContactData() throws AddressBookSystemException {
		try {
			Connection connection = addressBookDBService.getConnection();
			String query = "select c.firstname, c.lastname,c.address,c.city,"
					+ "c.state,c.zip,c.phone,c.email,c.addressbookname,a.type"
					+ " from contact c inner join addressbook a"
					+ " on c.addressbookname=a.addressbookname where firstname=?";
			ContactDataStatement = connection.prepareStatement(query);
		} catch (SQLException e) {
			throw new AddressBookSystemException("Error in preparing statement");
		}
	}

	// Reading data from DB and returning contact list
	public List<Contact> readDataFromDB() throws AddressBookSystemException {
		String query = "select c.firstname, c.lastname,c.address,c.city,c.state,c.zip,"
				+ "c.phone,c.email,c.addressbookname,a.type"
				+ " from contact c inner join addressbook a"
				+ " on c.addressbookname=a.addressbookname";
		return this.getContactByQuery(query);
	}

	// Getting the data and returning contact list
	private List<Contact> getContactByQuery(String query) throws AddressBookSystemException {
		List<Contact> contactList = null;
		try (Connection connection = addressBookDBService.getConnection();) {
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			ResultSet resultSet = preparedStatement.executeQuery(query);
			contactList = this.getAddressBookData(resultSet);
		} catch (SQLException e) {
			throw new AddressBookSystemException("Error in getting the result set from database");
		}
		return contactList;
	}

	// Retrieving contact from DB using name
	public List<Contact> getcontactDataByName(String name) throws AddressBookSystemException {
		List<Contact> contactList = null;
		if (this.ContactDataStatement == null)
			this.prepareStatementForContactData();
		try {
			ContactDataStatement.setString(1, name);
			ResultSet resultSet = ContactDataStatement.executeQuery();
			contactList = this.getAddressBookData(resultSet);
		} catch (SQLException e) {
			throw new AddressBookSystemException("Error in getting contact by name");
		}
		return contactList;
	}

	// Retrieving contact by date
	public List<Contact> getcontactDataByDate(LocalDate startDate, LocalDate endDate)
			throws AddressBookSystemException {
		String query = String.format("select c.firstname, c.lastname,c.address,c.city,"
				+ "c.state,c.zip,c.phone,c.email,c.addressbookname,a.type"
				+ " from contact c inner join addressbook a"
				+ " on c.addressbookname=a.addressbookname"
				+ " where dateadded between '%s' AND '%s'",
				Date.valueOf(startDate), Date.valueOf(endDate));
		try {
			return this.getContactByQuery(query);
		} catch (AddressBookSystemException e) {
			throw new AddressBookSystemException("Error in getting contact by date range");
		}
	}

	// Populating the contact object and adding to contact list
	private List<Contact> getAddressBookData(ResultSet resultSet) throws AddressBookSystemException {
		List<Contact> contactList = new ArrayList<>();
		try {
			while (resultSet.next()) {
				String firstName = resultSet.getString("firstname");
				String lastName = resultSet.getString("lastname");
				String address = resultSet.getString("address");
				String city = resultSet.getString("city");
				String state = resultSet.getString("state");
				String zip = resultSet.getString("zip");
				String phoneNumber = resultSet.getString("phone");
				String email = resultSet.getString("email");
				String addressBookName = resultSet.getString("addressbookname");
				String addressBookType = resultSet.getString("type");
				contactList.add(new Contact(firstName, lastName, address, city, state, zip, phoneNumber, email,
						addressBookName, addressBookType));
			}
		} catch (SQLException e) {
			throw new AddressBookSystemException("Error in populating the contact object");
		}
		return contactList;
	}

	// Updating contact address by passing name and address
	public int updateContactToDB(String name, String address) throws AddressBookSystemException {
		return this.updateContactDataUsingPreparedStatement(name, address);
	}

	// Executing prepared statement to update contact in DB
	private int updateContactDataUsingPreparedStatement(String name, String address) throws AddressBookSystemException {
		try (Connection connection = addressBookDBService.getConnection();) {
			String query = "update contact set address=? where firstname=?";
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, address);
			preparedStatement.setString(2, name);
			return preparedStatement.executeUpdate();
		} catch (SQLException e) {
			throw new AddressBookSystemException("Error in updating contact using prepared statement");
		}
	}
}

package com.capgemini.addressbookdbsystem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class AddressBookDBService {

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
	// Reading data from DB and returning contact list
	public List<Contact> readDataFromDB() throws AddressBookSystemException {
		String query = "select c.firstname, c.lastname,c.address,c.city,c.state,c.zip,"
				+ "c.phone,c.email,c.addressbookname,a.type from contact c inner join addressbook a"
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
	
	// Populating the contact object and adding to contact list
	private List<Contact> getAddressBookData(ResultSet resultSet) throws AddressBookSystemException {
		List<Contact> contactList = new ArrayList<>();
		try {
		while(resultSet.next()) {
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
}

package com.capgemini.addressbookdbsystem;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
				+ "c.phone,c.email,c.addressbookname,a.type" + " from contact c inner join addressbook a"
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
				+ "c.state,c.zip,c.phone,c.email,c.addressbookname,a.type" + " from contact c inner join addressbook a"
				+ " on c.addressbookname=a.addressbookname" + " where dateadded between '%s' AND '%s'",
				Date.valueOf(startDate), Date.valueOf(endDate));
		try {
			return this.getContactByQuery(query);
		} catch (AddressBookSystemException e) {
			throw new AddressBookSystemException("Error in getting contact by date range");
		}
	}

	// Retrieving contact by city or state
	public Map<String, Integer> getcontactDataByCityOrState() throws AddressBookSystemException {
		Map<String, Integer> contactCountByCityOrState = new HashMap<>();
		String queryForCityCount = "select city,count(firstname) as count from contact group by city";
		String queryForStateCount = "select state,count(firstname) as count from contact group by state";
		ResultSet resultSetCount;
		try (Connection connection = addressBookDBService.getConnection()) {
			Statement statement = connection.createStatement();
			resultSetCount = statement.executeQuery(queryForCityCount);
			while (resultSetCount.next()) {
				String city = resultSetCount.getString("city");
				int countCity = resultSetCount.getInt("count");
				contactCountByCityOrState.put(city, countCity);
			}
			resultSetCount = statement.executeQuery(queryForStateCount);
			while (resultSetCount.next()) {
				String state = resultSetCount.getString("state");
				int countState = resultSetCount.getInt("count");
				contactCountByCityOrState.put(state, countState);
			}
		} catch (SQLException e) {
			throw new AddressBookSystemException("Error in getting count by city or state");
		}
		return contactCountByCityOrState;
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
				long zip = resultSet.getLong("zip");
				long phoneNumber = resultSet.getLong("phone");
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

	public Contact addContactToDb(String firstName, String lastName, String address, String city, String state,
			long zip, long phone, String email, String addressBookName, String addressBookType, LocalDate dateAdded)
			throws AddressBookSystemException {
		Connection connection = null;
		try {
			connection = this.getConnection();
			connection.setAutoCommit(false);
		} catch (SQLException e) {
			throw new AddressBookSystemException("Error in establising connection for adding to database");
		}

		try {
			Statement statement = connection.createStatement();
			String query = String.format(
					"insert into contact values ('%s','%s','%s','%s','%s','%s','%s','%s','%s','%s')", firstName,
					lastName, address, city, state, zip, phone, email, addressBookName, dateAdded);
			statement.executeUpdate(query);
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e1) {
				throw new AddressBookSystemException("Error in inserting record to Contact table");
			}
		}
		// Check for duplicates before entry
		try {
			Statement statement = connection.createStatement();
			List<String> addressBookNameList = new ArrayList<>();
			ResultSet resultSetAddressBook = statement.executeQuery("select * from addressbook");
			while (resultSetAddressBook.next()) {
				String primaryKey = resultSetAddressBook.getString("addressbookname");
				addressBookNameList.add(primaryKey);
			}
			int duplicateCounter = 0;
			for (String string : addressBookNameList) {
				if (!string.equalsIgnoreCase(addressBookName))
					duplicateCounter++;
			}
			if (duplicateCounter == 0) {
				String query = String.format("insert into addressbook(addressbookname,type) VALUES ('%s','%s');",
						addressBookName, addressBookType);
				statement.executeUpdate(query);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e2) {
				throw new AddressBookSystemException("Error in inserting record to AddressBook table");
			}
		}

		try {
			connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return new Contact(firstName, lastName, address, city, state, zip, phone, email, addressBookName,
				addressBookType, dateAdded);
	}
}

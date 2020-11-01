package com.capgemini.addressbookdbsystem;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class AddressBookService {

	private static Logger log = Logger.getLogger(AddressBookService.class.getName());

	private List<Contact> contactList;
	private Map<String, Integer> countByCityOrState;
	private AddressBookDBService addressBookDBService;

	public AddressBookService() {
		addressBookDBService = AddressBookDBService.getInstance();
	}

	public AddressBookService(List<Contact> contactList) {
		this();
		this.contactList = contactList;
	}

	// Reading and returning list of contact from DB
	public List<Contact> readDataFromDB() throws AddressBookSystemException {
		contactList = addressBookDBService.readDataFromDB();
		return contactList;
	}

	// Reading and returning list of contact for date range from DB
	public List<Contact> readContactForDateRange(LocalDate startDate, LocalDate endDate)
			throws AddressBookSystemException {
		return addressBookDBService.getcontactDataByDate(startDate, endDate);
	}

	// Reading and returning contact by city or state
	public Map<String, Integer> readContactByCityOrState() throws AddressBookSystemException {
		this.countByCityOrState = addressBookDBService.getcontactDataByCityOrState();
		return this.countByCityOrState;
	}

	// Adding contact to DB
	public void addContactToDB(String firstName, String lastName, String address, String city, String state, long zip,
			long phone, String email, String addressBookName, String addressBookType, LocalDate dateAdded) throws AddressBookSystemException {
		this.contactList.add(addressBookDBService.addContactToDb(firstName, lastName, address, city, state, zip, phone,
				email, addressBookName, addressBookType, dateAdded));

	}

	// Getting contact data from POJO class
	private Contact getContactData(String name) {
		return this.contactList.stream().filter(contact -> contact.firstName.equals(name)).findFirst().orElse(null);
	}

	// Updating contact to DB using name and address
	public void updateContactToDatabase(String name, String address) throws AddressBookSystemException {
		int result = addressBookDBService.updateContactToDB(name, address);
		if (result == 0)
			return;
		Contact contact = this.getContactData(name);
		if (contact != null)
			contact.address = address;
	}

	// Check for sync with DB
	public boolean checkContactInfoSyncWithDB(String name) throws AddressBookSystemException {
		List<Contact> contactList = addressBookDBService.getcontactDataByName(name);
		return contactList.get(0).equals(getContactData(name));
	}
}

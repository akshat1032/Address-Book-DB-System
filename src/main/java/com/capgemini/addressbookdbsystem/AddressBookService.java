package com.capgemini.addressbookdbsystem;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class AddressBookService {

	public static Logger log = Logger.getLogger(AddressBookService.class.getName());

	private List<Contact> contactList;
	private Map<String, Integer> countByCityOrState;
	private AddressBookDBService addressBookDBService;

	public AddressBookService() {
		addressBookDBService = AddressBookDBService.getInstance();
	}

	public AddressBookService(List<Contact> contactList) {
//		this();
		this.contactList = new ArrayList<>(contactList);
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
			long phone, String email, String addressBookName, String addressBookType, LocalDate dateAdded)
			throws AddressBookSystemException {
		this.contactList.add(addressBookDBService.addContactToDb(firstName, lastName, address, city, state, zip, phone,
				email, addressBookName, addressBookType, dateAdded));

	}

	// Adding multiple contact to DB
	public void addMultipleContactToDB(List<Contact> contactList) {
		contactList.forEach(contactData -> {
			try {
				this.addContactToDB(contactData.firstName, contactData.lastName, contactData.address, contactData.city,
						contactData.state, contactData.zip, contactData.phone, contactData.email,
						contactData.addressBookName, contactData.addressBookType, contactData.dateAdded);
			} catch (AddressBookSystemException e) {
				e.printStackTrace();
			}
		});
	}

	// Adding multiple contact to DB using threads
	public void addMultipleContactToDBUsingThreads(List<Contact> contactList) {
		Map<Integer, Boolean> contactAdditionStatus = new HashMap<>();
		contactList.forEach(contactData -> {
			Runnable task = () -> {
				contactAdditionStatus.put(contactData.hashCode(), false);
				log.info("Employee being added : " + Thread.currentThread().getName());
				try {
					this.addContactToDB(contactData.firstName, contactData.lastName, contactData.address,
							contactData.city, contactData.state, contactData.zip, contactData.phone, contactData.email,
							contactData.addressBookName, contactData.addressBookType, contactData.dateAdded);
				} catch (AddressBookSystemException e) {
					e.printStackTrace();
				}
				contactAdditionStatus.put(contactData.hashCode(), true);
			};
			Thread thread = new Thread(task, contactData.firstName);
			thread.start();
		});
		while (contactAdditionStatus.containsValue(false)) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
			}
		}
	}

	// Getting contact data from POJO class
	public Contact getContactData(String name) {
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

	// Returning no of entries
	public long countEntries() {
		return contactList.size();
	}

	// Adding contact to the list
	public void addContactToJSONServer(Contact contactInfo) {
		this.contactList.add(contactInfo);
	}

	// Update contact details for server data
	public void updateContactDetailsForServer(String firstName, String address) {
		Contact contact = this.getContactData(firstName);
		if (contact != null)
			contact.address = address;
	}
	
	// Deleting a contact by name from server record
	public void deleteContactFromServer(String firstName) {
		Contact contact = this.getContactData(firstName);
		contactList.remove(contact);

	}
}

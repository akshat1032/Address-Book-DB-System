package com.capgemini.addressbooksystemdbtest;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.Test;

import com.capgemini.addressbookdbsystem.AddressBookService;
import com.capgemini.addressbookdbsystem.AddressBookSystemException;
import com.capgemini.addressbookdbsystem.Contact;

public class AddressBookSystemDBTest {

	private Logger log = Logger.getLogger(AddressBookSystemDBTest.class.getName());

	// Check contact count when reading from DB
	@Test
	public void contactsRetrievedFromDB_MatchCount() throws AddressBookSystemException {
		AddressBookService addressBookService = new AddressBookService();
		List<Contact> contactList = addressBookService.readDataFromDB();
		Assert.assertEquals(8, contactList.size());
		log.info("Entries from database count matched successfully!");
	}

	// Update contact should sync with DB
	@Test
	public void contactUpdated_SyncWithDB() throws AddressBookSystemException {
		AddressBookService addressBookService = new AddressBookService();
		List<Contact> contactList = addressBookService.readDataFromDB();
		addressBookService.updateContactToDatabase("Mahesh", "Danapur");
		boolean result = addressBookService.checkContactInfoSyncWithDB("Mahesh");
		Assert.assertTrue(result);
		log.info("Update contact in sync with DB tested successfully!");
	}

	// Match contact count when retrieved for DB
	@Test
	public void contactsRetrievedForDateRange_MatchCount() throws AddressBookSystemException {
		AddressBookService addressBookService = new AddressBookService();
		addressBookService.readDataFromDB();
		LocalDate startDate = LocalDate.of(2018, 01, 01);
		LocalDate endDate = LocalDate.now();
		List<Contact> contactList = addressBookService.readContactForDateRange(startDate, endDate);
		Assert.assertEquals(6, contactList.size());
		log.info("Retrieve contact for date range from DB count matched successfully!");
	}

	// Match count of contact when retrieved from DB using city or state
	@Test
	public void contactsRetrieved_NumberOfContacts_ByCityOrState() throws AddressBookSystemException {
		AddressBookService addressBookService = new AddressBookService();
		addressBookService.readDataFromDB();
		Map<String, Integer> contactCountByCityOrState = addressBookService.readContactByCityOrState();
		Assert.assertTrue(contactCountByCityOrState.get("Lucknow").equals(1)
				&& contactCountByCityOrState.get("Mumbai").equals(1)
				&& contactCountByCityOrState.get("Meerut").equals(1) && contactCountByCityOrState.get("Patna").equals(1)
				&& contactCountByCityOrState.get("Bihar").equals(1)
				&& contactCountByCityOrState.get("Maharashtra").equals(1)
				&& contactCountByCityOrState.get("Uttar Pradesh").equals(2));
		log.info("Contact count by city or state tested successfully!");
	}

	// New contact added and check sync with DB
	@Test
	public void givenNewContact_WhenAdded_ShouldSyncWithDB() throws AddressBookSystemException {
		AddressBookService addressBookService = new AddressBookService();
		addressBookService.readDataFromDB();
		LocalDate date = LocalDate.now();
		addressBookService.addContactToDB("Sasuke", "Uchiha", "Mount Myoboku", "Konoha", "Land of Fire", 321456, 99966642l,
				"sasukesclass@akatsuki.com", "Casual", "Friends", date);
		boolean result = addressBookService.checkContactInfoSyncWithDB("Sasuke");
		Assert.assertTrue(result);
		log.info("Contact added to database should sync with databse tested succesfully!");
	}
	
	// Add multiple contacts to DB
	@Test
	public void givenMultipleContacts_WhenAdded_MatchCount() throws AddressBookSystemException {
		Contact[] contactArray = {
				new Contact("Sohan", "Raj", "Kushpur", "Purnea", "Bihar", 800012l, 98352980l,
						"sohan@gmail.com", "Personal","Family", LocalDate.now()),
				new Contact("Rahul", "Kapoor", "Luvpur", "Lucknow", "Uttar Pradesh", 800023l, 96577782l,
						"rahul_kapoor@gmail.com", "Casual","Friends", LocalDate.now()),
				new Contact("Kapil", "Sharma", "Gainkothi", "Udaipur", "Rajasthan", 768002l, 999666333l,
						"kapilsharma@rediff.com", "Work","Professional", LocalDate.now()) };
		AddressBookService addressBookService = new AddressBookService();
		addressBookService.readDataFromDB();
		Instant start = Instant.now();
		addressBookService.addMultipleContactToDB(Arrays.asList(contactArray));
		Instant end = Instant.now();
		log.info("Duration without thread : " + Duration.between(start, end));
		Instant threadStart = Instant.now();
		addressBookService.addMultipleContactToDBUsingThreads(Arrays.asList(contactArray));
		Instant threadEnd = Instant.now();
		log.info("Duartion with Thread : " + Duration.between(threadStart, threadEnd));
		Assert.assertEquals(10, addressBookService.countEntries());
	}

}

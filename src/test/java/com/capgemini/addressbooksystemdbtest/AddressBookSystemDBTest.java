package com.capgemini.addressbooksystemdbtest;

import java.time.LocalDate;
import java.util.List;
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
		Assert.assertEquals(3, contactList.size());
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
		Assert.assertEquals(2, contactList.size());
		log.info("Retrieve contact for date range from DB count matched successfully!");
	}

}

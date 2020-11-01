package com.capgemini.addressbooksystemdbtest;

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

}

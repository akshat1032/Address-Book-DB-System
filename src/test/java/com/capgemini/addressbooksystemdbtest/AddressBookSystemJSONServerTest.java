package com.capgemini.addressbooksystemdbtest;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.capgemini.addressbookdbsystem.AddressBookService;
import com.capgemini.addressbookdbsystem.AddressBookSystemException;
import com.capgemini.addressbookdbsystem.Contact;
import com.google.gson.Gson;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class AddressBookSystemJSONServerTest {

	@Before
	public void setUp() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = 3000;
	}

	public static Contact[] getContactList() {
		Response response = RestAssured.get("/contact");
		AddressBookService.log.info("Contact in server :\n" + response.asString());
		Contact[] arrayOfContacts = new Gson().fromJson(response.asString(), Contact[].class);
		return arrayOfContacts;
	}

	public Response addContactToJsonServer(Contact contactData) {
		String contactJson = new Gson().toJson(contactData);
		RequestSpecification request = RestAssured.given();
		request.header("Content-Type", "application/json");
		request.body(contactJson);
		return request.post("/contact");
	}

	@Test
	public void givenNewContact_WhenAdded_MatchCount() throws AddressBookSystemException {
		AddressBookService addressBookService;
		Contact[] arrayOfContacts = getContactList();
		addressBookService = new AddressBookService(Arrays.asList(arrayOfContacts));
		Contact contactInfo = null;
		contactInfo = new Contact(3, "Ramesh", "Kumar", "Kankarbagh", "Patna", "Bihar", 800006l, 9874563l,
				"Ramesh.kumar@gmail.com", "Personal", "Family", LocalDate.now());
		Response response = addContactToJsonServer(contactInfo);
		int statusCode = response.getStatusCode();
		Assert.assertEquals(201, statusCode);
		contactInfo = new Gson().fromJson(response.asString(), Contact.class);
		addressBookService.addContactToJSONServer(contactInfo);
		long entries = addressBookService.countEntries();
		Assert.assertEquals(3, entries);
	}
	
	@Test
	public void givenContactInJsonServer_WhenRetrived_MatchCount() {
		Contact[] contactArray = getContactList();
		AddressBookService addressBookService;
		addressBookService = new AddressBookService(Arrays.asList(contactArray));
		Assert.assertEquals(3, addressBookService.countEntries());
	}
}

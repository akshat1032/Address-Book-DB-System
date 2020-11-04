package com.capgemini.addressbooksystemdbtest;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.logging.Logger;

import org.json.JSONObject;
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

	// Getting contact from server
	public static Contact[] getContactList() {
		Response response = RestAssured.get("/contact");
		AddressBookService.log.info("Contact in server :\n" + response.asString());
		Contact[] arrayOfContacts = new Gson().fromJson(response.asString(), Contact[].class);
		return arrayOfContacts;
	}

	// Posting contact to server and returning response
	public Response addContactToJsonServer(Contact contactData) {
		String contactJson = new Gson().toJson(contactData);
		RequestSpecification request = RestAssured.given();
		request.header("Content-Type", "application/json");
		request.body(contactJson);
		return request.post("/contact");
	}

	// Adding new contact to server and populating object
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
	
	// Adding multiple contact to server and matching status code and count
	@Test
	public void givenListOfContacts_WhenAdded_MatchCount() {
		AddressBookService addressBookService;
		Contact[] contactArray = getContactList();
		addressBookService = new AddressBookService(Arrays.asList(contactArray));
		Contact[] contactArrays = {
				new Contact(4,"Kiba", "Inuzuka", "Inuzuka Clan House", "Konohagakure", "Land of Fire", 112345, 777856, "kibaakamaru@gmail.com",
						"Casual", "Friends", LocalDate.now()),
				new Contact(5,"Gaara", "Of The Sand", "Kazekage house", "Sunagakure", "Land of Wind", 111222, 6547894, "gaara_shukaku@magnetsand.com",
						"Personal", "VIP", LocalDate.now()),
				new Contact(6,"Shikamaru", "Nara", "Nara House", "Konohagakure", "Land of Fire", 333666, 9874563, "shadowmaster_lazy@nara.com",
						"Casual", "Friends", LocalDate.now())};
		for (Contact contact : contactArrays) {
			Response response = addContactToJsonServer(contact);
			int statusCode = response.getStatusCode();
			Assert.assertEquals(201, statusCode);
			contact = new Gson().fromJson(response.asString(), Contact.class);
			addressBookService.addContactToJSONServer(contact);
		}
		long entries = addressBookService.countEntries();
		Assert.assertEquals(6, entries);
	}
	
	// Retrieving contact from server and matching count
	@Test
	public void givenContactInJsonServer_WhenRetrived_MatchCount() {
		Contact[] contactArray = getContactList();
		AddressBookService addressBookService;
		addressBookService = new AddressBookService(Arrays.asList(contactArray));
		Assert.assertEquals(6, addressBookService.countEntries());
	}
	
	// Update contact to server and match status code
//	@Test
//	public void givenContact_WhenUpdated_MatchStatusCode() {
//		AddressBookService addressBookService;
//		Contact[] contactArray = getContactList();
//		addressBookService = new AddressBookService(Arrays.asList(contactArray));
//		addressBookService.updateContactDetailsForServer("Naurto", "Myoboku Mountain");
//		Contact contact = addressBookService.getContactData("Naruto");
//		String contactJson = new Gson().toJson(contact);
//		RequestSpecification request = RestAssured.given();
//		request.header("Content-Type", "application/json");
//		request.body(contactJson);
//		Response response = request.put("/contact/" + contact.firstName);
//		int statusCode = response.getStatusCode();
//		Assert.assertEquals(200, statusCode);
//	}
}

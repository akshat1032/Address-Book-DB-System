package com.capgemini.addressbookdbsystem;

import java.time.LocalDate;

public class Contact {

	public String firstName;
	public String lastName;
	public String address;
	public String city;
	public String state;
	public long zip;
	public long phone;
	public String email;
	public String addressBookName;
	public String addressBookType;
	public LocalDate dateAdded;

	// Adding all the contact information of contact table
	public Contact(String firstName, String lastName, String address, String city, String state, long zip,
			long phone, String email) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.address = address;
		this.city = city;
		this.state = state;
		this.zip = zip;
		this.phone = phone;
		this.email = email;
	}

	// Adding the information into address book table
	public Contact(String firstName, String lastName, String address, String city, String state, long zip,
			long phone, String email, String addressBookName, String addressBookType) {
		this(firstName, lastName, address, city, state, zip, phone, email);
		this.addressBookName = addressBookName;
		this.addressBookType = addressBookType;
	}
	
	// Adding the information into updated address book and contact table
	public Contact(String firstName, String lastName, String address, String city, String state, long zip,
			long phone, String email, String addressBookName, String addressBookType, LocalDate dateAdded) {
		this(firstName, lastName, address, city, state, zip, phone, email,addressBookName,addressBookType);
		this.dateAdded = dateAdded;
	}

	@Override
	public String toString() {
		return "Contact [firstName=" + firstName + ", lastName=" + lastName + ", address=" + address + ", city=" + city
				+ ", state=" + state + ", zip=" + zip + ", phone=" + phone + ", email=" + email
				+ ", addressBookName=" + addressBookName + ", addressBookType=" + addressBookType + "]";
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Contact that = (Contact) o;
		return firstName.equals(that.firstName) && address.equals(that.address);
	}
}

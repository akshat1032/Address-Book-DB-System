package com.capgemini.addressbookdbsystem;

public class Contact {

	public String firstName;
	public String lastName;
	public String address;
	public String city;
	public String state;
	public String zip;
	public String phoneNo;
	public String email;
	public String addressBookName;
	public String addressBookType;

	// Adding all the contact information of contact table
	public Contact(String firstName, String lastName, String address, String city, String state, String zip,
			String phoneNo, String email) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.address = address;
		this.city = city;
		this.state = state;
		this.zip = zip;
		this.phoneNo = phoneNo;
		this.email = email;
	}

	// Adding the information into address book table
	public Contact(String firstName, String lastName, String address, String city, String state, String zip,
			String phoneNo, String email, String addressBookName, String addressBookType) {
		this(firstName, lastName, address, city, state, zip, phoneNo, email);
		this.addressBookName = addressBookName;
		this.addressBookType = addressBookType;
	}

	@Override
	public String toString() {
		return "Contact [firstName=" + firstName + ", lastName=" + lastName + ", address=" + address + ", city=" + city
				+ ", state=" + state + ", zip=" + zip + ", phoneNo=" + phoneNo + ", email=" + email
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

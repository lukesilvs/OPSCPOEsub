package com.landmarkguideapp.diiscovery;

public class UserAccount
{
    public String firstName, lastName, emailAddress;

    // empty constructor
    public UserAccount() { }

    public UserAccount(String firstName, String lastName, String emailAddress)
    {
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
    }

}

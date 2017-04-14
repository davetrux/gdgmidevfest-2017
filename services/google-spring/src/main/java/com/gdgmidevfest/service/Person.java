package com.gdgmidevfest.service;

/**
 * Created by david on 12/22/14.
 */

import java.io.Serializable;


public class Person implements Serializable {
    private String FirstName;
    private String LastName;
    private String Gender;

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String _FirstName) {
        this.FirstName = _FirstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }
}

package com.reryde.app.Models;

import java.io.Serializable;

/**
 * Created by Tranxit Technologies Pvt Ltd. on 17-03-2018.
 */

public class ContactModel implements Serializable {
    String Name,PhoneNumber,ProfilePicture;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    private boolean isSelected;


    public ContactModel(String name,String phoneNumber,String ProfilePicture){
        this.Name = name;
        this.PhoneNumber = phoneNumber;
        this.ProfilePicture =ProfilePicture;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public String getProfilePicture() {
        return ProfilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        ProfilePicture = profilePicture;
    }
}

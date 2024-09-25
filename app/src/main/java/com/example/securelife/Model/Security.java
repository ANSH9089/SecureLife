package com.example.securelife.Model;

public class Security {
    private String name, phone, password, description, address,status,imageUrl;

    public Security() {
    }

    public Security(String imageUrl,String name, String phone, String password, String description, String address, String status) {
        this.name = name;
        this.phone = phone;
        this.password = password;
        this.description = description;
        this.address = address;
        this.status = status;
        this.imageUrl=imageUrl;
    }
    public String getImage() {
        return imageUrl;
    }

    public void setImage(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

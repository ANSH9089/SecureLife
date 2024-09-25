package com.example.securelife.Model;

public class Visitors {
    private String blockNo,arrival,departure,name,phone,reason,status;

    public Visitors() {
    }

    public Visitors(String blockNo, String arrival, String departure, String name, String phone, String reason,String status) {
        this.blockNo = blockNo;
        this.arrival = arrival;
        this.departure = departure;
        this.name = name;
        this.phone = phone;
        this.reason = reason;
        this.status=status;
    }

    public String getBlockNo() {
        return blockNo;
    }

    public void setBlockNo(String BlockNo) {
        this.blockNo = blockNo;
    }

    public String getArrival() {
        return arrival;
    }

    public void setArrival(String arrival) {
        this.arrival = arrival;
    }

    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
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

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

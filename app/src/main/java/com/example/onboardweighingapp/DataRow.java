package com.example.onboardweighingapp;

public class DataRow {
    private String date;
    private String time;
    private String sensorReadingStatus;
    private String weight;
    private String switchStatus;

    public DataRow(String date, String time, String weight, String switchStatus) {
        this.date = date;
        this.time = time;
        this.weight = weight;
        this.switchStatus = switchStatus;
    }

    // Getters
    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getSensorReadingStatus() {
        return sensorReadingStatus;
    }

    public String getWeight() {
        return weight;
    }

    public String getSwitchStatus() {
        return switchStatus;
    }
}

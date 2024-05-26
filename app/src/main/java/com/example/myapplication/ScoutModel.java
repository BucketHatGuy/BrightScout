package com.example.myapplication;

public class ScoutModel {
    private int dataID;
    private String name;
    private int teamScouted;
    private int qualNumber;
    private String robotPosition;

    public ScoutModel(int dataID, String name, int teamScouted, int qualNumber, String robotPosition) {
        this.dataID = dataID;
        this.name = name;
        this.teamScouted = teamScouted;
        this.qualNumber = qualNumber;
        this.robotPosition = robotPosition;
    }

    @Override
    public String toString() {
        return "scoutModel{" +
                "dataID=" + dataID +
                ", name='" + name + '\'' +
                ", teamScouted=" + teamScouted +
                ", qualNumber=" + qualNumber +
                ", robotPosition='" + robotPosition + '\'' +
                '}';
    }

    public int getDataID() {
        return dataID;
    }

    public void setDataID(int dataID) {
        this.dataID = dataID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTeamScouted() {
        return teamScouted;
    }

    public void setTeamScouted(int teamScouted) {
        this.teamScouted = teamScouted;
    }

    public int getQualNumber() {
        return qualNumber;
    }

    public void setQualNumber(int qualNumber) {
        this.qualNumber = qualNumber;
    }

    public String getRobotPosition() {
        return robotPosition;
    }

    public void setRobotPosition(String robotPosition) {
        this.robotPosition = robotPosition;
    }
}

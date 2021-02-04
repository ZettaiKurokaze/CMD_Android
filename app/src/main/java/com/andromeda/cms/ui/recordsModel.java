package com.andromeda.cms.ui;

import com.google.firebase.firestore.Exclude;

import java.util.Calendar;

public class recordsModel {
    String documentId, CourseID, Notes, RoutineID, UserEmail;
    String Building, Room, Date, Time, StartTime, EndTime, DateCompare;
    Calendar currentDate=Calendar.getInstance();

    public recordsModel() {

    }
    public recordsModel(String documentId,String CourseID,String Notes,String RoutineID,String UserEmail) {
        this.documentId=documentId;
        this.CourseID=CourseID;
        this.Notes=Notes;
        this.RoutineID=RoutineID;
        this.UserEmail=UserEmail;
    }

    public void generateVariables(){
        Building=documentId.substring(0,1);
        Room=documentId.substring(2,5);
        Date=documentId.substring(6,16);
        Time=documentId.substring(17,18);
        StartTime=getStartTimeValue(Time);
        EndTime=getEndTimeValue(Time);
        DateCompare=Date.substring(6,10)+Date.substring(3,5)+Date.substring(0,2);
    }


    public void generateDate() {
        currentDate.set(Calendar.DAY_OF_MONTH,Integer.parseInt(Date.substring(0,2)));
        currentDate.set(Calendar.MONTH,Integer.parseInt(Date.substring(3,5))-1);
        currentDate.set(Calendar.YEAR,Integer.parseInt(Date.substring(6,10)));
        currentDate.set(Calendar.HOUR,Integer.parseInt(StartTime.substring(0,2)));
        currentDate.set(Calendar.MINUTE,Integer.parseInt(StartTime.substring(3,5)));

        if(Integer.parseInt(Time)<4)
            currentDate.set(Calendar.AM_PM, Calendar.AM);
        else
            currentDate.set(Calendar.AM_PM, Calendar.PM);
    }

    public void generateDocId(){
        documentId=Building+"-"+Room+"-"+Date+"-"+Time;
    }

    public Calendar getCurrentDate() { return currentDate; }

    public void setCurrentDate(Calendar currentDate) { this.currentDate = currentDate; }

    @Exclude
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getCourseID() {
        return CourseID;
    }

    public void setCourseID(String courseID) {
        CourseID = courseID;
    }

    public String getNotes() {
        return Notes;
    }

    public void setNotes(String notes) {
        Notes = notes;
    }

    public String getRoutineID() {
        return RoutineID;
    }

    public void setRoutineID(String routineID) {
        RoutineID = routineID;
    }

    public String getUserEmail() {
        return UserEmail;
    }

    public void setUserEmail(String userEmail) {
        UserEmail = userEmail;
    }

    public String getBuilding() { return Building; }

    public String getRoom() { return Room; }

    public String getDate() { return Date; }

    public String getTime() { return Time; }

    public String getStartTime() { return StartTime; }

    public String getEndTime() { return EndTime; }

    public String getDateCompare() { return DateCompare; }

    public void setDateCompare(String dateCompare) { DateCompare = dateCompare; }

    public void setBuilding(String building) {
        Building = building;
    }

    public void setRoom(String room) {
        Room = room;
    }

    public void setDate(String date) {
        Date = date;
    }

    public void setTime(String time) {
        Time = time;
    }

    public void setStartTime() {
        StartTime=getStartTimeValue(Time);
    }

    public void setEndTime() {
        EndTime=getEndTimeValue(Time);
    }

    public String getStartTimeValue(String Time){
        switch (Integer.valueOf(Time)){
            case 1:
                return("09:00");
            case 2:
                return("10:00");
            case 3:
                return("11:00");
            case 4:
                return("12:00");
            case 5:
                return("02:10");
            case 6:
                return("03:10");
            case 7:
                return("04:15");
            case 8:
                return("05:15");
            default:
                return("");
        }
    }

    public String getEndTimeValue(String Time){
        switch (Integer.parseInt(Time)){
            case 1:
                return("09:50");
            case 2:
                return("10:50");
            case 3:
                return("11:50");
            case 4:
                return("12:50");
            case 5:
                return("03:00");
            case 6:
                return("04:00");
            case 7:
                return("05:05");
            case 8:
                return("06:05");
            default:
                return("");
        }
    }
}

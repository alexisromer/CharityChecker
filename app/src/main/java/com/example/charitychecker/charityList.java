package com.example.charitychecker;

public class charityList {
    private String name;
    private String tagLine;
    private String cause;
    private String address;


    public charityList(String n, String t, String c, String a){
        this.name = n;
        this.tagLine = t;
        this.cause = c;
        this.address = a;

    }

    public String getName(){ return name; }

    public void setName(String name){ this.name = name; }

    public String getTagline(){ return tagLine; }

    public void setTagLine(String tagLine){ this.tagLine = tagLine; }

    public String getCause(){ return cause; }

    public void setCause(String cause){ this.cause = cause; }

    public String getAddress(){ return address; }

    public void setAddress(String address){ this.address = address; }

}

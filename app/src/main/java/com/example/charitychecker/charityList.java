package com.example.charitychecker;

public class charityList {
    private String EIN;
    private String name;
    private String tagLine;
    private String cause;
    private String address;
    private String donateURL;



    public charityList(String e, String n, String t, String c, String a, String d){
        this.EIN = e;
        this.name = n;
        this.tagLine = t;
        this.cause = c;
        this.address = a;
        this.donateURL = d;
    }

    public charityList(){
        this.EIN = "null";
        this.name = "null";
        this.tagLine = "null";
        this.cause = "null";
        this.address = "null";
        this.donateURL = "null";
    }

    public String getName(){ return name; }
    public void setName(String name){ this.name = name; }

    public String getTagline(){ return tagLine; }
    public void setTagLine(String tagLine){ this.tagLine = tagLine; }

    public String getCause(){ return cause; }
    public void setCause(String cause){ this.cause = cause; }

    public String getAddress(){ return address; }
    public void setAddress(String address){ this.address = address; }

    public String getEIN(){ return EIN; }
    public void setEIN(String ein){ this.EIN = ein; }

    public String getDonateURL(){ return donateURL; }
    public void setDonateURL(String url){ this.donateURL = url; }
}

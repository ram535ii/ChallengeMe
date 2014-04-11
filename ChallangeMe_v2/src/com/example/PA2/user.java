package com.example.PA2;

/**
 * Created by darylrodrigo on 26/03/2014.
 */
public class user {
    private String username, location;
    private int age;

    private boolean ski, random, bike;

    public user(String _username, int _age, String _location){
        username = _username;
        location = _location;
        age = _age;

        ski = true;
        random = true;
        bike = true;
    }

    public String getName()
    {
        //include validation, logic, logging or whatever you like here
        return this.username;
    }
    public void setName(String value)
    {
        //include more logic
        this.username = value;
    }

    public int getAge()
    {
        //include validation, logic, logging or whatever you like here
        return this.age;
    }
    public void setAge(int value)
    {
        //include more logic
        this.age = value;
    }

    public String getLocation()
    {
        //include validation, logic, logging or whatever you like here
        return this.location;
    }
    public void setLocation(String value)
    {
        //include more logic
        this.location = value;
    }

    // Interests
    public Boolean getSki()
    {
        return this.ski;
    }
    public void setSki(Boolean value)
    {
        this.ski = value;
    }


    public Boolean getRandom()
    {
        return this.random;
    }

    public void setRandom(Boolean value)
    {
        this.random = value;
    }


    public Boolean getBike()
    {
        return this.bike;
    }
    public void setBike(Boolean value)
    {
        this.bike = value;
    }


}

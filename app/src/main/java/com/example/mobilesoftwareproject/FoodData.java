package com.example.mobilesoftwareproject;

public class FoodData {
    int ID;
    String foodName;
    String Impression;
    int Cost;
    String Location;
    int Calorie;
    String Photo;
    String Type;
    String Time;


    public FoodData(int ID,String foodName,String Impression,int Cost,String Location,int Calorie,String Photo,String Type,String Time) {
        this.ID = ID;
        this.foodName = foodName;
        this.Impression = Impression;
        this.Cost = Cost;
        this.Location = Location;
        this.Calorie = Calorie;
        this.Photo = Photo;
        this.Type = Type;
        this.Time = Time;
    }

    public int getID() {
        return ID;
    }

    public String getImpression() {
        return Impression;
    }

    public String getLocation() {
        return Location;
    }

    public String getPhoto() {
        return Photo;
    }

    public String getFoodName() {
        return foodName;
    }

    public int getCost() {
        return Cost;
    }

    public int getCalorie() {
        return Calorie;
    }

    public String getType() {
        return Type;
    }

    public String getTime() {
        return Time;
    }
}

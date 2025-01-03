package com.georgian.flag;

public class MyDataModel {

    private int id;
    private String name;
    private String campus;
    private String imageUrl;
    private String birthdate;
    private String bio;
    private String gender;
    private String preferred_gender;
    private int fromage;
    private int toage;

    public MyDataModel(int id, String name, String campus, String imageUrl, String birthdate, String bio, String gender, String preferred_gender, int fromage, int toage) {
        this.id = id;
        this.name = name;
        this.campus = campus;
        this.imageUrl = imageUrl;
        this.birthdate = birthdate;
        this.bio = bio;
        this.gender = gender;
        this.preferred_gender = preferred_gender;
        this.fromage = fromage;
        this.toage = toage;
    }


    public int getId() { return id; }
    public String getName() {
        return name;
    }

    public String getCampus() {
        return campus;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public String getBio() {
        return bio;
    }

    public String getGender() {
        return gender;
    }

    public String getPreferredGender() {
        return preferred_gender;
    }

    public int getFromAge() {
        return fromage;
    }

    public void setFromAge(int fromage) {
        this.fromage = fromage;
    }

    public int getToAge() {
        return toage;
    }

    public void setToAge(int toage) {
        this.toage = toage;
    }
}

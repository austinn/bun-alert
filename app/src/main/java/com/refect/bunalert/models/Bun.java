package com.refect.bunalert.models;

import com.refect.bunalert.utils.Utils;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by austinn on 8/2/2017.
 */

public class Bun {

    private String name;
    private String message;
    private ArrayList<Double> location;
    private String zipCode;
    private String imageUrl;
    private long expiration;
    private String size;

    public Bun() {

    }

    public Bun(String name, String message, ArrayList<Double> location, String zipCode, String imageUrl) {
        setName(name);
        setMessage(message);
        setLocation(location);
        setZipCode(zipCode);
        setImageUrl(imageUrl);

        setExpiration(Utils.addMinutesToDate(5, new Date()).getTime());
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getExpiration() {
        return expiration;
    }

    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }

    public ArrayList<Double> getLocation() {
        return location;
    }

    public void setLocation(ArrayList<Double> location) {
        this.location = location;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}

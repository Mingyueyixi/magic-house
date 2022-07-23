package com.lu.magic.bean;

public class AMapConfig extends BaseConfig {
    private double lat;
    private double lng;
    private String name;

    public AMapConfig() {
        this(0, 0);
    }

    public AMapConfig(double lan, double lng) {
        super(false);
        this.lat = lan;
        this.lng = lng;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

}

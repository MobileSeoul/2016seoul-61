package com.example.lee.playinseoul.model;

/**
 * Created by LEE on 2016-08-12.
 */
public class Tour {
    public static int totalCount;

    private String addr1;
    private String firstimage;
    private String title;
    private String mapx;
    private String mapy;
    private String contentId;
    private String contenttypeid;

    public  Tour(){this.title = null;};

    public Tour(String addr1, String firstimage, String title, String mapx, String mapy, String contentId, String contenttypeid) {
        this.addr1 = addr1;
        this.firstimage = firstimage;
        this.title = title;
        this.mapx = mapx;
        this.mapy = mapy;
        this.contentId = contentId;
        this.contenttypeid = contenttypeid;

    }

    public String getAddr1() {
        return addr1;
    }

    public void setAddr1(String addr1) {
        this.addr1 = addr1;
    }

    public String getFirstimage() {
        return firstimage;
    }

    public void setFirstimage(String firstimage) {
        this.firstimage = firstimage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMapx() {
        return mapx;
    }

    public void setMapx(String mapx) {
        this.mapx = mapx;
    }

    public String getMapy() {
        return mapy;
    }

    public void setMapy(String mapy) {
        this.mapy = mapy;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public String getContenttypeid() {
        return contenttypeid;
    }

    public void setContenttypeid(String contenttypeid) {
        this.contenttypeid = contenttypeid;
    }
}

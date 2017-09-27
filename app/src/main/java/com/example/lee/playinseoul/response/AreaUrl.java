package com.example.lee.playinseoul.response;

/**
 * Created by LEE on 2016-08-14.
 */
public class AreaUrl {


    private String ServiceKey = "9KGRFCDMxCyymB9RuJTsFQycV1fYFsmFdY%2B9KqJOJajPElaXW6x%2B7pUb6Cq9%2BGTV%2B9h8f76Ejsr2R9eQaEy0sg%3D%3D";
    private int page;
    private int tema;
    private int sigu;


    public AreaUrl(int page, int tema, int sigu) {
        this.page = page;
        this.tema = tema;
        this.sigu = sigu;
    }

    public String getAreaUrl(){
        String queryUrl = "http://api.visitkorea.or.kr/openapi/service/rest/KorService/areaBasedList"
                + "?ServiceKey=" + ServiceKey
                + "&numOfRows=10"
                + "&pageNo="+page
                + "&arrange=A"
                + "&listYN=Y"
                + "&contentTypeId=" + tema
                + "&areaCode=1"
                + "&sigunguCode=" + sigu
                + "&MobileOS=ETC"
                + "&MobileApp=INU";

        return  queryUrl;
    }
}

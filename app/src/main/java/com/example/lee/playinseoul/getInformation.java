package com.example.lee.playinseoul;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 *
 * 콘텐츠 아이디 정리
 *
 * 관광지 -- 12
 * 문화시설 -- 14
 * 축제 공연 행사 -- 15
 * 여행코스 - 25
 * 레포츠 - 28
 * 숙박 - 32
 * 쇼핑 - 38
 * 음식  - 39
 *
 * 시군구 코드 정리
 * areacode 1 -- 서울 -> 이것만 쓸거임.
 *
 * 강남구 - 1
 * 강동구 - 2
 * 강북구 - 3
 * 강서구 - 4
 * 관악구 - 5
 * 광진구 - 6
 * 구로구 - 7
 * 금천구 - 8
 * 노원구 - 9
 * 도봉구 - 10
 * 동대문구 - 11
 * 동작구 - 12
 * 마포구 - 13
 * 서대문구 - 14
 * 서초구 - 15
 * 성동구 - 16
 * 성북구 - 17
 * 송파구 - 18
 * 양천구 - 19
 * 영등포구 - 20
 * 용산구 - 21
 * 은평구 - 22
 * 종로구 - 23
 * 중구 - 24
 * 중랑구 - 25
 *
 */



class dataSet
{
    String addr1;
    String areacode;
    String cat1;
    String cat2;
    String cat3;
    String contentid;
    String firstimage;
    String firstimage2;
    String mapx;
    String mapy;
    String mlevel;
    String readcount;
    String sigungucode;
    String title;
    String tel;
}

class partydataSet extends dataSet // 행사정보의 데이터 클래스, event시작일과 종료일을 추가로 갖는다.
{
    String eventstartdate;
    String eventenddate;
}

class moreDataSet
{
    String homepage;
    String overview; //개요
    String telname; // 전화번호명(부서명)
    String tel; // 전화번호
    String title;
    String addr1;
    String addr2;
    String cat3;
}



public class getInformation {
    String ServiceKey="9KGRFCDMxCyymB9RuJTsFQycV1fYFsmFdY%2B9KqJOJajPElaXW6x%2B7pUb6Cq9%2BGTV%2B9h8f76Ejsr2R9eQaEy0sg%3D%3D";


    String getTotalNum(int contentTypeId,int sigunguCode,int pageNo)
    {
        String queryUrl = "http://api.visitkorea.or.kr/openapi/service/rest/KorService/areaBasedList"
                +"?ServiceKey="+ServiceKey
                +"&numOfRows=10"
                +"&pageNo="+pageNo
                +"&arrange=A"
                +"&listYN=Y"
                +"&contentTypeId="+contentTypeId
                +"&areaCode=1"
                +"&sigunguCode="+sigunguCode
                +"&MobileOS=ETC"
                +"&MobileApp=INU";

        String totalCount="";

        try {
            URL url = new URL(queryUrl);
            InputStream is = url.openStream();
            Log.d("opend","totalNum!!");

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser(); // 파싱하는 객체

            xpp.setInput(new InputStreamReader(is, "UTF-8")); // inputstream으로부터 xml 입력받기

            String tag = "";

            xpp.next();
            int eventType = xpp.getEventType();


            while(eventType!=XmlPullParser.END_DOCUMENT) {

                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG: // Start tag인지점을 구분하여 객체에 그 정보 값을 넣어준다.
                        tag = xpp.getName(); // 태그의 이름을 얻어온다( item, contentid 등등)
                        break;
                    case XmlPullParser.TEXT:
                        if(tag.equals("totalCount"))
                        {
                            totalCount=xpp.getText();
                            xpp.next();
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                eventType=xpp.next();
            }

        }catch(Exception e)
        {
            e.printStackTrace();
        }

        return totalCount;
    }


    //일반적인 관광정보를 가져오는 함수.
    ArrayList<dataSet> getXmlData(int contID,int sigunCode,int pNo,int numOfRows)
    {
        ArrayList<dataSet> dataSets = new ArrayList<dataSet>(); //data 결과를 받은 arrayList;.

        int contentTypeId=contID; // 문화 관광 레저등 테마를 집어넣는다.
        int sigunguCode=sigunCode; // 구의 코드를 집어넣는다.
        int pageNo=pNo; // 몇번째 페이지를 보여줄거냐
        int numRows=numOfRows;


        String queryUrl = "http://api.visitkorea.or.kr/openapi/service/rest/KorService/areaBasedList"
        +"?ServiceKey="+ServiceKey
                +"&numOfRows="+numRows
                +"&pageNo="+pageNo
                +"&arrange=A"
                +"&listYN=Y"
                +"&contentTypeId="+contentTypeId
                +"&areaCode=1"
                +"&sigunguCode="+sigunguCode
                +"&MobileOS=ETC"
                +"&MobileApp=INU";

        try {
            URL url = new URL(queryUrl);
            InputStream is = url.openStream();
            Log.d("opend","xmlDataJoin!!");

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser(); // 파싱하는 객체

            xpp.setInput(new InputStreamReader(is, "UTF-8")); // inputstream으로부터 xml 입력받기

            String tag="";
            dataSet tdata = new dataSet(); // data를 저장하기 위한 변수


            xpp.next();
            int eventType=xpp.getEventType();


            while(eventType!=XmlPullParser.END_DOCUMENT)
            {

                switch(eventType)
                {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG: // Start tag인지점을 구분하여 객체에 그 정보 값을 넣어준다.
                        tag=xpp.getName(); // 태그의 이름을 얻어온다( item, contentid 등등)
                        break;

                    case XmlPullParser.TEXT:

                        if(tag.equals("item"));
                        else if(tag.equals("addr1"))
                        {
                            tdata.addr1=xpp.getText();
                            xpp.next();
                        }
                        else if(tag.equals("areacode"))
                        {
                            tdata.areacode=xpp.getText();
                            xpp.next();
                        }
                        else if(tag.equals("cat1"))
                        {
                            tdata.cat1=xpp.getText();
                            xpp.next();
                        }
                        else if(tag.equals("cat2"))
                        {
                            tdata.cat2=xpp.getText();
                            xpp.next();
                        }
                        else if(tag.equals("cat3"))
                        {
                            tdata.cat3=xpp.getText();
                            xpp.next();
                        }
                        else if(tag.equals("contentid"))
                        {
                            tdata.contentid=xpp.getText();
                            xpp.next();
                        }
                        else if(tag.equals("firstimage"))
                        {

                            tdata.firstimage=xpp.getText();
                            xpp.next();
                        }
                        else if(tag.equals("firstimage2"))
                        {
                            tdata.firstimage2=xpp.getText();
                            xpp.next();
                        }
                        else if(tag.equals("mapx"))
                        {
                            tdata.mapx=xpp.getText();
                            xpp.next();
                        }
                        else if(tag.equals("mapy"))
                        {
                            tdata.mapy=xpp.getText();
                            xpp.next();
                        }
                        else if(tag.equals("mlevel"))
                        {
                            tdata.mlevel=xpp.getText();
                            xpp.next();
                        }
                        else if(tag.equals("readcount"))
                        {
                            tdata.readcount=xpp.getText();
                            xpp.next();
                        }
                        else if(tag.equals("sugungucode"))
                        {
                            tdata.sigungucode=xpp.getText();
                            xpp.next();
                        }
                        else if(tag.equals("title"))
                        {
                            tdata.title=xpp.getText();
                            xpp.next();
                        }
                        else if(tag.equals("tel"))
                        {
                            tdata.tel=xpp.getText();
                            xpp.next();
                        }

                        break;

                    case XmlPullParser.END_TAG: // end tag 일때
                        tag = xpp.getName();

                        if(tag.equals("item"))
                        {
                            dataSets.add(tdata);
                            tdata=new dataSet();
                        }
                        break;
                } // switch 끝

                eventType = xpp.next();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return dataSets;

    }// 일반적인 관광정보를 가져옴



    ArrayList<partydataSet> getpartyXmldata()
    {
        ArrayList<partydataSet> dataSets = new ArrayList<partydataSet>(); //data 결과를 받은 arrayList;.

        String eventStartDate="";

        DateFormat df1 = new SimpleDateFormat("yyyyMMdd");
        Calendar calendar = Calendar.getInstance();
        eventStartDate=df1.format(calendar.getTime());

        String queryUrl = "http://api.visitkorea.or.kr/openapi/service/rest/KorService/searchFestival"
                +"?ServiceKey="+ServiceKey
                +"&numOfRows=999"
                +"&pageNo="+"1"
                +"&arrange=A"
                +"&listYN=Y"
                +"&eventStartDate="+eventStartDate
                +"&MobileOS=ETC"
                +"&MobileApp=INU";

        try {
            URL url = new URL(queryUrl);
            InputStream is = url.openStream();

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser(); // 파싱하는 객체

            xpp.setInput(new InputStreamReader(is, "UTF-8")); // inputstream으로부터 xml 입력받기

            String tag="";
            partydataSet tdata = new partydataSet(); // data를 저장하기 위한 변수


            xpp.next();
            int eventType=xpp.getEventType();


            while(eventType!=XmlPullParser.END_DOCUMENT)
            {

                switch(eventType)
                {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG: // Start tag인지점을 구분하여 객체에 그 정보 값을 넣어준다.
                        tag=xpp.getName(); // 태그의 이름을 얻어온다( item, contentid 등등)
                        break;

                    case XmlPullParser.TEXT:

                        if(tag.equals("item"));
                        else if(tag.equals("addr1"))
                        {

                            tdata.addr1=xpp.getText();
                            xpp.next();
                        }
                        else if(tag.equals("areacode"))
                        {
                            tdata.areacode=xpp.getText();
                            xpp.next();
                        }
                        else if(tag.equals("cat1"))
                        {
                            tdata.cat1=xpp.getText();
                            xpp.next();
                        }
                        else if(tag.equals("cat2"))
                        {
                            tdata.cat2=xpp.getText();
                            xpp.next();
                        }
                        else if(tag.equals("cat3"))
                        {
                            tdata.cat3=xpp.getText();
                            xpp.next();
                        }
                        else if(tag.equals("contentid"))
                        {
                            tdata.contentid=xpp.getText();
                            xpp.next();
                        }
                        else if(tag.equals("eventstartdate"))
                        {
                            tdata.eventstartdate=xpp.getText();
                            xpp.next();
                        }
                        else if(tag.equals("eventenddate"))
                        {
                            tdata.eventenddate=xpp.getText();
                            xpp.next();
                        }
                        else if(tag.equals("firstimage"))
                        {

                            tdata.firstimage=xpp.getText();
                            xpp.next();
                        }
                        else if(tag.equals("firstimage2"))
                        {
                            tdata.firstimage2=xpp.getText();
                            xpp.next();
                        }
                        else if(tag.equals("mapx"))
                        {
                            tdata.mapx=xpp.getText();
                            xpp.next();
                        }
                        else if(tag.equals("mapy"))
                        {
                            tdata.mapy=xpp.getText();
                            xpp.next();
                        }
                        else if(tag.equals("mlevel"))
                        {
                            tdata.mlevel=xpp.getText();
                            xpp.next();
                        }
                        else if(tag.equals("readcount"))
                        {
                            tdata.readcount=xpp.getText();
                            xpp.next();
                        }
                        else if(tag.equals("sugungucode"))
                        {
                            tdata.sigungucode=xpp.getText();
                            xpp.next();
                        }
                        else if(tag.equals("title"))
                        {
                            tdata.title=xpp.getText();
                            xpp.next();
                        }
                        else if(tag.equals("tel"))
                        {
                            tdata.tel=xpp.getText();
                            xpp.next();
                        }
                        break;

                    case XmlPullParser.END_TAG: // end tag 일때
                        tag = xpp.getName();

                        if(tag.equals("item"))
                        {
                            if(tdata.areacode.equals("1")) {
                                dataSets.add(tdata);
                                tdata = new partydataSet();
                            }
                        }
                        break;
                } // switch 끝

                eventType = xpp.next();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return dataSets;

    }



   //더 많은 이미지 정보를 조회 한다.
    ArrayList<String> getMoreImage(int contType,int contId)
    {
        ArrayList<String> dataSets = new ArrayList<String>(); //data 결과를 받은 arrayList;.


        String queryUrl = "http://api.visitkorea.or.kr/openapi/service/rest/KorService/detailImage"
                +"?ServiceKey="+ServiceKey
                +"&contentId="+contId
                +"&contentTypeId="+contType
                +"&arrange=A"
                +"&listYN=Y"
                +"&numOfRows=999"
                +"&pageNo=1"
                +"&MobileOS=ETC"
                +"&MobileApp=INU";

        try {
            URL url = new URL(queryUrl);
            InputStream is = url.openStream();

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser(); // 파싱하는 객체

            xpp.setInput(new InputStreamReader(is, "UTF-8")); // inputstream으로부터 xml 입력받기

            String tag="";
            String tdata = new String();


            xpp.next();
            int eventType=xpp.getEventType();


            while(eventType!=XmlPullParser.END_DOCUMENT)
            {

                switch(eventType)
                {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG: // Start tag인지점을 구분하여 객체에 그 정보 값을 넣어준다.
                        tag=xpp.getName(); // 태그의 이름을 얻어온다( item, contentid 등등)
                        break;

                    case XmlPullParser.TEXT:

                        if(tag.equals("item"));
                        else if(tag.equals("originimgurl"))
                        {
                            tdata=xpp.getText();
                            xpp.next();
                        }

                        break;

                    case XmlPullParser.END_TAG: // end tag 일때
                        tag = xpp.getName();

                        if(tag.equals("item"))
                        {
                            Log.d("tag",tdata);

                            dataSets.add(tdata);
                            tdata = new String();

                        }
                        break;
                } // switch 끝

                eventType = xpp.next();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return dataSets;

    }



    moreDataSet getSpecificData(int contId)
    {
        int contentId = contId; // 콘텐트 아이디를 집어 넣는다.

        moreDataSet tdata = new moreDataSet(); // data를 저장하기 위한 변수

        String queryUrl = "http://api.visitkorea.or.kr/openapi/service/rest/KorService/detailCommon"
                +"?ServiceKey="+ServiceKey
                +"&contentId="+contentId
                +"&defaultYN=Y"
                +"&overviewYN=Y"
                +"&addrinfoYN=Y"
                +"&catcodeYN=Y"
                +"&MobileOS=ETC"
                +"&MobileApp=INU"
                +"&numOfRows=999&pageNo=1";

        try {
            URL url = new URL(queryUrl);
            InputStream is = url.openStream();

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser(); // 파싱하는 객체

            xpp.setInput(new InputStreamReader(is, "UTF-8")); // inputstream으로부터 xml 입력받기

            String tag="";



            xpp.next();
            int eventType=xpp.getEventType();


            while(eventType!=XmlPullParser.END_DOCUMENT)
            {

                switch(eventType)
                {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG: // Start tag인지점을 구분하여 객체에 그 정보 값을 넣어준다.
                        tag=xpp.getName(); // 태그의 이름을 얻어온다( item, contentid 등등)
                        break;

                    case XmlPullParser.TEXT:

                        if(tag.equals("item"));
                        else if(tag.equals("homepage"))
                        {
                            tdata.homepage=xpp.getText();
                            xpp.next();
                        }
                        else if(tag.equals("overview"))
                        {
                            tdata.overview=xpp.getText();
                            xpp.next();
                        }
                        else if(tag.equals("telname"))
                        {
                            tdata.telname=xpp.getText();
                            xpp.next();
                        }
                        else if(tag.equals("tel"))
                        {
                            tdata.tel=xpp.getText();
                            xpp.next();
                        }
                        else if(tag.equals("title"))
                        {
                            tdata.title=xpp.getText();
                            xpp.next();
                        }
                        else if(tag.equals("cat3"))
                        {
                            tdata.cat3=xpp.getText();
                            xpp.next();
                        }
                        else if(tag.equals("addr1"))
                        {
                            tdata.addr1=xpp.getText();
                            xpp.next();
                        }
                        else if(tag.equals("addr2"))
                        {
                            tdata.addr2=xpp.getText();
                            xpp.next();
                        }
                        break;

                    case XmlPullParser.END_TAG: // end tag 일때
                        tag = xpp.getName();
                        break;
                } // switch 끝

                eventType = xpp.next();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return tdata;

    }// 일반적인 관광정보를 가져옴




}

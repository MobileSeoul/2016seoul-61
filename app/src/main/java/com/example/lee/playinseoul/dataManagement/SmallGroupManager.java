package com.example.lee.playinseoul.dataManagement;

/**
 * Created by Hong on 2016-09-20.
 */
public class SmallGroupManager {
    public static String getGroupName(String code) {
        switch(code) {
            case "A01010100": return "국립공원";
            case "A01010200": return "도립공원";
            case "A01010300": return "군립공원";
            case "A01010400": return "산";
            case "A01010500": return "자연생태관광지";
            case "A01010600": return "자연휴양림";
            case "A01010700": return "수목원";
            case "A01010800": return "폭포";
            case "A01010900": return "계곡";
            case "A01011000": return "약수터";
            case "A01011100": return "해안절경";
            case "A01011200": return "해수욕장";
            case "A01011300": return "섬";
            case "A01011400": return "항구/포구";
            case "A01011500": return "어촌";
            case "A01011600": return "등대";
            case "A01011700": return "호수";
            case "A01011800": return "강";
            case "A01011900": return "동굴";

            case "A02010100": return "고궁";
            case "A02010200": return "성";
            case "A02010300": return "문";
            case "A02010400": return "고택";
            case "A02010500": return "생가";
            case "A02010600": return "민속마을";
            case "A02010700": return "유적지/사적지";
            case "A02010800": return "사찰";
            case "A02010900": return "종교성지";
            case "A02011000": return "안보관광";

            case "A02020100": return "유원지";
            case "A02020200": return "관광단지";
            case "A02020300": return "온천/욕장/스파";
            case "A02020400": return "이색찜질방";
            case "A02020500": return "헬스투어";
            case "A02020600": return "테마공원";
            case "A02020700": return "공원";
            case "A02020800": return "유람선/잠수함관광";

            case "A02030100": return "농.산.어촌 체험";
            case "A02030200": return "전통체험";
            case "A02030300": return "산사체험";
            case "A02030400": return "이색체험";
            case "A02030500": return "관광농원";
            case "A02030600": return "이색거리";

            case "A02040100": return "제철소";
            case "A02040200": return "조선소";
            case "A02040300": return "공단";
            case "A02040400": return "발전소";
            case "A02040500": return "광산";
            case "A02040600": return "식음료";
            case "A02040700": return "화학/금속";
            case "A02040800": return "기타";
            case "A02040900": return "전자/반도체";
            case "A02041000": return "자동차";

            case "A02050100": return "다리/대교";
            case "A02050200": return "기념탑/기념비/전망대";
            case "A02050300": return "분수";
            case "A02050400": return "동상";
            case "A02050500": return "터널";
            case "A02050600": return "유명건물";

            case "A02060100": return "박물관";
            case "A02060200": return "기념관";
            case "A02060300": return "전시관";
            case "A02060400": return "컨벤션센터";
            case "A02060500": return "미술관/화랑";
            case "A02060600": return "공연장";
            case "A02060700": return "문화원";
            case "A02060800": return "외국문화원";
            case "A02060900": return "도서관";
            case "A02061000": return "대형서점";
            case "A02061100": return "문화전수시설";
            case "A02061200": return "영화관";
            case "A02061300": return "어학당";
            case "A02061400": return "학교";

            case "A02070100": return "문화관광축제";
            case "A02070200": return "일반축제";

            case "A02080100": return "전통공연";
            case "A02080200": return "연극";
            case "A02080300": return "뮤지컬";
            case "A02080400": return "오페라";
            case "A02080500": return "전시회";
            case "A02080600": return "박람회";
            case "A02080700": return "컨벤션";
            case "A02080800": return "무용";
            case "A02080900": return "클래식음악회";
            case "A02081000": return "대중콘서트";
            case "A02081100": return "영화";
            case "A02081200": return "스포츠경기";
            case "A02081300": return "기타행사";

            case "C01120001": return "가족코스";
            case "C01130001": return "나홀로코스";
            case "C01140001": return "힐링코스";
            case "C01150001": return "도보코스";
            case "C01160001": return "캠핑코스";
            case "C01170001": return "맛코스";

            case "A03010100": return "육상레포츠";
            case "A03010200": return "수상레포츠";
            case "A03010300": return "항공레포츠";

            case "A03020100": return "스포츠센터";
            case "A03020200": return "수련시설";
            case "A03020300": return "경기장";
            case "A03020400": return "인라인";
            case "A03020500": return "자전거하이킹";
            case "A03020600": return "카트";
            case "A03020700": return "골프";
            case "A03020800": return "경마";
            case "A03020900": return "경륜";
            case "A03021000": return "카지노";
            case "A03021100": return "승마";
            case "A03021200": return "스키/스노보드";
            case "A03021300": return "스케이트";
            case "A03021400": return "썰매장";
            case "A03021500": return "수렵장";
            case "A03021600": return "사격장";
            case "A03021700": return "야영장/오토캠핑장";
            case "A03021800": return "암벽등반";
            case "A03021900": return "빙벽등반";
            case "A03022000": return "서바이벌게임";
            case "A03022100": return "ATV";
            case "A03022200": return "MTB";
            case "A03022300": return "오프로드";
            case "A03022400": return "번지점프";
            case "A03022500": return "자동차경주";
            case "A03022600": return "스키(보드) 렌탈샵";
            case "A03022700": return "트래킹";

            case "A03030100": return "윈드서핑/제트스키";
            case "A03030200": return "카약/카누";
            case "A03030300": return "요트";
            case "A03030400": return "스노쿨링/스킨스쿠버다이빙";
            case "A03030500": return "민물낚시";
            case "A03030600": return "바다낚시";
            case "A03030700": return "수영";
            case "A03030800": return "레프팅";

            case "A03040100": return "스카이다이빙";
            case "A03040200": return "초경량비행";
            case "A03040300": return "헹글라이딩/패러글라이딩";
            case "A03040400": return "열기구";

            case "A03050100": return "복합 레포츠";

            case "B02010100": return "관광호텔";
            case "B02010200": return "수상관광호텔";
            case "B02010300": return "전통호텔";
            case "B02010400": return "가족호텔";
            case "B02010500": return "콘도미니엄";
            case "B02010600": return "유스호스텔";
            case "B02010700": return "펜션";
            case "B02010800": return "여관";
            case "B02010900": return "모텔";
            case "B02011000": return "민박";
            case "B02011100": return "게스트하우스";
            case "B02011200": return "홈스테이";
            case "B02011300": return "서비스레지던스";
            case "B02011400": return "의료관광호텔";
            case "B02011500": return "소형호텔";
            case "B02011600": return "한옥스테이";

            case "A04010100": return "5일장";
            case "A04010200": return "상설시장";
            case "A04010300": return "백화점";
            case "A04010400": return "면세점";
            case "A04010500": return "할인매장";
            case "A04010600": return "전문상가";
            case "A04010700": return "공예/공방";
            case "A04010800": return "관광기념품점";
            case "A04010900": return "특산물판매점";

            case "A05020100": return "한식";
            case "A05020200": return "서양식";
            case "A05020300": return "일식";
            case "A05020400": return "중식";
            case "A05020500": return "아시아식";
            case "A05020600": return "패밀리레스토랑";
            case "A05020700": return "이색음식점";
            case "A05020800": return "채식전문점";
            case "A05020900": return "바/까페";
            case "A05021000": return "클럽";

            default: return "";
        }
    }
}

package com.green.webclient.timetable.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class TimetableParam {
    private String sdSchulCode; // 학교코드
    private String ay; // 년도
    private int sem; // 학기
    private String dghtCrseScNm; //주간, 야간
    private String allTiYmd; // 일자
    private int grade; //학년
    private String classNm; // 반
}

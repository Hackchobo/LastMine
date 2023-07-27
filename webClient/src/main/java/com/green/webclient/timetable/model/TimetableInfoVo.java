package com.green.webclient.timetable.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.ToString;

@ToString
public class TimetableInfoVo {
    private String dayAndNight;
    private String grade;
    private String classNm;
    private String perio;
    private String subject;

    @JsonProperty("DGHT_CRSE_SC_NM")
    public void setDayAndNight(String dayAndNight) {
        this.dayAndNight = dayAndNight;
    }

    @JsonProperty("GRADE")
    public void setGrade(String grade) {
        this.grade = grade;
    }

    @JsonProperty("CLASS_NM")
    public void setClassNm(String classNm) {
        this.classNm = classNm;
    }

    @JsonProperty("dayAndNight")
    public String getDayAndNight() {
        return dayAndNight;
    }

    @JsonProperty("grade")
    public String getGrade() {
        return grade;
    }

    @JsonProperty("classNm")
    public String getClassNm() {
        return classNm;
    }
    @JsonProperty("perio")
    public String getPerio() {
        return perio;
    }

    @JsonProperty("PERIO")
    public void setPerio(String perio) {
        this.perio = perio;
    }

    @JsonProperty("subject")
    public String getSubject() {
        return subject;
    }

    @JsonProperty("ITRT_CNTNT")
    public void setSubject(String subject) {
        this.subject = subject;
    }
}

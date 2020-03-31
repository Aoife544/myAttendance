package com.example.geolocationapplication;

public class ModuleInfo {

    public String classNum;
    public String lecturerName;
    public String moduleName;


    public ModuleInfo(){

    }

    public ModuleInfo(String classNum, String lecturerName, String moduleName) {
        this.classNum = classNum;
        this.lecturerName = lecturerName;
        this.moduleName = moduleName;
    }

    public String getClassNum() {
        return classNum;
    }

    public void setClassNum(String classNum) {
        this.classNum = classNum;
    }

    public String getLecturerName() {
        return lecturerName;
    }

    public void setLecturerName(String lecturerName) {
        this.lecturerName = lecturerName;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

}
package com.andromeda.cms.ui;

import java.util.Map;

public class routineModel {
    private Map<String, String> Mon, Tue, Wed, Thu, Fri;

    routineModel(){}

    routineModel(Map<String, String> Friday, Map<String, String> Monday, Map<String, String>
            Thursday, Map<String, String> Tuesday, Map<String, String> Wednesday){
        Mon=Monday;
        Tue=Tuesday;
        Wed=Wednesday;
        Thu=Thursday;
        Fri=Friday;
    }

    public Map<String, String> getMon() {
        return Mon;
    }

    public void setMon(Map<String, String> mon) {
        Mon = mon;
    }

    public Map<String, String> getTue() {
        return Tue;
    }

    public void setTue(Map<String, String> tue) {
        Tue = tue;
    }

    public Map<String, String> getWed() {
        return Wed;
    }

    public void setWed(Map<String, String> wed) {
        Wed = wed;
    }

    public Map<String, String> getThu() {
        return Thu;
    }

    public void setThu(Map<String, String> thu) {
        Thu = thu;
    }

    public Map<String, String> getFri() {
        return Fri;
    }

    public void setFri(Map<String, String> fri) {
        Fri = fri;
    }
}

package com.mazhangjing.workstation;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;

public class Config {
    private String version;
    private Integer exitSecs;
    private Integer startDelay;
    private Integer eachAppDelay;
    private LinkedList<Map<String,Object>> appList;

    public Integer getEachAppDelay() {
        return eachAppDelay;
    }

    public void setEachAppDelay(Integer eachAppDelay) {
        this.eachAppDelay = eachAppDelay;
    }

    @Override
    public String toString() {
        return "Config{" +
                "version='" + version + '\'' +
                ", appList=" + appList +
                '}';
    }

    public Integer getExitSecs() {
        return exitSecs;
    }

    public void setExitSecs(Integer exitSecs) {
        this.exitSecs = exitSecs;
    }

    public String getVersion() {
        return version;
    }

    public Integer getStartDelay() {
        return startDelay;
    }

    public void setStartDelay(Integer startDelay) {
        this.startDelay = startDelay;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public LinkedList<Map<String, Object>> getAppList() {
        return appList;
    }

    public void setAppList(LinkedList<Map<String, Object>> appList) {
        this.appList = appList;
    }
}

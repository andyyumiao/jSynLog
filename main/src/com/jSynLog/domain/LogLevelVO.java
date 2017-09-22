package com.jSynLog.domain;

/**
 * Package: com.jSynLog.domain
 * User: 于淼
 * Date: 2017/4/7
 * Time: 14:19
 * Description:
 */
public class LogLevelVO {
    private String log;
    private String level;
    private String defaultLevel;

    public LogLevelVO(String log, String level, String defaultLevel) {
        this.log = log;
        this.level = level;
        this.defaultLevel = defaultLevel;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getDefaultLevel() {
        return defaultLevel;
    }

    public void setDefaultLevel(String defaultLevel) {
        this.defaultLevel = defaultLevel;
    }
}

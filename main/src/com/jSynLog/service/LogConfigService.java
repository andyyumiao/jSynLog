package com.jSynLog.service;

import com.jSynLog.domain.LogLevelVO;
import com.jSynLog.exception.InvokeException;

import java.util.List;

/**
 * Package: com.jSynLog.service
 * User: 于淼
 * Date: 2017/4/7
 * Time: 17:19
 * Description:
 */
public interface LogConfigService {
    void saveLogLevel(String appDomain, String pack, String level) throws InvokeException;

    void resetLogLevel(String appDomain) throws InvokeException;

    String lookfor(String appDomain,String pack) throws InvokeException;

    List<LogLevelVO> getAllLogLevel(String appDomain)throws InvokeException;

    List<String> getAllApp()throws InvokeException;
}

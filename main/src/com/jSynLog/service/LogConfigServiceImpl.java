package com.jSynLog.service;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.jSynLog.domain.LogLevelVO;
import com.jSynLog.exception.InvokeException;

import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Package: com.jSynLog.service
 * User: 于淼
 * Date: 2017/4/7
 * Time: 17:20
 * Description:
 */
@Service
public class LogConfigServiceImpl implements LogConfigService {
    private static final Log log = LogFactory.getLog(LogConfigServiceImpl.class);

    @Value("${logtab.zk.servers}")
    private String ZKServers;

    @Value("${logtab.name}")
    private String logtabName;

    @Override
    public void saveLogLevel(String appDomain, String pack, String level) throws InvokeException {
        if (StringUtils.isBlank(pack)) {
            throw new InvokeException(1, "日志目录不可为空!");
        }
        if (StringUtils.isBlank(level)) {
            throw new InvokeException(1, "日志级别不可为空!");
        }
        if (StringUtils.isBlank(appDomain)) {
            throw new InvokeException(1, "应用域名不可为空!");
        }

        ZkClient zkClient = null;

        try {
            zkClient = new ZkClient(ZKServers, 6000, 6000);
            log.debug("zk client start successfully!");
            log.debug("[zkServers]: " + ZKServers);

            if ("root".equals(pack)) {
                zkClient.writeData(findSubPath(appDomain, "root"), level);
            } else if ("all".equals(pack)) {
                //修改root
                zkClient.writeData(findSubPath(appDomain, "root"), level);

                //修改子目录
                String rootPath = findRootPath(appDomain);
                List<String> subPath = zkClient.getChildren(rootPath);
                for (String path : subPath) {
                    log.debug("=========update sub path==============" + path);
                    String thePath = findSubPath(appDomain, path);
                    try{
                        zkClient.writeData(thePath, level);
                    }catch (Exception e){
                        log.error("=============可能发现未知的节点："+thePath+"==============",e);
                    }

                }

            } else {
                String path = findSubPath(appDomain, pack);
                try{
                    zkClient.writeData(path, level);
                }catch (Exception e){
                    log.error("=============可能发现未知的节点："+path+"==============",e);
                }

            }

        } catch (Exception e) {
            log.error("========saveLogLevel==========", e);
            throw new InvokeException(1, "修改日志级别失败!");
        } finally {
            zkClient.close();
        }
    }

    private String findRootPath(String appDomain) {
        String path = "/" + logtabName + "/" + appDomain;
        return path;
    }

    private String findSubPath(String appDomain, String subFolder) {
        String path = "/" + logtabName + "/" + appDomain + "/" + subFolder;
        return path;
    }

    @Override
    public void resetLogLevel(String appDomain) throws InvokeException {
        if (StringUtils.isBlank(appDomain)) {
            throw new InvokeException(1, "应用域名不可为空!");
        }

        ZkClient zkClient = null;

        try {
            zkClient = new ZkClient(ZKServers, 6000, 6000);
            log.debug("zk client start successfully!");
            log.debug("[zkServers]: " + ZKServers);

            String defaultPath = zkClient.readData(findRootPath(appDomain));
            log.debug("==================defaultPath===================="+defaultPath);
            String[] confPathArray = defaultPath.split(";");

            for (int i = 0; i < confPathArray.length; i++) {
                String[] logLevelArray = confPathArray[i].split(":");
                for (int j = 0; j < logLevelArray.length; j++) {
                    String subPath = findSubPath(appDomain, logLevelArray[0]);
                    log.debug("===========reset=======" + subPath + "=====:" + logLevelArray[1]);
                    try{
                        zkClient.writeData(subPath, logLevelArray[1]);
                    }catch (Exception e){
                        log.error("=============可能发现未知的节点："+subPath+"==============",e);
                    }

                }
            }

        } catch (Exception e) {
            log.error("========resetLogLevel==========", e);
            throw new InvokeException(1, "复位日志级别失败!");
        } finally {
            zkClient.close();
        }
    }

    @Override
    public String lookfor(String appDomain, String pack) throws InvokeException {
        if (StringUtils.isBlank(pack)) {
            throw new InvokeException(1, "日志目录不可为空!");
        }
        if (StringUtils.isBlank(appDomain)) {
            throw new InvokeException(1, "应用域名不可为空!");
        }
        ZkClient zkClient = null;

        try {
            zkClient = new ZkClient(ZKServers, 6000, 6000);
            log.debug("zk client start successfully!");
            log.debug("[zkServers]: " + ZKServers);

            log.debug("==========finded=======lookforPath=====" + pack);
            if ("root".equals(pack)) {
                Stat stat = new Stat();
                String data = zkClient.readData(findSubPath(appDomain, pack), stat);
                return StringUtils.isBlank(data) ? "debug" : data;

            } else if ("all".equals(pack)) {
                return "debug";
            } else {
                Stat stat = new Stat();
                String data = zkClient.readData(findSubPath(appDomain, pack), stat);
                return StringUtils.isBlank(data) ? "debug" : data;

            }

        } catch (Exception e) {
            log.error("========lookfor==========", e);
            throw new InvokeException(1, "查找日志级别失败!");
        } finally {
            zkClient.close();
        }
    }

    @Override
    public List<LogLevelVO> getAllLogLevel(String appDomain) throws InvokeException {
        if (StringUtils.isBlank(appDomain)) {
            throw new InvokeException(1, "应用域名不可为空!");
        }
        log.debug("=========getAllLogLevel=========appDomain=====" + appDomain);
        List<LogLevelVO> logLevelVOList = Lists.newArrayList();

        ZkClient zkClient = null;

        try {
            zkClient = new ZkClient(ZKServers, 6000, 6000);
            log.debug("zk client start successfully!");
            log.debug("[zkServers]: " + ZKServers);

            String rootPath = findRootPath(appDomain);

            log.debug("=========getAllLogLevel=========rootPath=====" + rootPath);
            List<String> subPath = zkClient.getChildren(rootPath);
            log.debug("=========getAllLogLevel=========subPath=====" + JSONObject.toJSONString(subPath));
            for (String path : subPath) {
                log.debug("=========get sub path==============" + path);
                String subData = zkClient.readData(findSubPath(appDomain, path));
                logLevelVOList.add(new LogLevelVO(path, subData, subData));
            }

            logLevelVOList.add(new LogLevelVO("all", "debug", "debug"));
        } catch (Exception e) {
            log.error("========getAllLogLevel==========", e);
            throw new InvokeException(1, "获取日志级别失败!");
        } finally {
            zkClient.close();
        }

        return logLevelVOList;
    }

    @Override
    public List<String> getAllApp() throws InvokeException {
        List<String> appList = Lists.newArrayList();

        ZkClient zkClient = null;

        try {
            zkClient = new ZkClient(ZKServers, 6000, 6000);
            log.debug("zk client start successfully!");
            log.debug("[zkServers]: " + ZKServers);

            String root = "/" + logtabName;

            List<String> subPath = zkClient.getChildren(root);
            log.debug("=================getAllApp===================="+JSONObject.toJSONString(subPath));
            for (String domain : subPath) {
                appList.add(domain);
            }

        } catch (Exception e) {
            log.error("========getAllApp==========", e);
            throw new InvokeException(1, "获取应用失败!");
        } finally {
            zkClient.close();
        }

        return appList;
    }

}

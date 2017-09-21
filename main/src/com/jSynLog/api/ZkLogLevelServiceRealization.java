package com.jSynLog.api;

import com.alibaba.fastjson.JSONObject;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.zookeeper.CreateMode;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Package: com.jSynLog.api
 * User: 于淼
 * Date: 2017/4/13
 * Time: 13:34
 * Description:
 */
public class ZkLogLevelServiceImpl implements ZkLogLevelService {
    private static final Log log = LogFactory.getLog(ZkLogLevelServiceImpl.class);
    private static final AtomicBoolean initFlag = new AtomicBoolean(false);

    private String zkServers;
    private static String logtabLevelPath;
    private static ZkClient zkClient;
    private static String logtabName;
    private static String logtabAppDomain;

    private static String SP = "/";

    @Override
    public void init() {
        //防重复初始化
        if (initFlag.compareAndSet(false, true)) {
            try {
                zkClient = new ZkClient(zkServers, 6000, 6000);
                log.debug("zk client start successfully!");
                log.debug("[zkServers]: " + zkServers + ",[levelPath]:" + logtabLevelPath);

                //将默认log目录数据存入域名节点
                String domainPath = createDomain(zkClient, logtabLevelPath);
                log.debug("==============create=========domainPath========" + domainPath);
                zkClient.subscribeDataChanges(domainPath, new DomainDataListener());

                String[] levelPathArray = logtabLevelPath.split(";");
                for (int i = 0; i < levelPathArray.length; i++) {
                    String[] levelArray = levelPathArray[i].split(":");

                    String path = createSub(zkClient, levelArray[0], levelArray[1]);
                    zkClient.subscribeDataChanges(path, new SubDataListener());
                }

            } catch (Exception e) {
                e.printStackTrace();
                log.error("init zk failed.", e);
                if (null != zkClient) {
                    zkClient.close();
                }
            }
        }

    }

    private static String createDomain(ZkClient zkClient, String data) {
        String path = SP + logtabName;
        if (!zkClient.exists(path)) {
            zkClient.create(path, null, CreateMode.PERSISTENT);
        }
        path += (SP + logtabAppDomain);
        if (!zkClient.exists(path)) {
            zkClient.create(path, data, CreateMode.PERSISTENT);
        }
        return path;
    }

    private static String createSub(ZkClient zkClient, String subFolder, String data) {
        String path = SP + logtabName;
        if (!zkClient.exists(path)) {
            zkClient.create(path, null, CreateMode.PERSISTENT);
        }
        path += (SP + logtabAppDomain);
        if (!zkClient.exists(path)) {
            zkClient.create(path, null, CreateMode.PERSISTENT);
        }
        path += (SP + subFolder);
        if (!zkClient.exists(path)) {
            zkClient.create(path, data, CreateMode.EPHEMERAL);
        }
        return path;
    }

    private static class DomainDataListener implements IZkDataListener {
        public void handleDataChange(String dataPath, Object data) throws Exception {
            log.error("=====finded domain data changed=====" + dataPath + ":" + data.toString());
            logtabLevelPath = data.toString();
        }

        public void handleDataDeleted(String dataPath) throws Exception {
            log.debug("=========domaindata==========deleted============" + dataPath);
            String path = createDomain(zkClient, logtabLevelPath);
            zkClient.subscribeDataChanges(path, new DomainDataListener());
        }
    }

    private static class SubDataListener implements IZkDataListener {
        public void handleDataChange(String dataPath, Object data) throws Exception {
            log.debug(dataPath + ":" + data.toString());
            Level level = Level.toLevel(data.toString(), Level.INFO);

            String[] pathList = dataPath.split("/");
            String logFolder = pathList[pathList.length - 1];
            if ("root".equals(logFolder)) {
                LogManager.getRootLogger().setLevel(level);
            } else {
                LogManager.getLogger(logFolder).setLevel(level);
            }
        }

        public void handleDataDeleted(String dataPath) throws Exception {
            log.debug("=========data==========deleted============" + dataPath);
            String[] pathList = dataPath.split("/");
            String logFolder = pathList[pathList.length - 1];

            String[] levelPathArray = logtabLevelPath.split(";");
            for (int i = 0; i < levelPathArray.length; i++) {
                String[] levelArray = levelPathArray[i].split(":");

                //匹配挂掉的sub node，重新创建并监听
                if (levelArray[0].equals(logFolder)) {
                    String path = createSub(zkClient, levelArray[0], levelArray[1]);
                    zkClient.subscribeDataChanges(path, new SubDataListener());
                    break;
                }
            }
        }
    }

    private static class ZKChildListener implements IZkChildListener {
        /**
         * handleChildChange： 用来处理服务器端发送过来的通知
         * parentPath：对应的父节点的路径
         * currentChilds：子节点的相对路径
         */
        public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
            log.debug(parentPath);
            log.debug(JSONObject.toJSONString(currentChilds));
        }

    }

    public String getZkServers() {
        return zkServers;
    }

    public void setZkServers(String zkServers) {
        this.zkServers = zkServers;
    }

    public String getLogtabLevelPath() {
        return logtabLevelPath;
    }

    public void setLogtabLevelPath(String logtabLevelPath) {
        this.logtabLevelPath = logtabLevelPath;
    }

    public String getLogtabName() {
        return logtabName;
    }

    public void setLogtabName(String logtabName) {
        this.logtabName = logtabName;
    }

    public String getLogtabAppDomain() {
        return logtabAppDomain;
    }

    public void setLogtabAppDomain(String logtabAppDomain) {
        this.logtabAppDomain = logtabAppDomain;
    }
}

package com.jSybLog.web;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;

import com.jSybLog.domain.LogLevelVO;
import com.jSybLog.exception.InvokeException;
import com.jSybLog.service.LogConfigService;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


@Controller
@RequestMapping("/mobile_sys/loglevel")
public class Log4JController {
    private static final Log log = LogFactory.getLog(Log4JController.class);
    @Autowired
    private LogConfigService logConfigService;


    @RequestMapping("index.html")
    public String appList(Model model) {
       /* try{
            List<String> appList = logConfigService.getAllApp();
            List<LogLevelVO> logLevelVOList = logConfigService.getAllLogLevel(appList.get(0));
            model.addAttribute("logLevel", logLevelVOList);
            model.addAttribute("appList", appList);

        }catch (Exception e){
            log.error("===============index.html========failed========", e);
        }*/

        return "loglevel/index";
    }

    /**
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "setUpLog4jOutputLevel.ajax")
    @ResponseBody
    public JSONObject setUpLog4jOutputLevel(HttpServletRequest request) {
        JSONObject result = new JSONObject();
        try {

            String pack = request.getParameter("package");
            String level = request.getParameter("level");
            String domain = request.getParameter("domain");
            logConfigService.saveLogLevel(domain, pack, level);

            result.put("code", 0);
        } catch (InvokeException e) {
            result.put("msg", e.getMessage());
            result.put("code", 1);
        } catch (Exception e) {
            result.put("msg", "设置日志级别失败!");
            result.put("code", 1);
        }
        return result;
    }

    @RequestMapping(method = RequestMethod.POST, value = "reset.ajax")
    @ResponseBody
    public JSONObject reset(HttpServletRequest request) {
        JSONObject result = new JSONObject();
        try {
            String domain = request.getParameter("domain");
            logConfigService.resetLogLevel(domain);

            result.put("code", 0);
        } catch (InvokeException e) {
            result.put("msg", e.getMessage());
            result.put("code", 1);
        } catch (Exception e) {
            result.put("msg", "复位日志级别失败!");
            result.put("code", 1);
        }
        return result;
    }

    @RequestMapping(method = RequestMethod.POST, value = "lookfor.ajax")
    @ResponseBody
    public JSONObject lookfor(HttpServletRequest request) {
        JSONObject result = new JSONObject();
        try {
            String lookforPath = request.getParameter("package");
            String domain = request.getParameter("domain");
            String level = logConfigService.lookfor(domain, lookforPath);
            result.put("level", level);
            result.put("code", 0);
        } catch (InvokeException e) {
            result.put("msg", e.getMessage());
            result.put("code", 1);
        } catch (Exception e) {
            result.put("msg", "查询日志级别失败!");
            result.put("code", 1);
        }
        return result;
    }

    @RequestMapping(method = RequestMethod.POST, value = "switchApp.ajax")
    @ResponseBody
    public JSONObject switchApp(HttpServletRequest request) {
        JSONObject result = new JSONObject();
        try {
            String domain = request.getParameter("domain");
            List<LogLevelVO> logLevelVOList = null;

            List<String> appList = logConfigService.getAllApp();
            result.put("appList", appList);

            if (StringUtils.isBlank(domain)) {
                logLevelVOList = logConfigService.getAllLogLevel(null == appList ? null : appList.get(0));
            } else {
                logLevelVOList = logConfigService.getAllLogLevel(domain);
            }
            List<String> finalLogList = Lists.newArrayList();
            for (LogLevelVO levelVO : logLevelVOList) {
                finalLogList.add(levelVO.getLog());
            }
            result.put("logLevelList", finalLogList);

            result.put("code", 0);
        } catch (InvokeException e) {
            result.put("msg", e.getMessage());
            result.put("code", 1);
        } catch (Exception e) {
            result.put("msg", "切换应用目录失败!");
            result.put("code", 1);
        }
        return result;
    }

}

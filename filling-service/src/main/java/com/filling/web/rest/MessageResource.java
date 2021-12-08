package com.filling.web.rest;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/messaging/rest/v1")
public class MessageResource {

    @Autowired
    HttpServletRequest request;

    private static class AccountResourceException extends RuntimeException {

        private AccountResourceException(String message) {
            super(message);
        }
    }

    private final Logger log = LoggerFactory.getLogger(MessageResource.class);

    /**
     *
     * @param obj [{"eventId":"http://MrJiangs-MacBook-Pro.local:18633","destinations":["jobrunner-app"],"requiresAck":false,"ackEvent":false,"eventTypeId":2001,"payload":"{\"sdcId\":\"7991c301-5168-4925-85ed-fbad5532e138\",\"httpUrl\":\"http://MrJiangs-MacBook-Pro.local:18633\",\"javaVersion\":\"go1.17\",\"sdcBuildInfo\":{\"builtBy\":\"\",\"builtDate\":\"\",\"builtRepoSha\":\"\",\"sourceMd5Checksum\":\"\",\"version\":\"\"},\"labels\":[\"all\"],\"edge\":true,\"totalMemory\":0}","orgId":""},{"eventId":"1a98a887-a298-42c8-9b15-9e80934fbc2e","destinations":["jobrunner-app"],"requiresAck":false,"ackEvent":false,"eventTypeId":2002,"payload":"{\"pipelineStatusEventList\":[{\"name\":\"HTTPTEST90472271-7cec-4a84-b936-7c2b6f4e1ff7\",\"title\":\"HTTPTEST90472271-7cec-4a84-b936-7c2b6f4e1ff7\",\"rev\":\"\",\"timeStamp\":1633704603870,\"remote\":false,\"pipelineStatus\":\"FINISHED\",\"message\":\"\",\"workerInfos\":null,\"validationStatus\":null,\"issues\":\"\",\"clusterMode\":false,\"offset\":\"{\\\"Version\\\":2,\\\"Offset\\\":{\\\"$com.streamsets.sdc2go.pollsource.offset$\\\":null}}\",\"offsetProtocolVersion\":0,\"acl\":null,\"runnerCount\":0},{\"name\":\"localedgea0c69b40-c75a-4336-ab0a-5c0948040c68\",\"title\":\"localedgea0c69b40-c75a-4336-ab0a-5c0948040c68\",\"rev\":\"\",\"timeStamp\":1633791056540,\"remote\":false,\"pipelineStatus\":\"STOPPED\",\"message\":\"No Stage Instance found for : , stage: \",\"workerInfos\":null,\"validationStatus\":null,\"issues\":\"\",\"clusterMode\":false,\"offset\":\"{\\\"Version\\\":2,\\\"Offset\\\":{\\\"$com.streamsets.sdc2go.pollsource.offset$\\\":\\\"systemMetrics\\\"}}\",\"offsetProtocolVersion\":0,\"acl\":null,\"runnerCount\":0}]}","orgId":""},{"eventId":"5e589bee-9166-4498-b59b-dfcd3ad02683","destinations":["jobrunner-app","timeseries-app"],"requiresAck":false,"ackEvent":false,"eventTypeId":2003,"payload":"{\"timestamp\":1635668075005,\"sdcId\":\"7991c301-5168-4925-85ed-fbad5532e138\",\"cpuLoad\":3.960231356715859,\"usedMemory\":2255280}","orgId":""}]
     * @return
     */
    @PostMapping("/events")
    public JSONObject test02(@Valid @RequestBody Object obj) {

//        System.out.println(obj.toString());
        System.out.println(JSONObject.toJSONString(obj));
        // System.out.println(JSONArray.parseObject(obj.toString()).toJSONString());
        JSONObject result = new JSONObject();
        result.put("controlHubUrl", "controlHubUrl");
        result.put("controlHubUser", "admin@123");
        result.put("controlHubPassword", "controlHubPassword");
        result.put("controlHubUserToken", "controlHubUserToken");
        return result;
    }
}

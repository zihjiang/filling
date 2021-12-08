package com.filling.web.rest;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.filling.service.FillingEdgeNodesService;
import com.filling.web.rest.vm.EdgeRegistrationVM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URISyntaxException;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/security")
public class SecurityResource {

    @Autowired
    HttpServletRequest request;

    @Autowired
    FillingEdgeNodesService fillingEdgeNodesService;

    static String FULLAUTHTOKEN = "123456789";

    private final Logger log = LoggerFactory.getLogger(SecurityResource.class);

    /**
     * 鉴权
     * @param obj {"password":"admin","userName":"admin@123"}
     * @return
     */
    @PostMapping("/public-rest/v1/authentication/login")
    public JSONObject test02(@Valid @RequestBody Object obj) {
        JSONObject result = new JSONObject();
        result.put("controlHubUrl", "controlHubUrl");
        result.put("controlHubUser", "admin@123");
        result.put("controlHubPassword", "controlHubPassword");
        result.put("controlHubUserToken", "controlHubUserToken");
        log.debug(request.getRequestURI());
        System.out.println(JSONObject.toJSONString(obj));
        return result;
    }

    /**
     * 获取每个用户名@后的成分
     * @param obj {"active":true,"componentType":"dc-edge","numberOfComponents":1,"organization":"123"}
     * @return
     */
    @PutMapping("/rest/v1/organization/{id}/components")
    @ResponseStatus(HttpStatus.CREATED)
    public JSONArray test03(@Valid @RequestBody Object obj, @PathVariable String id) {
        JSONArray jsonArray = new JSONArray();
        JSONObject result = new JSONObject();
        result.put("fullAuthToken", "123456789");
        jsonArray.add(result);

        log.debug(request.getRequestURI());
        System.out.println(JSONObject.toJSONString(obj));
        return jsonArray;
    }

    /**
     *
     * @param edgeRegistrationVM {"authToken":"123456789","componentId":"7991c301-5168-4925-85ed-fbad5532e138","attributes":{"baseHttpUrl":"http://MrJiangs-MacBook-Pro.local:18633","sdc2goGoVersion":"go1.17","sdc2goGoOS":"darwin","sdc2goGoArch":"arm64","sdc2goBuildDate":"","sdc2goRepoSha":"","sdc2goVersion":""}}
     * @return
     */
    @PostMapping("/public-rest/v1/components/registration")
    public JSONObject test04(@Valid @RequestBody EdgeRegistrationVM edgeRegistrationVM) throws URISyntaxException {
        log.debug(request.getRequestURI());
        JSONObject result = new JSONObject();

        fillingEdgeNodesService.saveByUuid(edgeRegistrationVM.getEdgeNodes());
        result.put("fullAuthToken", "123456789");

        return result;
    }
}

package org.recap.controller;

import org.json.JSONObject;
import org.recap.util.PropertyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;import java.util.Map;@RefreshScope
@RestController
@RequestMapping("/ins")
class MessageRestController {
    @Autowired
    PropertyUtil propertyUtil;
    @GetMapping("/{institutionCode}")
    public Map<String, Object> getValue(@PathVariable("institutionCode") String institutionCode) {
    JSONObject json  = propertyUtil.getPropertyByInstitution(institutionCode);
    return json.toMap();
}
    @GetMapping("/test")
    public String  getValue() {

        return "response";
    }

}

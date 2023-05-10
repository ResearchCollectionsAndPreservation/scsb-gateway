package org.recap.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Dinakar N created on 10/05/23
 */
@RestController
public class SpringMvcController {
    @GetMapping("/")
    public String checkMvc() {
        return "index";
    }
}

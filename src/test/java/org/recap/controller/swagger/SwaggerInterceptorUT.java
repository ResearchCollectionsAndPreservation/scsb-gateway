package org.recap.controller.swagger;


import org.junit.jupiter.api.Test;
import org.recap.BaseTestCase;
import org.recap.config.SwaggerInterceptor;
import org.recap.util.MD5EncoderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Created by hemalathas on 25/1/17.
 */
public class SwaggerInterceptorUT extends BaseTestCase {

    @Autowired
    SwaggerInterceptor swaggerInterceptor;

    @Autowired
    HttpServletRequest httpServletRequest;

    @Autowired
    private HttpServletResponse httpServletResponse;

    @Test
    public void testPreHandle() throws Exception {
        httpServletRequest.setAttribute("api_key", "scsb");
        boolean continueExport = swaggerInterceptor.preHandle(httpServletRequest, httpServletResponse, new Object());
        assertTrue(!continueExport);
    }

    @Test
    public void testPostHandle() throws Exception {
        swaggerInterceptor.postHandle(httpServletRequest, httpServletResponse, new Object(), new ModelAndView());
    }

    @Test
    public void testAfterCompletion() throws Exception {
        swaggerInterceptor.afterCompletion(httpServletRequest, httpServletResponse, new Object(), new Exception());
    }

    @Test
    public void testgetMd5EncoderUtil() throws Exception {
        MD5EncoderUtil EncoderUtil = swaggerInterceptor.getMd5EncoderUtil();
        assertNotNull(EncoderUtil);
    }

    @Test
    public void testgetScsbApiKey() throws Exception {
        String ScsbApiKey = swaggerInterceptor.getScsbApiKey();
        assertNotNull(ScsbApiKey);
    }

    @Test
    public void testgetScsbapiKey() {
        String ScsbApiKey = swaggerInterceptor.getScsbApiKey();
    }

    @Test
    public void getMd5EncoderUtiltest() {
        MD5EncoderUtil EncoderUtil = swaggerInterceptor.getMd5EncoderUtil();
    }

    @Test
    public void matchingWithInstitutionKeysTest(){
        ReflectionTestUtils.invokeMethod(swaggerInterceptor, "matchingWithInstitutionKeys", "key");
    }

}
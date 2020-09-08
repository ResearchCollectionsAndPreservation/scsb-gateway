package org.recap.controller;

import org.recap.RecapCommonConstants;
import org.recap.service.RestHeaderService;
import org.recap.spring.SwaggerAPIProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

@Component
public class AbstractController {

    @Value("${scsb.circ.url}")
    private String scsbCircUrl;

    @Value("${scsb.solr.client.url}")
    private String scsbSolrClient;

    @Value("${scsb.etl.url}")
    private String scsbEtlUrl;

    @Value("${scsb.core.url}")
    private String scsbCoreUrl;


    @Autowired
    RestHeaderService restHeaderService;

    /**
     * Gets scsb circ url.
     *
     * @return the scsb circ url
     */
    public String getScsbCircUrl() {
        return scsbCircUrl;
    }

    /**
     * Gets scsb solr client url.
     * @return the scsb solr client url
     */
    public String getScsbSolrClientUrl() {
        return scsbSolrClient;
    }

    /**
     * Gets scsb etl url.
     * @return the scsb etl url
     */
    public String getScsbEtlUrl() {
        return scsbEtlUrl;
    }

    /**
     * Gets scsb core url.
     * @return the scsb core url
     */
    public String getScsbCoreUrl() {
        return scsbCoreUrl;
    }


    /**
     * Sets scsb solr client url.
     * @param scsbSolrClientUrl the scsb solr client url
     */
    public void setScsbSolrClientUrl(String scsbSolrClientUrl) {
        this.scsbSolrClient = scsbSolrClientUrl;
    }


    /**
     * Get rest template rest template.
     *
     * @return the rest template
     */
    public RestTemplate getRestTemplate(){
        return new RestTemplate();
    }

    public RestHeaderService getRestHeaderService() {
        return restHeaderService;
    }

    /**
     * Get http entity http entity.
     *
     * @return the http entity
     */
    public HttpEntity getHttpEntity(){
        return new HttpEntity<>(getHttpHeaders());
    }

    public HttpHeaders getHttpHeaders() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add(RecapCommonConstants.RESPONSE_DATE, new Date().toString());
        //responseHeaders.add(RecapCommonConstants.RESPONSE_HEADER_CONTENT_TYPE,RecapCommonConstants.RESPONSE_HEADER_CONTENT_TYPE_VALUE);
        return responseHeaders;
    }

    public HttpEntity getSwaggerHttpEntity(){
        return new HttpEntity<>(getSwaggerHeaders());
    }

    public static HttpHeaders getSwaggerHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(RecapCommonConstants.API_KEY, SwaggerAPIProvider.getInstance().getSwaggerApiKey());
        return headers;
    }

}

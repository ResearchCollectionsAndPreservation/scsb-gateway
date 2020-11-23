package org.recap.controller.swagger;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCase;
import org.recap.RecapConstants;
import org.recap.service.RestHeaderService;
import org.recap.model.SearchRecordsRequest;
import org.recap.model.SearchRecordsResponse;
import org.recap.model.SearchResultRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created by hemalathas on 3/2/17.
 */
public class SearchRecordsRestControllerUT extends BaseTestCase{

    @Value("${scsb.solr.doc.url}")
    String scsbSolrClient;

    @Mock
    RestTemplate mockRestTemplate;

    @Mock
    SearchRecordsRestController searchRecordsRestController;

    @Autowired
    RestHeaderService restHeaderService;

    public String getScsbSolrClient() {
        return scsbSolrClient;
    }

    public void setScsbSolrClient(String scsbSolrClient) {
        this.scsbSolrClient = scsbSolrClient;
    }

    @Test
    public void testSearchRecordService(){
        SearchRecordsRequest searchRecordsRequest = new SearchRecordsRequest();
        searchRecordsRequest.setFieldValue("test");
        searchRecordsRequest.setFieldName("test");
        searchRecordsRequest.setAvailability(Arrays.asList("Available"));
        searchRecordsRequest.setOwningInstitutions(Arrays.asList("PUL"));
        searchRecordsRequest.setCollectionGroupDesignations(Arrays.asList("Open"));
        searchRecordsRequest.setUseRestrictions(Arrays.asList("Others"));
        searchRecordsRequest.setMaterialTypes(Arrays.asList("Monograph"));
        searchRecordsRequest.setCatalogingStatus("Complete");
        searchRecordsRequest.setDeleted(false);
        searchRecordsRequest.setPageSize(10);
        searchRecordsRequest.setPageNumber(10);
        HttpEntity<SearchRecordsRequest> httpEntity = new HttpEntity<>(searchRecordsRequest, restHeaderService.getHttpHeaders());
        SearchRecordsResponse searchRecordsResponse = getSearchRecordsResponse();
        ResponseEntity<SearchRecordsResponse> responseEntity = new ResponseEntity<SearchRecordsResponse>(searchRecordsResponse,HttpStatus.OK);
        Mockito.when(mockRestTemplate.exchange(scsbSolrClient+ RecapConstants.URL_SEARCH_BY_JSON, HttpMethod.POST, httpEntity, SearchRecordsResponse.class)).thenReturn(responseEntity);
        Mockito.when(searchRecordsRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(searchRecordsRestController.getRestHeaderService()).thenReturn(restHeaderService);
        Mockito.when(searchRecordsRestController.getScsbSolrClientUrl()).thenReturn(scsbSolrClient);
        Mockito.when(searchRecordsRestController.searchRecordsServiceGetParam(searchRecordsRequest)).thenCallRealMethod();
        SearchRecordsResponse recordsResponse = searchRecordsRestController.searchRecordsServiceGetParam(searchRecordsRequest);
        assertNotNull(recordsResponse);
        assertNotNull(recordsResponse.getErrorMessage());
        assertNotNull(recordsResponse.getSearchResultRows());
        assertNotNull(recordsResponse.getTotalBibRecordsCount());
        assertNotNull(recordsResponse.getTotalItemRecordsCount());
        assertNotNull(recordsResponse.getTotalPageCount());
        assertNotNull(recordsResponse.getTotalRecordsCount());
        assertNotNull(searchRecordsRequest.getFieldValue());
        assertNotNull(searchRecordsRequest.getFieldName());
        assertNotNull(searchRecordsRequest.getOwningInstitutions());
        assertNotNull(searchRecordsRequest.getCollectionGroupDesignations());
        assertNotNull(searchRecordsRequest.getAvailability());
        assertNotNull(searchRecordsRequest.getMaterialTypes());
        assertNotNull(searchRecordsRequest.getUseRestrictions());
        assertNotNull(searchRecordsRequest.isDeleted());
        assertNotNull(searchRecordsRequest.getCatalogingStatus());
        assertNotNull(searchRecordsRequest.getPageNumber());
        assertNotNull(searchRecordsRequest.getPageSize());
    }

    @Test
    public void testSearchRecordService_Exception(){
        SearchRecordsRequest searchRecordsRequest = new SearchRecordsRequest();
        Mockito.when(searchRecordsRestController.searchRecordsServiceGetParam(searchRecordsRequest)).thenCallRealMethod();
        SearchRecordsResponse recordsResponse = searchRecordsRestController.searchRecordsServiceGetParam(searchRecordsRequest);
        assertNull(recordsResponse.getErrorMessage());
    }

    @Test
    public void checkGetterServices(){
        Mockito.when(searchRecordsRestController.getRestTemplate()).thenCallRealMethod();
        Mockito.when(searchRecordsRestController.getScsbSolrClientUrl()).thenCallRealMethod();
        assertNotEquals(searchRecordsRestController.getRestTemplate(),mockRestTemplate);
        assertNotEquals(searchRecordsRestController.getScsbSolrClientUrl(),scsbSolrClient);
    }

    @Test
    public void testSearchRecordServiceGet(){
        HttpEntity request = new HttpEntity(restHeaderService.getHttpHeaders());
        List<SearchResultRow> searchResultRowList = new ArrayList<>();
        ResponseEntity<List> httpEntity = new ResponseEntity<List>(searchResultRowList,HttpStatus.OK);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(scsbSolrClient + RecapConstants.URL_SEARCH_BY_PARAM)
                .queryParam("fieldValue", "test")
                .queryParam("fieldName", "test")
                .queryParam("owningInstitutions", "PUL")
                .queryParam("collectionGroupDesignations","Shared")
                .queryParam("availability","Available")
                .queryParam("materialTypes","Monograph")
                .queryParam("useRestrictions","NoRestrictions")
                .queryParam("pageSize", 10);
        Mockito.when(mockRestTemplate.exchange(builder.build().encode().toUri(), HttpMethod.GET, request, List.class)).thenReturn(httpEntity);
        Mockito.when(searchRecordsRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(searchRecordsRestController.getScsbSolrClientUrl()).thenReturn(scsbSolrClient);
        Mockito.when(searchRecordsRestController.getRestHeaderService()).thenReturn(restHeaderService);
        Mockito.when(searchRecordsRestController.searchRecordsServiceGet("test","test","PUL","Shared","Available","Monograph","NoRestrictions",10)).thenCallRealMethod();
        List<SearchResultRow> searchResultRows= searchRecordsRestController.searchRecordsServiceGet("test","test","PUL","Shared","Available","Monograph","NoRestrictions",10);
        assertNotNull(searchResultRows);

    }

    @Test
    public void testSearchRecordServiceGet_Exception(){
        HttpEntity request = new HttpEntity(restHeaderService.getHttpHeaders());
        List<SearchResultRow> searchResultRowList = new ArrayList<>();
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(scsbSolrClient + RecapConstants.URL_SEARCH_BY_PARAM)
                .queryParam("fieldValue", "test")
                .queryParam("fieldName", "test")
                .queryParam("owningInstitutions", "PUL")
                .queryParam("collectionGroupDesignations","Shared")
                .queryParam("availability","Available")
                .queryParam("materialTypes","Monograph")
                .queryParam("useRestrictions","NoRestrictions")
                .queryParam("pageSize", 10);
        Mockito.when(mockRestTemplate.exchange(builder.build().encode().toUri(), HttpMethod.GET, request, List.class)).thenReturn(null);
        Mockito.when(searchRecordsRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(searchRecordsRestController.getScsbSolrClientUrl()).thenReturn(scsbSolrClient);
        Mockito.when(searchRecordsRestController.getRestHeaderService()).thenReturn(restHeaderService);
        Mockito.when(searchRecordsRestController.searchRecordsServiceGet("test","test","PUL","Shared","Available","Monograph","NoRestrictions",10)).thenCallRealMethod();
        List<SearchResultRow> searchResultRows= searchRecordsRestController.searchRecordsServiceGet("test","test","PUL","Shared","Available","Monograph","NoRestrictions",10);
        assertNotNull(searchResultRows);
    }



    public SearchRecordsResponse getSearchRecordsResponse(){
        SearchRecordsResponse searchRecordsResponse = new SearchRecordsResponse();
        searchRecordsResponse.setTotalPageCount(3);
        searchRecordsResponse.setTotalRecordsCount("1");
        searchRecordsResponse.setShowTotalCount(true);
        searchRecordsResponse.setTotalBibRecordsCount("1");
        searchRecordsResponse.setTotalItemRecordsCount("1");
        searchRecordsResponse.setErrorMessage("message");
        searchRecordsResponse.setSearchResultRows(Arrays.asList(new SearchResultRow()));
        return searchRecordsResponse;
    }
}
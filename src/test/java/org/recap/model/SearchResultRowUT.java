package org.recap.model;

import org.junit.Test;
import org.recap.BaseTestCase;

import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.assertNotNull;

/**
 * Created by hemalathas on 24/3/17.
 */
public class SearchResultRowUT extends BaseTestCase{

    @Test
    public void testSearchResultRow(){
        SearchResultRow searchResultRow = new SearchResultRow();
        searchResultRow.setBibId(1);
        searchResultRow.setTitle("test");
        searchResultRow.setAuthor("John");
        searchResultRow.setPublisher("Test");
        searchResultRow.setPublisherDate(new Date().toString());
        searchResultRow.setOwningInstitution("PUL");
        searchResultRow.setCustomerCode("PB");
        searchResultRow.setCollectionGroupDesignation("Open");
        searchResultRow.setUseRestriction("Supervised Use");
        searchResultRow.setBarcode("3326564464656465465");
        searchResultRow.setSummaryHoldings("test");
        searchResultRow.setAvailability("Available");
        searchResultRow.setLeaderMaterialType("Test");
        searchResultRow.setSelected(true);
        searchResultRow.setShowItems(true);
        searchResultRow.setSelectAllItems(true);
        searchResultRow.setRequestPosition(1);
        searchResultRow.setItemId(1);
        searchResultRow.setPatronBarcode(456985227);
        searchResultRow.setRequestingInstitution("CUL");
        searchResultRow.setDeliveryLocation("PB");
        searchResultRow.setRequestType("Recall");
        searchResultRow.setRequestNotes("test");
        searchResultRow.setSearchItemResultRows(Arrays.asList(getSearchItemResultRow()));

        assertNotNull(searchResultRow.getBibId());
        assertNotNull(searchResultRow.getTitle());
        assertNotNull(searchResultRow.getAuthor());
        assertNotNull(searchResultRow.getPublisher());
        assertNotNull(searchResultRow.getPublisherDate());
        assertNotNull(searchResultRow.getOwningInstitution());
        assertNotNull(searchResultRow.getCustomerCode());
        assertNotNull(searchResultRow.getCollectionGroupDesignation());
        assertNotNull(searchResultRow.getUseRestriction());
        assertNotNull(searchResultRow.getBarcode());
        assertNotNull(searchResultRow.getSummaryHoldings());
        assertNotNull(searchResultRow.getAvailability());
        assertNotNull(searchResultRow.getLeaderMaterialType());
        assertNotNull(searchResultRow.isSelected());
        assertNotNull(searchResultRow.isShowItems());
        assertNotNull(searchResultRow.isSelectAllItems());
        assertNotNull(searchResultRow.getItemId());
        assertNotNull(searchResultRow.getRequestPosition());
        assertNotNull(searchResultRow.getPatronBarcode());
        assertNotNull(searchResultRow.getRequestingInstitution());
        assertNotNull(searchResultRow.getDeliveryLocation());
        assertNotNull(searchResultRow.getRequestType());
        assertNotNull(searchResultRow.getRequestNotes());
        assertNotNull(searchResultRow.getSearchItemResultRows());
        assertNotNull(searchResultRow.getSearchItemResultRows().get(0).getCallNumber());
        assertNotNull(searchResultRow.getSearchItemResultRows().get(0).getChronologyAndEnum());
        assertNotNull(searchResultRow.getSearchItemResultRows().get(0).getCustomerCode());
        assertNotNull(searchResultRow.getSearchItemResultRows().get(0).getBarcode());
        assertNotNull(searchResultRow.getSearchItemResultRows().get(0).getUseRestriction());
        assertNotNull(searchResultRow.getSearchItemResultRows().get(0).getCollectionGroupDesignation());
        assertNotNull(searchResultRow.getSearchItemResultRows().get(0).getAvailability());
        assertNotNull(searchResultRow.getSearchItemResultRows().get(0).isSelectedItem());
        assertNotNull(searchResultRow.getSearchItemResultRows().get(0).getItemId());

    }

    public SearchItemResultRow getSearchItemResultRow(){
        SearchItemResultRow searchItemResultRow = new SearchItemResultRow();
        searchItemResultRow.setCallNumber("1321");
        searchItemResultRow.setChronologyAndEnum("test");
        searchItemResultRow.setCustomerCode("PB");
        searchItemResultRow.setBarcode("321564564647684");
        searchItemResultRow.setUseRestriction("Supervised Use");
        searchItemResultRow.setCollectionGroupDesignation("Open");
        searchItemResultRow.setAvailability("Available");
        searchItemResultRow.setSelectedItem(true);
        searchItemResultRow.setItemId(1);
        SearchItemResultRow searchItemResultRow1 =searchItemResultRow;
        searchItemResultRow.compareTo(searchItemResultRow1);
        searchItemResultRow.compareTo(new SearchItemResultRow());
        return searchItemResultRow;
    }

}
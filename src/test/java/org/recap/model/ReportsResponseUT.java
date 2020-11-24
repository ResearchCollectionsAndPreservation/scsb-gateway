package org.recap.model;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.model.reports.ReportsInstitutionForm;
import org.recap.model.search.DeaccessionItemResultsRow;
import org.recap.model.search.IncompleteReportResultsRow;

import java.util.Collections;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * Created by hemalathas on 23/3/17.
 */
public class ReportsResponseUT extends BaseTestCase {

    @Test
    public void testReportResponse(){
        ReportsResponse reportsResponse = new ReportsResponse();
        ReportsInstitutionForm reportsInstitutionForm = new ReportsInstitutionForm();
        reportsInstitutionForm.setInstitution("PUL");
        reportsInstitutionForm.setAccessionPrivateCount(1);
        reportsInstitutionForm.setAccessionSharedCount(1);
        reportsInstitutionForm.setAccessionOpenCount(1);
        reportsInstitutionForm.setDeaccessionPrivateCount(1);
        reportsInstitutionForm.setDeaccessionSharedCount(1);
        reportsInstitutionForm.setDeaccessionOpenCount(1);
        reportsInstitutionForm.setOpenCgdCount(1);
        reportsInstitutionForm.setSharedCgdCount(1);
        reportsInstitutionForm.setPrivateCgdCount(1);
        reportsResponse.setReportsInstitutionFormList(Collections.singletonList(reportsInstitutionForm));
        reportsResponse.setTotalRecordsCount("1");
        reportsResponse.setTotalPageCount(1);
        reportsResponse.setMessage("Success");
        reportsResponse.setIncompletePageNumber(1);
        reportsResponse.setIncompletePageSize(10);
        reportsResponse.setIncompleteTotalPageCount(1);
        reportsResponse.setIncompleteTotalRecordsCount("1");
        DeaccessionItemResultsRow deaccessionItemResultsRow = new DeaccessionItemResultsRow();
        deaccessionItemResultsRow.setItemBarcode("32145686568567");
        deaccessionItemResultsRow.setDeaccessionNotes("test");
        deaccessionItemResultsRow.setDeaccessionOwnInst("PUL");
        deaccessionItemResultsRow.setItemId(1);
        deaccessionItemResultsRow.setTitle("test");
        deaccessionItemResultsRow.setCgd("Open");
        IncompleteReportResultsRow incompleteReportResultsRow = new IncompleteReportResultsRow();
        incompleteReportResultsRow.setTitle("test");
        incompleteReportResultsRow.setAuthor("john");
        incompleteReportResultsRow.setBarcode("32145564654564");
        incompleteReportResultsRow.setCreatedDate(new Date().toString());
        incompleteReportResultsRow.setCustomerCode("PB");
        incompleteReportResultsRow.setOwningInstitution("PUL");
        reportsResponse.setDeaccessionItemResultsRows(Collections.singletonList(deaccessionItemResultsRow));
        reportsResponse.setIncompleteReportResultsRows(Collections.singletonList(incompleteReportResultsRow));

        assertEquals(1, reportsResponse.getReportsInstitutionFormList().size());
        assertNotNull(reportsResponse.getTotalRecordsCount());
        assertNotNull(reportsResponse.getTotalPageCount());
        assertNotNull(reportsResponse.getMessage());
        assertNotNull(reportsResponse.getDeaccessionItemResultsRows());
        assertNotNull(reportsResponse.getIncompleteTotalRecordsCount());
        assertNotNull(reportsResponse.getIncompleteTotalPageCount());
        assertNotNull(reportsResponse.getIncompleteReportResultsRows());
        assertNotNull(reportsResponse.getIncompletePageNumber());
        assertNotNull(reportsResponse.getIncompletePageSize());
        assertNotNull(incompleteReportResultsRow.getTitle());
        assertNotNull(incompleteReportResultsRow.getAuthor());
        assertNotNull(incompleteReportResultsRow.getCreatedDate());
        assertNotNull(incompleteReportResultsRow.getOwningInstitution());
        assertNotNull(incompleteReportResultsRow.getCustomerCode());
        assertNotNull(incompleteReportResultsRow.getBarcode());

    }



}
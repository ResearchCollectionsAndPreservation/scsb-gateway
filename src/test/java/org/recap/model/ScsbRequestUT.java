package org.recap.model;

import org.junit.Test;
import org.recap.BaseTestCase;

import java.util.Date;

import static org.junit.Assert.assertEquals;

public class ScsbRequestUT extends BaseTestCase {

    @Test
    public void scsbRequest(){
        ScsbRequest scsbRequest=new ScsbRequest();
        scsbRequest.setReportType("Incremental");
        scsbRequest.setTransmissionType("1");
        scsbRequest.setDateFrom(new Date());
        scsbRequest.setDateTo(new Date());
        assertEquals("Incremental",scsbRequest.getReportType());
    }
}

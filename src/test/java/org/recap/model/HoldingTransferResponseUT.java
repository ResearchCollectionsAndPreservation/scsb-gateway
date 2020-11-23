package org.recap.model;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.model.transfer.Destination;
import org.recap.model.transfer.HoldingTransferResponse;
import org.recap.model.transfer.HoldingsTransferRequest;
import org.recap.model.transfer.Source;

import static org.junit.Assert.assertNotNull;

public class HoldingTransferResponseUT extends BaseTestCase {

    @Test
    public void holdingTransferResponse(){
        HoldingTransferResponse holdingTransferResponse = new HoldingTransferResponse();
        holdingTransferResponse.setMessage("Success");
        Source source=new Source();
        source.setOwningInstitutionHoldingsId("1");
        source.setOwningInstitutionBibId("2");
        Destination destination=new Destination();
        destination.setOwningInstitutionBibId("1");
        destination.setOwningInstitutionHoldingsId("2");
        HoldingsTransferRequest holdingsTransferRequest = new HoldingsTransferRequest();
        holdingsTransferRequest.setSource(source);
        holdingsTransferRequest.setDestination(destination);
        holdingTransferResponse.setHoldingsTransferRequest(holdingsTransferRequest);
        assertNotNull(holdingTransferResponse.getMessage());
        assertNotNull(holdingTransferResponse.getHoldingsTransferRequest());

     }

}

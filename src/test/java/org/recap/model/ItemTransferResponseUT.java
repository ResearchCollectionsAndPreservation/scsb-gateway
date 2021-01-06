package org.recap.model;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.RecapCommonConstants;
import org.recap.model.transfer.ItemDestination;
import org.recap.model.transfer.ItemSource;
import org.recap.model.transfer.ItemTransferRequest;
import org.recap.model.transfer.ItemTransferResponse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Anitha on 13/6/20.
 */

public class ItemTransferResponseUT extends BaseTestCase {

    @Test
    public void itemTransferResponse(){
        ItemTransferResponse itemTransferResponse = new ItemTransferResponse(RecapCommonConstants.SUCCESS,getItemTransferRequest());
        assertEquals(RecapCommonConstants.SUCCESS,itemTransferResponse.getMessage());
        assertNotNull(new ItemTransferResponse());
        assertNotNull(itemTransferResponse.getItemTransferRequest());
    }

    private ItemTransferRequest getItemTransferRequest() {
        ItemTransferRequest itemTransferRequest = new ItemTransferRequest();
        itemTransferRequest.setSource(getItemSource());
        itemTransferRequest.setDestination(getItemDestination());
        return itemTransferRequest;
    }

    private ItemDestination getItemDestination() {
        ItemDestination itemDestination = new ItemDestination();
        itemDestination.setOwningInstitutionItemId("CUL");
        return itemDestination;
    }

    private ItemSource getItemSource() {
        ItemSource itemSource = new ItemSource();
        itemSource.setOwningInstitutionItemId("PUL");
        return itemSource;
    }
}

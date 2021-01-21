package org.recap.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by sudhishk on 15/12/16.
 */
@Getter
@Setter
public class ItemHoldRequest extends AbstractRequestItem {
    private String patronIdentifier;
    private String bibId;
    private String pickupLocation;
    private String trackingId;
    private String title;
    private String author;
    private String callNumber;
}

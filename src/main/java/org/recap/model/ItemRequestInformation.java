package org.recap.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Optional;

/**
 * Created by hemalathas on 1/11/16.
 */
@Data
public class ItemRequestInformation {
    private List<String> itemBarcodes;
    private String titleIdentifier;
    private String itemOwningInstitution = "";
    private String patronBarcode = "";
    private String emailAddress = "";
    private String requestingInstitution = "";
    private String requestType = "";
    private String deliveryLocation = "";
    public String getDeliveryLocation() {
        return Optional.ofNullable(deliveryLocation).orElse("");
    }
    private String requestNotes = "";
    private String trackingId;
    private String author;
    private String callNumber;

    /**
     * EDD Request
     */
    private String startPage;
    private String endPage;
    private String chapterTitle = "";
    private String bibId;
    private String username;
    private String issue;
    private String volume;

    /**
     * Is owning institution item boolean.
     *
     * @return the boolean
     */
    @JsonIgnore
    public boolean isOwningInstitutionItem() {
        boolean bSuccess;
        if (itemOwningInstitution.equalsIgnoreCase(requestingInstitution)) {
            bSuccess = true;
        } else {
            bSuccess = false;
        }
        return bSuccess;
    }

    @Override
    public String toString() {
        return "ItemRequestInformation{" +
                "itemBarcodes=" + itemBarcodes +
                ", titleIdentifier='" + titleIdentifier + '\'' +
                ", itemOwningInstitution='" + itemOwningInstitution + '\'' +
                ", patronBarcode='" + patronBarcode + '\'' +
                ", emailAddress='" + emailAddress + '\'' +
                ", requestingInstitution='" + requestingInstitution + '\'' +
                ", requestType='" + requestType + '\'' +
                ", deliveryLocation='" + deliveryLocation + '\'' +
                ", requestNotes='" + requestNotes + '\'' +
                ", trackingId='" + trackingId + '\'' +
                ", author='" + author + '\'' +
                ", callNumber='" + callNumber + '\'' +
                ", startPage='" + startPage + '\'' +
                ", endPage='" + endPage + '\'' +
                ", chapterTitle='" + chapterTitle + '\'' +
                ", bibId='" + bibId + '\'' +
                ", username='" + username + '\'' +
                ", issue='" + issue + '\'' +
                ", volume='" + volume + '\'' +
                '}';
    }
}
package org.recap.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by sudhishk on 26/12/16.
 */
@ApiModel(value = "PatronInformationRequest", description = "Model for Requesting Patron Information")
@Data
public class PatronInformationRequest{

    @ApiModelProperty(name = "patronIdentifier", position = 0, required = true)
    private String patronIdentifier = "";
    @ApiModelProperty(name = "itemOwningInstitution", position = 1, required = true)
    private String itemOwningInstitution="";
}

package org.recap.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AccessionModelRequest {
    @ApiModelProperty(position = 1)
    private String imsLocationCode;
    @ApiModelProperty(position = 2)
    private List<AccessionRequest> accessionRequests;
}

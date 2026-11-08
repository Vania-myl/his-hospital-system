package com.his.inspection.dto;

import lombok.Data;

@Data
public class InspectionQueryDTO {
    private String caseNumber;
    private String realName;
    private String inspectionState;
    private String creationDate;
    private Integer page;
    private Integer size;
    private Integer offset;
}

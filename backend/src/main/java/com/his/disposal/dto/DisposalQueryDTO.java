package com.his.disposal.dto;

import lombok.Data;

@Data
public class DisposalQueryDTO {
    private String caseNumber;
    private String realName;
    private String disposalState;
    private String creationDate;
    private Integer page;
    private Integer size;
    private Integer offset;
}

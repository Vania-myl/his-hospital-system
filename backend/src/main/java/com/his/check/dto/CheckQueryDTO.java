package com.his.check.dto;

import lombok.Data;

@Data
public class CheckQueryDTO {
    private String caseNumber;
    private String realName;
    private String checkState;
    private String creationDate;
    private Integer page;
    private Integer size;
    private Integer offset;
}

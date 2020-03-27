package com.primer.githelper;

import lombok.Data;

@Data
public class GitParam {
    private String baseDir;
    private String account;
    private String exportExcelPath;
    private String startDate;
    private String startTime;
    private String endDate;
    private String endTime;
}

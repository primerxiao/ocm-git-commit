package com.primer.githelper;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

@Data
@NoArgsConstructor
public class GitCommitInfo {
    @ExcelProperty("工程")
    private String modeName;
    @ExcelProperty("修改文件")
    private String commitFile;
    @ExcelProperty("合计")
    private int fileNum;
    @ExcelProperty("提交人员")
    private String gitAccount;
    @ExcelProperty("说明，禅道号")
    private String commitMsg;
    @ExcelIgnore
    private ArrayList<String> msgList = new ArrayList<>();
    @ExcelIgnore
    private ArrayList<String> fileList = new ArrayList<>();
}

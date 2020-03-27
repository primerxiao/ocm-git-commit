package com.primer.githelper;

import java.util.ArrayList;

import com.alibaba.excel.EasyExcel;

import java.io.File;
import java.util.List;

public class Write {
    public static void main(String[] args) {
        String templateFileName = "d://0116.xlsx";
        String fileName = "d://writeDemo.xlsx";
        // 这里 需要指定写用哪个class去写，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
        EasyExcel.write(fileName, GitCommitInfo.class).withTemplate(templateFileName).sheet().doWrite(data());
    }

    public static List<GitCommitInfo> data() {
        List<GitCommitInfo> gitCommitInfos = new ArrayList<>();

        for (int i = 0; i < 1000; i++) {
            GitCommitInfo gitCommitInfo = new GitCommitInfo();
            gitCommitInfo.setModeName("" + i);
            gitCommitInfo.setCommitFile("" + i);
            gitCommitInfo.setFileNum(0);
            gitCommitInfo.setGitAccount("" + i);
            gitCommitInfo.setCommitMsg("" + i);

            gitCommitInfos.add(gitCommitInfo);

        }
        return gitCommitInfos;
    }

}

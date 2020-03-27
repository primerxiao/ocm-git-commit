package com.primer.githelper;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 执行cmd命令
 */
public class CmdProcess {
    //根据参数获取所有信息
    public List<GitCommitInfo> excuteCmd(GitParam gitParam) {
        List<GitCommitInfo> gitCommitInfos = new ArrayList<>();
        //获取文件路径
        String baseDir = gitParam.getBaseDir();
        File file = new File(baseDir);
        if (!file.exists()||!file.isDirectory()) {
            return null;
        }
        File[] files = file.listFiles();
        for (File file1 : files) {
            if (!file1.isDirectory()) {
                continue;
            }
            //判断.git文件是否存在 如果存在那么就是需要获取提交记录的模块
            if (!new File(file1.getAbsolutePath() + "//.git").exists()) {
                continue;
            }
            System.out.println(file1.getAbsolutePath());
            //执行cmd命令
            //"git log --no-merges --committer=\""+ui->account->text()+"\" --until=\""+ui->endTime->text()+"\" --since=\""+ui->startTime->text()+"\" --name-only --pretty=format:\"||||%H||||%s||||%ct||||\"";
            String cmd="git log --no-merges --committer=\""+gitParam.getAccount()+"\" --until=\""+gitParam.getEndDate()+" "+gitParam.getEndTime()+"\" --since=\""+gitParam.getStartDate()+" "+gitParam.getStartTime()+"\" --name-only --pretty=format:\"----%H----%s----%ct----\"";
            String s = execCMD(cmd, file1.getAbsolutePath());
            if (StringUtils.isEmpty(s)) {
                continue;
            }
            //这里是一个模块 一个模块会有多个文件列表以及提交信息
            GitCommitInfo gitCommitInfo = new GitCommitInfo();
            gitCommitInfo.setModeName(file1.getName());
            String[] split = s.split("----");
            for (int i = 0; i < split.length; i++) {
                if (i == 0) {
                    continue;
                }
                if (i%4==1) {
                    String msg = split[i + 1].trim();
                    if (!gitCommitInfo.getMsgList().contains(msg)) {
                        gitCommitInfo.getMsgList().add(msg);
                    }
                    String fileListStr = split[i + 3].trim();
                    String[] fileListArr = fileListStr.split("\n");
                    for (String s1 : fileListArr) {
                        if (!gitCommitInfo.getFileList().contains(s1.trim())) {
                            gitCommitInfo.getFileList().add(s1.trim());
                        }
                    }
                }

            }
            gitCommitInfo.setFileNum(gitCommitInfo.getFileList().size());
            gitCommitInfo.setCommitFile(StringUtils.join(gitCommitInfo.getFileList(), "\r\n"));
            gitCommitInfo.setCommitMsg(StringUtils.join(gitCommitInfo.getMsgList(), "\r\n"));
            gitCommitInfo.setGitAccount(gitParam.getAccount());
            gitCommitInfos.add(gitCommitInfo);
        }
        return gitCommitInfos;
    }
    //执行cmd命令，获取返回结果
    private String execCMD(String command,String workDir) {
        StringBuilder sb =new StringBuilder();
        try {
            Process process=Runtime.getRuntime().exec(command,null,new File(workDir));
            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(process.getInputStream(),"UTF-8"));
            String line;
            while((line=bufferedReader.readLine())!=null)
            {
                sb.append(line+"\n");
            }
        } catch (Exception e) {
            return e.toString();
        }
        return sb.toString();
    }
}

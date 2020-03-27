package com.primer.githelper;

import com.alibaba.excel.EasyExcel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    public TextField baseDirText;
    @FXML
    public Button loadBaseDirBtn;
    @FXML
    public TextField accountText;
    @FXML
    public DatePicker startDateText;
    @FXML
    public DatePicker endDateText;
    @FXML
    public TextField startTimeText;
    @FXML
    public TextField endTimeText;
    @FXML
    public TextField exportDirText;
    @FXML
    public Button showToConsoleBtn;
    @FXML
    public Button exportBtn;
    @FXML
    public Button clearBtn;
    @FXML
    public Label exportTipLabel;
    @FXML
    public Button openExportExcelBtn;
    @FXML
    public TextArea console;
    @FXML
    public Button saveConfigBtn;
    @FXML
    public Label exportDirLabel;

    private CmdProcess cmdProcess = new CmdProcess();

    public void initialize(URL location, ResourceBundle resources) {
        //初始化
        //隐藏控件
        openExportExcelBtn.setManaged(false);
        exportTipLabel.setManaged(false);
        exportDirText.setManaged(false);
        exportDirLabel.setManaged(false);
        //读取配置文件
        ArrayList<IniFileEntity> iniFileEntities = Ini4jUtils.readIniFile(new File("./config.ini"));
        if (!iniFileEntities.isEmpty()) {
            for (IniFileEntity iniFileEntity : iniFileEntities) {
                if (StringUtils.isEmpty(iniFileEntity.getValue())) {
                    continue;
                }
                if (iniFileEntity.getSection().equals("param") && iniFileEntity.getKey().equals("baseDir")) {
                    baseDirText.setText(iniFileEntity.getValue());
                    continue;
                }
                if (iniFileEntity.getSection().equals("param") && iniFileEntity.getKey().equals("account")) {
                    accountText.setText(iniFileEntity.getValue());
                    continue;
                }
                if (iniFileEntity.getSection().equals("param") && iniFileEntity.getKey().equals("exportExcelDir")) {
                    exportDirText.setText(iniFileEntity.getValue());
                    continue;
                }
                if (iniFileEntity.getSection().equals("param") && iniFileEntity.getKey().equals("startDateTime")) {
                    String[] startDateTime = iniFileEntity.getValue().split(" ");
                    if (StringUtils.isEmpty(startDateTime[1])) {
                        startDateTime[1] = "00:00:00";
                    }
                    startDateText.setValue(LocalDate.parse(startDateTime[0]));
                    startTimeText.setText(LocalTime.parse(startDateTime[1]).format(DateTimeFormatter.ofPattern("HH:mm:ss")));
                    continue;
                }
                if (iniFileEntity.getSection().equals("param") && iniFileEntity.getKey().equals("endDateTime")) {
                    String[] endDateTime = iniFileEntity.getValue().split(" ");
                    if (StringUtils.isEmpty(endDateTime[1])) {
                        endDateTime[1] = "00:00:00";
                    }
                    endDateText.setValue(LocalDate.parse(endDateTime[0]));
                    endTimeText.setText(LocalTime.parse(endDateTime[1]).format(DateTimeFormatter.ofPattern("HH:mm:ss")));
                    continue;
                }
            }
        }else {
            startDateText.setValue(LocalDate.now());
            endDateText.setValue(LocalDate.now().plusDays(1));
            endTimeText.setText("00:00:00");
            startTimeText.setText("00:00:00");
        }
        //展示
        showToConsoleBtn.setOnMouseClicked(e->{
            List<GitCommitInfo> gitCommitInfos = cmdProcess.excuteCmd(getCurrentParam());
            console.clear();
            for (GitCommitInfo gitCommitInfo : gitCommitInfos) {
                console.appendText("模块名：" + gitCommitInfo.getModeName() + "\r\n");
                console.appendText("\r\n");
                console.appendText("提交信息：" + "\r\n");
                console.appendText(StringUtils.join(gitCommitInfo.getMsgList(),"\r\n"));
                console.appendText("\r\n");
                console.appendText("提交文件：" + "\r\n");
                console.appendText(StringUtils.join(gitCommitInfo.getFileList(), "\r\n"));
            }

        });
        //导出
        exportBtn.setOnMouseClicked(e->{
            //获取参数
            GitParam currentParam = getCurrentParam();
            List<GitCommitInfo> gitCommitInfos = cmdProcess.excuteCmd(currentParam);
            if (gitCommitInfos.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("提示");
                alert.setHeaderText("提示信息：");
                alert.setContentText("数据为空");
                alert.showAndWait();
                return;
            }
            Map<String, String> map = System.getenv();
            String userName = map.get("USERNAME");// 获取用户名
            String fileName ="C:\\Users\\"+userName+"\\Documents\\"+LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HHmmss"))+".xlsx";

            String tpFileName = "./template.xlsx";

            // 这里 需要指定写用哪个class去读，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
            // 如果这里想使用03 则 传入excelType参数即可
            EasyExcel.write(fileName, GitCommitInfo.class).withTemplate(tpFileName).sheet("代码清单").needHead(false).doWrite(gitCommitInfos);
            exportTipLabel.setText(fileName);
            exportTipLabel.setManaged(true);
            openExportExcelBtn.setManaged(true);

        });
        //清除
        clearBtn.setOnMouseClicked(e-> console.clear());
        //打开导出的文件
        openExportExcelBtn.setOnMouseClicked(e->{
            if (exportTipLabel.getText().isEmpty()) {
                return;
            }
            try {
                Runtime.getRuntime().exec("cmd /c start "+ exportTipLabel.getText());
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        });
        //保存
        saveConfigBtn.setOnMouseClicked(e->saveConfig());
        loadBaseDirBtn.setOnMouseClicked(e->{
            DirectoryChooser directoryChooser=new DirectoryChooser();
            File file = directoryChooser.showDialog(new Stage());
            String path = file.getPath();//选择的文件夹路径
            if (StringUtils.isNotEmpty(path)) {
                baseDirText.setText(path);
            }
        });
    }

    private GitParam getCurrentParam() {
        GitParam gitParam = new GitParam();
        gitParam.setAccount(accountText.getText());
        gitParam.setBaseDir(baseDirText.getText());
        gitParam.setEndDate(endDateText.getPromptText());
        gitParam.setEndDate(endDateText.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        gitParam.setEndTime(endTimeText.getText());
        gitParam.setStartDate(startDateText.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        gitParam.setStartTime(startTimeText.getText());
        return gitParam;
    }
    private void saveConfig() {
        ArrayList<IniFileEntity> iniFileEntities = new ArrayList<>();
        iniFileEntities.add(new IniFileEntity("param", "baseDir", baseDirText.getText()));
        iniFileEntities.add(new IniFileEntity("param", "account", accountText.getText()));
        iniFileEntities.add(new IniFileEntity("param", "exportExcelDir", exportDirText.getText()));
        iniFileEntities.add(new IniFileEntity("param", "startDateTime", startDateText.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))+" "+startTimeText.getText()));
        iniFileEntities.add(new IniFileEntity("param", "endDateTime", endDateText.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))+" "+endTimeText.getText()));
        try {
            Ini4jUtils.writeIniFile("./config.ini",iniFileEntities);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

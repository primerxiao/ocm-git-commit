package com.primer.githelper;

import org.apache.commons.lang3.StringUtils;
import org.ini4j.Ini;
import org.ini4j.Profile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Ini4jUtils {
    public static boolean writeIniFile(String filePath, List<IniFileEntity> filecontent) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            file.createNewFile();
        }
        Ini ini = new Ini();
        ini.load(file);
        //将文件内容保存到ini对象中
        filecontent.stream().forEach((entity) -> {
            Profile.Section param = ini.get(entity.getSection());
            if (!Objects.isNull(param)) {
                String s = param.get(entity.getKey());
                if (StringUtils.isEmpty(s)) {
                    ini.add(entity.getSection(), entity.getKey(), entity.getValue());
                } else {
                    param.put(entity.getKey(), entity.getValue());
                }
            } else {
                ini.add(entity.getSection(), entity.getKey(), entity.getValue());
            }
        });
        //将文件内容保存到文件中
        ini.store(file);
        return true;
    }

    public static ArrayList<IniFileEntity> readIniFile(File iniFile) {

        ArrayList<IniFileEntity> iniFileEntities = new ArrayList<>();
        if (!iniFile.exists()) {
            return iniFileEntities;
        }
        try {
            Ini ini = new Ini();
            ini.load(iniFile);
            Profile.Section section = ini.get("param");

            IniFileEntity baseDir = new IniFileEntity();
            baseDir.setSection("param");
            baseDir.setKey("baseDir");
            baseDir.setValue(section.get("baseDir"));
            iniFileEntities.add(baseDir);

            IniFileEntity account = new IniFileEntity();
            account.setSection("param");
            account.setKey("account");
            account.setValue(section.get("account"));
            iniFileEntities.add(account);

            IniFileEntity exportExcelDir = new IniFileEntity();
            exportExcelDir.setSection("param");
            exportExcelDir.setKey("exportExcelDir");
            exportExcelDir.setValue(section.get("exportExcelDir"));
            iniFileEntities.add(exportExcelDir);

            IniFileEntity startDateTime = new IniFileEntity();
            startDateTime.setSection("param");
            startDateTime.setKey("startDateTime");
            startDateTime.setValue(section.get("startDateTime"));
            iniFileEntities.add(startDateTime);

            IniFileEntity endDateTime = new IniFileEntity();
            endDateTime.setSection("param");
            endDateTime.setKey("endDateTime");
            endDateTime.setValue(section.get("endDateTime"));
            iniFileEntities.add(endDateTime);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return iniFileEntities;
    }
}

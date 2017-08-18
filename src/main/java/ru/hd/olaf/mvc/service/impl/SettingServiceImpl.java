package ru.hd.olaf.mvc.service.impl;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hd.olaf.entities.Setting;
import ru.hd.olaf.mvc.repository.SettingRepository;
import ru.hd.olaf.mvc.service.SettingService;
import ru.hd.olaf.util.LogUtil;

import java.util.*;

/**
 * Created by d.v.hozyashev on 17.08.2017.
 */
@Service
public class SettingServiceImpl implements SettingService{

    @Autowired
    private SettingRepository settingRepository;

    private static final Logger logger = LoggerFactory.getLogger(SettingServiceImpl.class); //логгер

    public Setting getByName(String name) {
        logger.debug(LogUtil.getMethodName());
        return settingRepository.findByName(name);
    }

    public Setting save(Setting setting) {
        logger.debug(LogUtil.getMethodName());

        setting = settingRepository.save(setting);
        return setting;
    }

    public Map<String, String> getSettings() {
        logger.debug(LogUtil.getMethodName());

        Map<String, String> properties = new HashMap<String, String>();
        List<Setting> settings = Lists.newArrayList(settingRepository.findAll());

        for (Setting setting : settings){
            properties.put(setting.getName(), setting.getValue());
        }

        return properties;
    }

    public Set<String> getNamesAll() {
        Set<String> settings = getNamesColumns();

        settings.add("smtpHost");
        settings.add("smtpPort");
        settings.add("smtpLogin");
        settings.add("smtpPassword");
        settings.add("smtpSender");
        settings.add("smtpTitle");

        return settings;
    }

    public Set<String> getNamesColumns() {
        Set<String> settings = new HashSet<String>();

        settings.add("columnName");
        settings.add("columnCreateDate");
        settings.add("columnUpdateDate");
        settings.add("columnRisk");
        settings.add("columnRating");
        settings.add("columnBranchCode");

        return settings;
    }
}

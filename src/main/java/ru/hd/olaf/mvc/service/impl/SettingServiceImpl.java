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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
}

package ru.hd.olaf.mvc.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.hd.olaf.entities.Setting;
import ru.hd.olaf.mvc.service.SettingService;
import ru.hd.olaf.util.JsonResponse;
import ru.hd.olaf.util.LogUtil;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by d.v.hozyashev on 16.08.2017.
 */
@Controller
public class DataController {

    @Autowired
    private SettingService settingService;

    private final static Set<String> settings = initSettingsSet();

    private static final Logger logger = LoggerFactory.getLogger(DataController.class);

    @RequestMapping(value = "/data", method = RequestMethod.GET)
    public String getViewData(Model model) {
        logger.debug(LogUtil.getMethodName());

        for (String param : settings) {
            Setting value = settingService.getByName(param);

            if (value != null) {
                model.addAttribute(param, value.getValue());
            }
        }

        return "/data/data";
    }

    @RequestMapping(value = "/data/settings/save", method = RequestMethod.POST)
    public
    @ResponseBody
    JsonResponse saveSettings(@RequestParam Map<String, String> settings) {
        logger.debug(LogUtil.getMethodName());

        JsonResponse response;
        try {
            for (Map.Entry<String, String> param : settings.entrySet()) {
                Setting setting = settingService.getByName(param.getKey());

                if (setting == null)
                    setting = new Setting(param.getKey());

                setting.setValue(param.getValue());

                settingService.save(setting);
            }
            response = new JsonResponse("Настройки сохранены");
        } catch (Exception e) {
            e.printStackTrace();
            String message = String.format("Возникла ошибка при сохранении настроек: %s", e.getMessage());
            logger.debug(message);
            response = new JsonResponse(message);
        }

        return response;
    }

    private static Set<String> initSettingsSet() {
        Set<String> settings = new HashSet<String>();

        settings.add("columnName");
        settings.add("columnCreateDate");
        settings.add("columnUpdateDate");
        settings.add("columnBranchCode");
        settings.add("smtpHost");
        settings.add("smtpPort");
        settings.add("smtpLogin");
        settings.add("smtpPassword");
        settings.add("smtpSender");
        settings.add("smtpTitle");

        return settings;
    }
}

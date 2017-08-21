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
import ru.hd.olaf.mvc.service.ClientService;
import ru.hd.olaf.mvc.service.SettingService;
import ru.hd.olaf.util.JsonResponse;
import ru.hd.olaf.util.LogUtil;
import ru.hd.olaf.xls.XLSGenerator;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Map;

/**
 * Created by d.v.hozyashev on 16.08.2017.
 */
@Controller
public class DataController {

    @Autowired
    private SettingService settingService;
    @Autowired
    private ClientService clientService;

    private static final Logger logger = LoggerFactory.getLogger(DataController.class);

    @RequestMapping(value = "/data", method = RequestMethod.GET)
    public String getViewData(Model model) {
        logger.debug(LogUtil.getMethodName());

        for (String param : settingService.getNamesAll()) {
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

    @RequestMapping(value = "/data/overdue", method = RequestMethod.GET)
    public void getRepost(HttpServletResponse response) {
        logger.debug(LogUtil.getMethodName());

        try {
            ByteArrayOutputStream bytesOutput = XLSGenerator.createXLSByteArray(
                    clientService.getOverdueClients(),
                    clientService.getRiskClients(),
                    clientService.getRatingClients()
            );

            InputStream inputStream = new BufferedInputStream(new ByteArrayInputStream(bytesOutput.toByteArray()));

            response.setContentType("application/x-download");
            response.setContentLength(bytesOutput.size());
            response.setHeader("Content-disposition", "attachment; filename=\"overdue.xls\"");

            org.apache.commons.io.IOUtils.copy(inputStream, response.getOutputStream());
            response.getOutputStream().close();

            logger.debug("Обработка завершена - сформированные данные переданы в response");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

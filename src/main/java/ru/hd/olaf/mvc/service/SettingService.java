package ru.hd.olaf.mvc.service;

import ru.hd.olaf.entities.Setting;

import java.util.Map;

/**
 * Created by d.v.hozyashev on 17.08.2017.
 */
public interface SettingService {

    Setting getByName(String name);

    Setting save(Setting setting);

    Map<String, String> getSettings();
}

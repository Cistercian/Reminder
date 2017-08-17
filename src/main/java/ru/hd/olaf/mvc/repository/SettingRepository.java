package ru.hd.olaf.mvc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.hd.olaf.entities.Setting;

/**
 * Created by d.v.hozyashev on 17.08.2017.
 */
public interface SettingRepository extends JpaRepository<Setting, Integer> {

    Setting findByName(String name);
}

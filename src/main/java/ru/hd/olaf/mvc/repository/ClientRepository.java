package ru.hd.olaf.mvc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.hd.olaf.entities.Client;

import java.util.List;

/**
 * Created by d.v.hozyashev on 16.08.2017.
 */
public interface ClientRepository extends JpaRepository<Client, Integer>{

    Client findByName(String name);

    @Query("SELECT c FROM Client c WHERE c.updateDate IS NULL OR DATEDIFF(CURRENT_DATE, c.updateDate) > 365")
    List<Client> getOverdue();
}

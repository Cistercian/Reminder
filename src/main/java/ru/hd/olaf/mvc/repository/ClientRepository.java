package ru.hd.olaf.mvc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.hd.olaf.entities.Client;

/**
 * Created by d.v.hozyashev on 16.08.2017.
 */
public interface ClientRepository extends JpaRepository<Client, Integer>{

    Client findByName(String name);

}

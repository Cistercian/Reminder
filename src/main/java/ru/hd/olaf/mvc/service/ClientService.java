package ru.hd.olaf.mvc.service;

import ru.hd.olaf.entities.Client;

import java.util.List;

/**
 * Created by d.v.hozyashev on 16.08.2017.
 */
public interface ClientService {

    Client getByName(String name);

    Client createOrUpdate(Client client);

    String getReport();

    Long getCountTotal();

    Long getCountOverdue();

    List<Client> getOverdueClients();
}

package ru.hd.olaf.mvc.service;

import ru.hd.olaf.entities.Client;
import ru.hd.olaf.util.ErrorsCountEntity;

import java.util.List;

/**
 * Created by d.v.hozyashev on 16.08.2017.
 */
public interface ClientService {

    Client getByName(String name);

    Client createOrUpdate(Client client);

    String getReport();

    Long getCountTotal();

    long getCountErrors();

    List<Client> getOverdueClients();

    List<Client> getRiskClients();

    List<Client> getRatingClients();

    List<ErrorsCountEntity> getStats();

    List<ErrorsCountEntity> getErrorsOverdue();

    List<ErrorsCountEntity> getErrorsRisk();

    List<ErrorsCountEntity> getErrorsRating();

    void deleteAllData();
}

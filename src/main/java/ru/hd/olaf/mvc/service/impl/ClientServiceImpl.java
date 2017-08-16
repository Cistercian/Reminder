package ru.hd.olaf.mvc.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hd.olaf.entities.Client;
import ru.hd.olaf.mvc.repository.ClientRepository;
import ru.hd.olaf.mvc.service.ClientService;
import ru.hd.olaf.util.LogUtil;

/**
 * Created by d.v.hozyashev on 16.08.2017.
 */
@Service
public class ClientServiceImpl implements ClientService {

    private static final Logger logger = LoggerFactory.getLogger(ClientServiceImpl.class); //логгер

    @Autowired
    private ClientRepository clientRepository;

    public Client getByName(String name) {
        logger.debug(LogUtil.getMethodName());
        return clientRepository.findByName(name);
    }

    public Client createOrUpdate(Client client) {
        logger.debug(LogUtil.getMethodName());
        Client existedClient = getByName(client.getName());

        if (existedClient != null) {
            logger.debug("Данный клиент уже есть в базе - обновляем его данные.");
            existedClient.setCreateDate(client.getCreateDate());
            existedClient.setUpdateDate(client.getUpdateDate());
            existedClient.setRisk(client.getRisk());
            existedClient.setRating(client.getRating());
            existedClient.setBranch(client.getBranch());

            return clientRepository.save(existedClient);
        } else {
            logger.debug("Клиент новый - сохраняем в БД.");
            return clientRepository.save(client);
        }
    }
}

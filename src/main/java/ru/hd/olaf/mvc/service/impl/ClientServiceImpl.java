package ru.hd.olaf.mvc.service.impl;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hd.olaf.entities.Branch;
import ru.hd.olaf.entities.Client;
import ru.hd.olaf.mvc.repository.ClientRepository;
import ru.hd.olaf.mvc.service.ClientService;
import ru.hd.olaf.util.ErrorsCountEntity;
import ru.hd.olaf.util.LogUtil;

import java.util.*;

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
        List<Client> clients = Lists.newArrayList(clientRepository.findByName(name));

        return clients.size() > 0 ? clients.get(0) : null;
    }

    public Client createOrUpdate(Client client) {
        logger.debug(LogUtil.getMethodName());
        Client existedClient = getByName(client.getName());

        try {
            if (existedClient != null) {
                logger.debug("Данный клиент уже есть в базе - обновляем его данные.");

                if (!client.equals(existedClient)) {
                    existedClient.setCreateDate(client.getCreateDate());
                    existedClient.setUpdateDate(client.getUpdateDate());
                    existedClient.setRisk(client.getRisk());
                    existedClient.setRating(client.getRating());
                    existedClient.setBranch(client.getBranch());

                    return clientRepository.save(existedClient);
                } else
                    return client;
            } else {
                logger.debug("Клиент новый - сохраняем в БД.");

                return clientRepository.save(client);

            }
        } catch (Exception e) {
            throw new RuntimeException(String.format("Ошибка сохранения клиента в БД: %s",
                    ExceptionUtils.getRootCause(e).getMessage()));
        }
    }

    public String getReport() {
        logger.debug(LogUtil.getMethodName());

        return null;
    }

    public Long getCountTotal() {
        return clientRepository.count();
    }

    public long getCountErrors() {
        long count = 0;
        for (ErrorsCountEntity errors : getStats()) {
            count += errors.getCount();
        }
        return count;
    }

    public List<Client> getOverdueClients() {
        logger.debug(LogUtil.getMethodName());
        return Lists.newArrayList(clientRepository.getOverdue());
    }

    public List<Client> getRiskClients() {
        logger.debug(LogUtil.getMethodName());
        return Lists.newArrayList(clientRepository.getRisk());
    }

    public List<Client> getRatingClients() {
        logger.debug(LogUtil.getMethodName());
        List<Client> clients = new ArrayList<Client>();

        for (Client client : Lists.newArrayList(clientRepository.findAll())) {
            String rating = client.getRating();

            if (hasError(rating))
                clients.add(client);
        }

        Collections.sort(clients, new Comparator<Client>() {
            public int compare(Client o1, Client o2) {
                return o1.getBranch().getCode().compareTo(o2.getBranch().getCode());
            }
        });

        return clients;
    }

    public List<ErrorsCountEntity> getStats() {
        logger.debug(LogUtil.getMethodName());

        List<ErrorsCountEntity> result = new ArrayList<ErrorsCountEntity>();

        mergeLists(result, getErrorsOverdue());
        mergeLists(result, getErrorsRisk());
        mergeLists(result, getErrorsRating());

        return result;
    }

    public List<ErrorsCountEntity> getErrorsOverdue() {
        logger.debug(LogUtil.getMethodName());
        return Lists.newArrayList(clientRepository.getErrorsOverdue());
    }

    public List<ErrorsCountEntity> getErrorsRisk() {
        logger.debug(LogUtil.getMethodName());
        return Lists.newArrayList(clientRepository.getErrorsRisk());
    }

    public List<ErrorsCountEntity> getErrorsRating() {
        logger.debug(LogUtil.getMethodName());

        Map<Branch, Long> errors = new HashMap<Branch, Long>();
        List<ErrorsCountEntity> result = new ArrayList<ErrorsCountEntity>();

        for (Client client : Lists.newArrayList(clientRepository.findAll())) {
            String rating = client.getRating();

            if (hasError(rating)) {
                Branch branch = client.getBranch();
                if (errors.containsKey(branch))
                    errors.put(branch, errors.get(branch) + 1);
                else
                    errors.put(branch, 1L);
            }
        }

        for (Map.Entry<Branch, Long> entry : errors.entrySet()) {
            result.add(new ErrorsCountEntity(entry.getValue(), entry.getKey()));
        }

        return result;
    }

    private boolean hasError(String rating) {
        //проверка на пустоту
        if (rating == null || rating.trim().length() == 0)
            return true;

        //есть в строке буквы (исключаю знаки препинания и пробел) - значение корректное
        for (char letter : rating.toCharArray()) {
            if (letter < 48 || letter > 57) {
                if (letter != '.' && letter != ',' && letter != ';' && letter != ' ')
                    return false;
            }
        }

        return true;
    }

    private void mergeLists(List<ErrorsCountEntity> list, List<ErrorsCountEntity> src) {
        for (ErrorsCountEntity error : src) {

            if (list.contains(error)) {
                ErrorsCountEntity entity = list.get(list.indexOf(error));
                entity.setCount(entity.getCount() + error.getCount());
            } else
                list.add(error);

        }
    }

    public void deleteAllData() {
        logger.debug(LogUtil.getMethodName());

        clientRepository.deleteAll();
    }
}

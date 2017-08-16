package ru.hd.olaf.mvc.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hd.olaf.entities.Branch;
import ru.hd.olaf.mvc.repository.BrachRepository;
import ru.hd.olaf.mvc.service.BranchService;
import ru.hd.olaf.util.LogUtil;

/**
 * Created by d.v.hozyashev on 16.08.2017.
 */
@Service
public class BranchServiceImpl implements BranchService {

    private static final Logger logger = LoggerFactory.getLogger(ClientServiceImpl.class); //логгер

    @Autowired
    private BrachRepository brachRepository;

    public Branch getExistOrCreate(String code) {
        logger.debug(LogUtil.getMethodName());

        Branch branch = brachRepository.findByCode(code);

        logger.debug(String.format("Обработка подразделения с кодом: %s. Результат поиска подразделения в БД: %s",
                code, branch != null ? "найдено" : "не найдено"));

        return branch != null ? branch : brachRepository.save(new Branch(code));
    }
}

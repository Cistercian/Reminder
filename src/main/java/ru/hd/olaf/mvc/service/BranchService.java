package ru.hd.olaf.mvc.service;

import ru.hd.olaf.entities.Branch;

/**
 * Created by d.v.hozyashev on 16.08.2017.
 */
public interface BranchService {

    Branch getExistOrCreate(String code);

}

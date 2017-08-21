package ru.hd.olaf.mvc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.hd.olaf.entities.Branch;

/**
 * Created by d.v.hozyashev on 16.08.2017.
 */
public interface BranchRepository extends JpaRepository<Branch, Integer> {

    Branch findByCode(String code);

}

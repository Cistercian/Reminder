package ru.hd.olaf.mvc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.hd.olaf.entities.Client;
import ru.hd.olaf.util.OverdueGroupEntity;

import java.util.List;

/**
 * Created by d.v.hozyashev on 16.08.2017.
 */
public interface ClientRepository extends JpaRepository<Client, Integer>{

    List<Client> findByName(String name);

    long count();

    @Query("SELECT COUNT(c) FROM Client c WHERE c.updateDate IS NULL OR " +
            "(DATEDIFF(CURRENT_DATE, c.createDate) > 365 AND DATEDIFF(CURRENT_DATE, c.updateDate) > 365)")
    Long getCountOverdue();

    @Query("SELECT c FROM Client c WHERE c.updateDate IS NULL OR " +
            "(DATEDIFF(CURRENT_DATE, c.createDate) > 365 AND DATEDIFF(CURRENT_DATE, c.updateDate) > 365) " +
            "ORDER BY c.branch.code")
    List<Client> getOverdue();

    @Query("SELECT new ru.hd.olaf.util.OverdueGroupEntity(COUNT(c), c.branch) " +
            "FROM Client c " +
            "WHERE c.updateDate IS NULL OR " +
            "(DATEDIFF(CURRENT_DATE, c.createDate) > 365 AND DATEDIFF(CURRENT_DATE, c.updateDate) > 365)" +
            "GROUP BY c.branch")
    List<OverdueGroupEntity> getOverdueByBranch();
}

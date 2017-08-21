package ru.hd.olaf.mvc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.hd.olaf.entities.Client;
import ru.hd.olaf.util.ErrorsCountEntity;

import java.util.List;

/**
 * Created by d.v.hozyashev on 16.08.2017.
 */
public interface ClientRepository extends JpaRepository<Client, Integer> {

    List<Client> findByName(String name);

    long count();

    @Query("SELECT COUNT(c) FROM Client c WHERE c.updateDate IS NULL OR " +
            "(DATEDIFF(CURRENT_DATE, c.createDate) > 365 AND DATEDIFF(CURRENT_DATE, c.updateDate) > 365)")
    Long getCountOverdue();

    @Query("SELECT c FROM Client c WHERE c.updateDate IS NULL OR " +
            "(DATEDIFF(CURRENT_DATE, c.createDate) > 365 AND DATEDIFF(CURRENT_DATE, c.updateDate) > 365) " +
            "ORDER BY c.branch.code")
    List<Client> getOverdue();

    @Query("SELECT c FROM Client c " +
            "WHERE c.risk IS NULL OR c.risk = '' " +
            "ORDER BY c.branch.code")
    List<Client> getRisk();

    @Query("SELECT new ru.hd.olaf.util.ErrorsCountEntity(COUNT(c), c.branch) " +
            "FROM Client c " +
            "WHERE c.updateDate IS NULL OR " +
            "(DATEDIFF(CURRENT_DATE, c.createDate) > 365 AND DATEDIFF(CURRENT_DATE, c.updateDate) > 365)" +
            "GROUP BY c.branch " +
            "ORDER BY COUNT(c) DESC")
    List<ErrorsCountEntity> getErrorsOverdue();

    @Query("SELECT new ru.hd.olaf.util.ErrorsCountEntity(COUNT(c), c.branch) " +
            "FROM Client c " +
            "WHERE c.risk IS NULL OR c.risk = '' " +
            "GROUP BY c.branch " +
            "ORDER BY COUNT(c) DESC")
    List<ErrorsCountEntity> getErrorsRisk();

    @Query("DELETE FROM Client")
    @Modifying
    void deleteAll();
}

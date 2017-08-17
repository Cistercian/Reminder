package ru.hd.olaf.util;

import ru.hd.olaf.entities.Branch;

/**
 * Created by d.v.hozyashev on 17.08.2017.
 */
public class OverdueGroupEntity {
    private Long count;
    private String branchCode;

    public OverdueGroupEntity() {
    }

    public OverdueGroupEntity(Long count, Branch branchCode) {
        this.count = count;
        this.branchCode = branchCode.getCode();
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }
}

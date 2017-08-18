package ru.hd.olaf.util;

import ru.hd.olaf.entities.Branch;

/**
 * Created by d.v.hozyashev on 17.08.2017.
 */
public class ErrorsCountEntity {
    private Long count;
    private String branchCode;

    public ErrorsCountEntity() {
    }

    public ErrorsCountEntity(Long count, Branch branchCode) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ErrorsCountEntity that = (ErrorsCountEntity) o;

        return branchCode != null ? branchCode.equals(that.branchCode) : that.branchCode == null;

    }

    @Override
    public int hashCode() {
        return branchCode != null ? branchCode.hashCode() : 0;
    }
}

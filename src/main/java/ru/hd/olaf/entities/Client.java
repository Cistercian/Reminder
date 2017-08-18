package ru.hd.olaf.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by d.v.hozyashev on 16.08.2017.
 */
@Entity
@Table(name = "clients", schema = "reminder_db")
public class Client {
    private Integer id;
    private String name;
    private Date createDate;
    private Date updateDate;
    private String risk;
    private String rating;
    private Branch branch;

    public Client() {
    }

    public Client(String name, Date createDate, Date updateDate, Branch branch) {
        this.name = name;
        this.createDate = createDate;
        this.updateDate = updateDate;
        this.branch = branch;
    }

    public Client(String name, Date createDate, Date updateDate, String risk, String rating, Branch branch) {
        this.name = name;
        this.createDate = createDate;
        this.updateDate = updateDate;
        this.risk = risk;
        this.rating = rating;
        this.branch = branch;
    }

    @Id
    @Column(name = "ID", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Basic
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "create_date", nullable = true)
    @Temporal(TemporalType.DATE)
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @Basic
    @Column(name = "update_date", nullable = true)
    @Temporal(TemporalType.DATE)
    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    @Basic
    @Column(name = "risk", columnDefinition="TEXT")
    public String getRisk() {
        return risk;
    }

    public void setRisk(String risk) {
        this.risk = risk;
    }

    @Basic
    @Column(name = "rating", columnDefinition="TEXT")
    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }


    @ManyToOne()
    @JoinColumn(name = "branch_id")
    @JsonBackReference
    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Client client = (Client) o;

        if (name != null ? !name.equals(client.name) : client.name != null) return false;
        if (createDate != null ? !createDate.equals(client.createDate) : client.createDate != null) return false;
        if (updateDate != null ? !updateDate.equals(client.updateDate) : client.updateDate != null) return false;
        if (risk != null ? !risk.equals(client.risk) : client.risk != null) return false;
        if (rating != null ? !rating.equals(client.rating) : client.rating != null) return false;
        return branch != null ? branch.equals(client.branch) : client.branch == null;

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (createDate != null ? createDate.hashCode() : 0);
        result = 31 * result + (updateDate != null ? updateDate.hashCode() : 0);
        result = 31 * result + (risk != null ? risk.hashCode() : 0);
        result = 31 * result + (rating != null ? rating.hashCode() : 0);
        result = 31 * result + (branch != null ? branch.hashCode() : 0);
        return result;
    }
}

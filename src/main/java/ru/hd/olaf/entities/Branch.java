package ru.hd.olaf.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.util.Set;

/**
 * Created by d.v.hozyashev on 16.08.2017.
 */
@Entity
@Table(name = "branches", schema = "reminder_db")
public class Branch {
    private Integer id;
    private String code;
    private String name;
    private Set<Client> clients;

    public Branch() {
    }

    public Branch(String code) {
        this.code = code;
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
    @Column(name = "code")
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Basic
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "branch", orphanRemoval = false)
    @JsonBackReference
    public Set<Client> getClients() {
        return clients;
    }

    public void setClients(Set<Client> clients) {
        this.clients = clients;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Branch branch = (Branch) o;

        return code != null ? code.equals(branch.code) : branch.code == null;

    }

    @Override
    public int hashCode() {
        return code != null ? code.hashCode() : 0;
    }
}

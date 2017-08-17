package ru.hd.olaf.entities;

import javax.persistence.*;

/**
 * Created by d.v.hozyashev on 17.08.2017.
 */
@Entity
@Table(name = "settings", schema = "reminder_db")
public class Setting {
    private Integer id;
    private String name;
    private String value;

    public Setting() {
    }

    public Setting(String name) {
        this.name = name;
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
    @Column(name = "name", nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "value", nullable = false)
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

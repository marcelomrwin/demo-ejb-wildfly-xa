package com.redhat.servicec.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "TB_REGISTRY")
@NamedQuery(name = "Registry.findAll",query = "select r from Registry r order by r.id desc ")
public class Registry {
    @Id
    @NotNull
    private Long id;

    @Column(unique = true, nullable = false)
    @NotEmpty
    private String trxId;

    @Column(nullable = false)
    @NotEmpty
    private String info;

    @Column(nullable = false)
    @NotNull
    private LocalDateTime opDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTrxId() {
        return trxId;
    }

    public void setTrxId(String trxId) {
        this.trxId = trxId;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public LocalDateTime getOpDate() {
        return opDate;
    }

    public void setOpDate(LocalDateTime opDate) {
        this.opDate = opDate;
    }
}

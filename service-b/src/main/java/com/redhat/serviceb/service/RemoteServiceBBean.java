package com.redhat.serviceb.service;

import com.redhat.RemoteServiceB;
import com.redhat.RemoteServiceC;
import com.redhat.exception.BusinessException;
import com.redhat.service.utils.InfoUtils;
import com.redhat.service.utils.log.Logged;
import com.redhat.serviceb.entity.Registry;

import jakarta.ejb.*;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.time.LocalDateTime;
import java.util.List;

@Stateless
@Remote(RemoteServiceB.class)
@Local(LocalServiceB.class)
public class RemoteServiceBBean implements RemoteServiceB, LocalServiceB {

    @PersistenceContext
    private EntityManager em;

    @Inject
    private RemoteServiceC remoteServiceC;

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    @Logged
    public String ping() {
        return "Pong from %s %s".formatted(getClass().getName(), InfoUtils.getHostInfo()) + "\n" + remoteServiceC.ping();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Logged
    public Long createRegistry(String txtId) {

        Registry entity = new Registry();
        entity.setTrxId(txtId);
        entity.setInfo("Service B entity info");
        entity.setOpDate(LocalDateTime.now());
        em.persist(entity);

        remoteServiceC.createRegistry(entity.getId(), txtId);

        return entity.getId();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Logged
    @Override
    public Long failOnC(String txtId) throws BusinessException {
        Registry entity = new Registry();
        entity.setTrxId(txtId);
        entity.setInfo("Service B entity info");
        entity.setOpDate(LocalDateTime.now());
        em.persist(entity);

        remoteServiceC.raiseError(entity.getId(), txtId);

        return entity.getId();
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    @Logged
    public List<Registry> listRegistries() {
        return em.createNamedQuery("Registry.findAll", Registry.class).getResultList();
    }
}

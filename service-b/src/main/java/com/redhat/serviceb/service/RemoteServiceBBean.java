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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Stateless
@Remote(RemoteServiceB.class)
@Local(LocalServiceB.class)
public class RemoteServiceBBean implements RemoteServiceB, LocalServiceB {

    @PersistenceContext
    private EntityManager em;

    @Inject
    private RemoteServiceC remoteServiceC;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    @Logged
    public String ping() {
        return "Pong from %s %s".formatted(getClass().getName(), InfoUtils.getHostInfo()) + "\n" + remoteServiceC.ping();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Logged
    public Long createRegistry(String txtId) {

        Registry entity = getRegistry(txtId);
        em.persist(entity);

        remoteServiceC.createRegistry(entity.getId(), txtId);

        return entity.getId();
    }

    private Registry getRegistry(String txtId) {
        Registry entity = new Registry();
        entity.setTrxId(txtId);
        entity.setInfo("Service B entity info");
        entity.setOpDate(LocalDateTime.now());
        return entity;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Logged
    @Override
    public Long failOnC(String txtId) throws BusinessException {
        Registry entity = getRegistry(txtId);
        em.persist(entity);

        remoteServiceC.raiseError(entity.getId(), txtId);

        return entity.getId();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Logged
    @Override
    public Long xaRecovery(String uuid) {
        Registry entity = getRegistry(uuid);
        em.persist(entity);
        remoteServiceC.xaRecovery(entity.getId(), uuid);
        return entity.getId();
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    @Logged
    public List<Registry> listRegistries() {
        return em.createNamedQuery("Registry.findAll", Registry.class).getResultList();
    }
}

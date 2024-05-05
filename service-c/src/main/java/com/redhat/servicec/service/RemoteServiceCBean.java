package com.redhat.servicec.service;


import com.redhat.RemoteServiceC;

import com.redhat.exception.BusinessException;
import com.redhat.service.utils.InfoUtils;
import com.redhat.service.utils.log.Logged;
import com.redhat.servicec.entity.Registry;
import jakarta.ejb.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Stateless
@Remote(RemoteServiceC.class)
@Local(LocalServiceC.class)
public class RemoteServiceCBean implements RemoteServiceC,LocalServiceC {

    @PersistenceContext
    private EntityManager em;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    @Logged
    public String ping() {
        return "Pong from %s %s".formatted(getClass().getName(), InfoUtils.getHostInfo());
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Logged
    @Override
    public void createRegistry(Long registryId, String txtId) {

        Registry entity = new Registry();
        entity.setId(registryId);
        entity.setTrxId(txtId);
        entity.setInfo("Service C entity info");
        entity.setOpDate(LocalDateTime.now());

        em.persist(entity);

    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Logged
    @Override
    public void raiseError(Long id, String txtId) throws BusinessException {
        createRegistry(id, txtId);
//        throw new RuntimeException("Service C raised error");
        throw new BusinessException("Service C raise an Exception",txtId);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Logged
    @Override
    public void xaRecovery(Long id, String uuid) {
        logger.info("Placing the current thread on hold\nShutdown service-b to observe the transaction recovery operation.\nUsing podman: podman compose down serviceb and then after few seconds podman compose up serviceb -d\nUsing local server: just shutdown and restart the wildfly-b instance");
        try {
            createRegistry(id, uuid);
            TimeUnit.SECONDS.sleep(45);
        } catch (InterruptedException e) {
            logger.error("This error should not occur", e);
            throw new RuntimeException(e);
        }
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    @Logged
    public List<Registry> listRegistries() {
        return em.createNamedQuery("Registry.findAll", Registry.class).getResultList();
    }

}

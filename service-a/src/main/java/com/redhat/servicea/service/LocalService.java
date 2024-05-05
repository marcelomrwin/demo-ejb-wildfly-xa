package com.redhat.servicea.service;

import com.redhat.RemoteServiceB;
import com.redhat.exception.BusinessException;
import com.redhat.service.utils.InfoUtils;
import com.redhat.service.utils.log.Logged;
import com.redhat.servicea.entity.Registry;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Stateless
public class LocalService {
    @Inject
    RemoteServiceB remoteServiceB;

    @PersistenceContext
    private EntityManager em;

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public String pingRemoteService() {
        return "Pong from %s %s".formatted(getClass().getName(), InfoUtils.getHostInfo())+"\n"+remoteServiceB.ping();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public String processAll(String uuid) {
        Registry registry = new Registry();
        registry.setInfo("Registry created after service B");
        registry.setTrxId(uuid);
        Long entityId = remoteServiceB.createRegistry(uuid);
        registry.setId(entityId);
        registry.setOpDate(LocalDateTime.now());
        em.persist(registry);

        return "The transaction %s was concluded with the Id %d".formatted(uuid, entityId);

    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    @Logged
    public List<Registry> findAll() {
        return em.createNamedQuery("Registry.findAll", Registry.class).getResultList();
    }

    /**
     * Fail calls
     **/

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public String failOnC() throws BusinessException {
        String uuid = UUID.randomUUID().toString();
        Registry registry = new Registry();
        registry.setInfo("Registry MUST fail in service C");
        registry.setTrxId(uuid);
        Long entityId = remoteServiceB.failOnC(uuid);
        registry.setId(entityId);
        registry.setOpDate(LocalDateTime.now());
        em.persist(registry);

        return "Transaction %s was concluded with the Id %d".formatted(uuid, entityId);

    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public String failOnAafterBandC() throws BusinessException {
        String uuid = UUID.randomUUID().toString();
        processAll(uuid);
        throw new BusinessException("This transaction should not be commited", uuid);
    }
}

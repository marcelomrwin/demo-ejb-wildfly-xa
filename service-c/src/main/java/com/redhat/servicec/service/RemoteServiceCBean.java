package com.redhat.servicec.service;


import com.redhat.RemoteServiceC;

import com.redhat.exception.BusinessException;
import com.redhat.service.utils.InfoUtils;
import com.redhat.service.utils.log.Logged;
import com.redhat.servicec.entity.Registry;
import jakarta.ejb.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.time.LocalDateTime;
import java.util.List;

@Stateless
@Remote(RemoteServiceC.class)
@Local(LocalServiceC.class)
public class RemoteServiceCBean implements RemoteServiceC,LocalServiceC {

    @PersistenceContext
    private EntityManager em;

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    @Logged
    public String ping() {
        return "Pong from %s %s".formatted(getClass().getName(), InfoUtils.getHostInfo());
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Logged
    @Override
    public Long createRegistry(Long registryId, String txtId) {

        Registry entity = new Registry();
        entity.setId(registryId);
        entity.setTrxId(txtId);
        entity.setInfo("Service C entity info");
        entity.setOpDate(LocalDateTime.now());

        em.persist(entity);

        return entity.getId();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Logged
    @Override
    public void raiseError(Long id, String txtId) throws BusinessException {
        createRegistry(id, txtId);
//        throw new RuntimeException("Service C raised error");
        throw new BusinessException("Service C raise an Exception",txtId);
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    @Logged
    public List<Registry> listRegistries() {
        return em.createNamedQuery("Registry.findAll", Registry.class).getResultList();
    }

}

package com.redhat;

import com.redhat.exception.BusinessException;

public interface RemoteServiceC {
    String DEFAULT_BEAN_NAME = RemoteServiceC.class.getSimpleName() + "Bean";
    String ping();
    void createRegistry(Long registryId, String txId);

    void raiseError(Long id, String txId) throws BusinessException;

    void xaRecovery(Long id, String uuid);
}

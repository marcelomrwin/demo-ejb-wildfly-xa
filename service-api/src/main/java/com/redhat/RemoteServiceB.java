package com.redhat;

import com.redhat.exception.BusinessException;

public interface RemoteServiceB {
    String DEFAULT_BEAN_NAME = RemoteServiceB.class.getSimpleName() + "Bean";

    String ping();

    Long createRegistry(String txId);

    Long failOnC(String uuid) throws BusinessException;
}

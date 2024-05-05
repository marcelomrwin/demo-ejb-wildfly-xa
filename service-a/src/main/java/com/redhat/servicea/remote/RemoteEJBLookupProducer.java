package com.redhat.servicea.remote;

import com.redhat.RemoteServiceB;
import com.redhat.service.utils.remote.RemoteLookupHelper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.NamingException;

@ApplicationScoped
public class RemoteEJBLookupProducer {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private static final String REMOTE_DEPLOYMENT_NAME = System.getProperty("remote.deployment.name", "service-b");

    @Produces
    public RemoteServiceB produceRemoteEJB() throws NamingException {
        logger.info("Requesting Remote Interface {}", RemoteServiceB.class.getName());
        return RemoteLookupHelper.lookupRemoteEJBOutbound(RemoteServiceB.DEFAULT_BEAN_NAME, RemoteServiceB.class, REMOTE_DEPLOYMENT_NAME);
    }

}

package com.redhat.serviceb.remote;

import com.redhat.RemoteServiceC;
import com.redhat.service.utils.remote.RemoteLookupHelper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.NamingException;

@ApplicationScoped
public class RemoteEJBLookupProducer {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private static final String REMOTE_DEPLOYMENT_NAME = System.getProperty("remote.deployment.name", "service-c");

    @Produces
    public RemoteServiceC produceRemoteEJB() throws NamingException {
        logger.info("Requesting Remote Interface {}", RemoteServiceC.class.getName());
        return RemoteLookupHelper.lookupRemoteEJBOutbound(RemoteServiceC.DEFAULT_BEAN_NAME, RemoteServiceC.class, REMOTE_DEPLOYMENT_NAME);
    }

}

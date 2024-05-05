package com.redhat.service.utils.remote;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

public class RemoteLookupHelper {
    private static Logger logger = LoggerFactory.getLogger(RemoteLookupHelper.class);
    private static final String JNDI_PKG_PREFIXES = "org.jboss.ejb.client.naming";

    public static <T> T lookupRemoteEJBOutbound(String beanImplName, Class<T> remoteInterface, String appContext) throws NamingException {
        final Properties jndiProperties = new Properties();
        jndiProperties.put(Context.URL_PKG_PREFIXES, JNDI_PKG_PREFIXES);
        final Context context = new InitialContext(jndiProperties);

        String jndiLookup = "ejb:/" + appContext + "/" + beanImplName + "!"
                + remoteInterface.getName();
        logger.info("Looking up EJB outbound bean with name {}", jndiLookup);
        return (T) context.lookup(jndiLookup);
    }
}

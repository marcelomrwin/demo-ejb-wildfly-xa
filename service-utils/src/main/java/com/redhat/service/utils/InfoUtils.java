package com.redhat.service.utils;


import jakarta.transaction.Status;
import jakarta.transaction.TransactionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.InitialContext;
import java.net.InetAddress;
import java.net.UnknownHostException;


/**
 * Utility class with generic conversions and info getters.
 */
public final class InfoUtils {
    private static final Logger log = LoggerFactory.getLogger(InfoUtils.class);

    private InfoUtils() throws IllegalAccessException {
        throw new IllegalAccessException("utility class, do not instantiate");
    }

    /**
     * To get information about hostname and jboss node name.
     *
     * @return string with information about hostname and jboss node name
     */
    public static String getHostInfo() {
        String currentIpHost = "<<unknown hostname>>";
        try {
            InetAddress ip = InetAddress.getLocalHost();
            currentIpHost = ip.toString();
        } catch (UnknownHostException e) {
            log.warn("Cannot get current IP and hostname. Cause: %s", e);
        }
        String jbossNodeName = System.getProperty("jboss.node.name");
        return String.format("host: %s, jboss node name: %s", currentIpHost, jbossNodeName);
    }

    /**
     * Converting transaction status code as int to string representation,
     * see {@link Status}.
     *
     * @param status status code as integer
     * @return string representation of the transaction status code
     */
    public static String transactionStatusAsString(int status) {
        switch (status) {
            case Status.STATUS_ACTIVE:
                return "jakarta.transaction.Status.STATUS_ACTIVE";
            case Status.STATUS_COMMITTED:
                return "jakarta.transaction.Status.STATUS_COMMITTED";
            case Status.STATUS_MARKED_ROLLBACK:
                return "jakarta.transaction.Status.STATUS_MARKED_ROLLBACK";
            case Status.STATUS_NO_TRANSACTION:
                return "jakarta.transaction.Status.STATUS_NO_TRANSACTION";
            case Status.STATUS_PREPARED:
                return "jakarta.transaction.Status.STATUS_PREPARED";
            case Status.STATUS_PREPARING:
                return "jakarta.transaction.Status.STATUS_PREPARING";
            case Status.STATUS_ROLLEDBACK:
                return "jakarta.transaction.Status.STATUS_ROLLEDBACK";
            case Status.STATUS_ROLLING_BACK:
                return "jakarta.transaction.Status.STATUS_ROLLING_BACK";
            case Status.STATUS_UNKNOWN:
            default:
                return "jakarta.transaction.Status.STATUS_UNKNOWN";
        }
    }

    public static String getTransactionStatus() {
        int statusCode = Status.STATUS_UNKNOWN;

        try {
            InitialContext ctx = new InitialContext();
            TransactionManager tm = (TransactionManager) ctx.lookup("java:/TransactionManager");
            statusCode = tm.getStatus();
        } catch (Exception e) {
            log.warn("Cannot get transaction manager at JNDI binding 'java:/TransactionManager'", e);
            return "error to obtain transaction manager";
        }

        return transactionStatusAsString(statusCode);
    }
}

package edu.jespinoza.service;

import java.util.Collection;

public interface OracleTestConnectionService {

    Collection<String> checkConnection(String driver, String url,
                                       String user, String password)
            throws Exception;
}

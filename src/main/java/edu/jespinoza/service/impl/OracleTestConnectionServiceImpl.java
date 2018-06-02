package edu.jespinoza.service.impl;

import edu.jespinoza.service.OracleTestConnectionService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class OracleTestConnectionServiceImpl implements OracleTestConnectionService {
    private static OracleTestConnectionServiceImpl ourInstance =
            new OracleTestConnectionServiceImpl();

    public static OracleTestConnectionServiceImpl getInstance() {
        return ourInstance;
    }

    private OracleTestConnectionServiceImpl() {
    }

    @Override
    public Collection<String> checkConnection(String driver, String url,
                                              String user, String password)
            throws Exception {
        Collection<String> list = new ArrayList<>();
        try {
            //  Cargamos la clase que implementa el Driver
            Class.forName(driver);

            //  Establecemos una conexion con la Base de Datos
            list.add("ESTABLECIENDO CONEXION CON " + url + "...");
            Connection conexion = DriverManager.getConnection(url,
                    user, password);
            list.add("CONEXION ESTABLECIDA.");
            // Liberamos recursos y cerramos la conexion
            conexion.close();
            list.add("CONEXION A " + url + " CERRADA.");
        } catch (SQLException ex) {
            //  Mostramos toda la informacion disponible sobre el error
            while (ex != null) {
                list.add("SQLState: " + ex.getSQLState());
                list.add("Mensaje: " + ex.getMessage());
                list.add("Vendedor: " + ex.getErrorCode());
                ex = ex.getNextException();
            }
        }
        return list;
    }
}

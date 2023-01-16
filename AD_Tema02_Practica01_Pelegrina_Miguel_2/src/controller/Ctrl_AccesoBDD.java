
package controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase Controlador que nos conecta con una BDD de Oracle del usuario AD_TEMA02 
 * @author migup
 */
public class Ctrl_AccesoBDD {
    /**
     * Metodo que nos conecta con la BDD de Oracle
     * @return Devuelve una conexion con la BDD o nulo en el caso de que no se 
     * haya podido establecer la conexion
     */
    public Connection conectarOracle(){
        Connection con = null;
        try {
            con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe",
                    "AD_TEMA02","AD_TEMA02");
        } catch (SQLException ex) {}
        return con;
    }
}


package controller;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import model.Alumno;
import view.Principal;

/**
 * Clase Controlador de los objetos de la clase Alumno 
 * @author migup
 */
public class Ctrl_Alumno {
    // Declaracion e inicializacion de variables
    private ArrayList<Alumno> listaAlumnos = new ArrayList<>();
    private Statement st;
    private String sentenciaSQL;
    private ResultSet resultados;
    
    /**
     * Metodo que realiza una consulta sobre la tabla Alumnos de la BDD
     * @return Devuelve un arraylist con objetos de la clase Alumno
     */
    public ArrayList<Alumno> consultarAlumno(){
        listaAlumnos.removeAll(listaAlumnos);        
        //listaAlumnos = new ArrayList<>();
        
        //comprobamos que la conexion sigue abierta
        if(Principal.connection != null){            
            try {      
                // Escribimos la sentencia SQL
                sentenciaSQL = "SELECT * FROM ALUMNOS";
                // Nos creamos el statement
                st = Principal.connection.createStatement();
                // Ejecutamos la consulta
                resultados = st.executeQuery(sentenciaSQL);
                // Recorremos el ResultSet
                while(resultados.next()){
                    // Por cada registro nos creamos un objeto de la clase 
                    // Alumno que anadimos al arraylist
                    listaAlumnos.add(new Alumno(
                            resultados.getString("ccodalu"),
                            resultados.getString("cnomalu")));
                }
                // Cerramos ResultSet 
                resultados.close();
                // Cerramos el statement
                st.close();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        } 
        
        return listaAlumnos;
    }
}

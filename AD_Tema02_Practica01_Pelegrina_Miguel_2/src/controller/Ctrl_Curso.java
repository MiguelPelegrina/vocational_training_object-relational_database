
package controller;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import model.Curso;
import view.Principal;

/**
 * Clase Controlador de los objetos de la clase Curso
 * @author migup
 */
public class Ctrl_Curso {
    // Declaracion e inicializacion de variables
    private ArrayList<Curso> listaCursos = new ArrayList<>();
    private Statement st;
    private String sentenciaSQL;
    private ResultSet resultados;
    
    /**
     * Metodo que realiza una consulta sobre la tabla Cursos de la BDD
     * @return Devuelve un arraylist con objetos de la clase Curso
     */
    public ArrayList<Curso> consultarCurso() {
        listaCursos.removeAll(listaCursos);        
        
        //comprobamos que la conexion sigue abierta
        if(Principal.connection != null){
            try {   
                // Escribimos la sentencia SQL
                sentenciaSQL = "SELECT * FROM CURSOS";   
                // Nos creamos el statement
                st = Principal.connection.createStatement();                
                // Ejecutamos la consulta
                resultados = st.executeQuery(sentenciaSQL);  
                // Recorremos el ResultSet
                while(resultados.next()){
                    // Por cada registro nos creamos un objeto de la clase Curso
                    // que anadimos al arraylist
                    listaCursos.add(new Curso(
                            resultados.getString("ccodcurso"),
                            resultados.getString("cnomcurso"),
                            resultados.getInt("nnumexa")
                    ));
                }               
                // Cerramos el ResultSet
                resultados.close();
                // Cerramos el statement
                st.close();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        } 
        
        return listaCursos;
    }
}

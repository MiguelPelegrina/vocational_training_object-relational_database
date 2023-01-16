
package controller;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import model.Examen;
import view.Principal;

/**
 * Clase Controlador de los objetos de la clase Examen
 * @author migup
 */
public class Ctrl_Examen {
    // Declaracion e inicializacion de variables
    private ArrayList<Examen> examenes = new ArrayList<>();
    private PreparedStatement psp;
    private CallableStatement cst;
    private String sentenciaSQL;
    private int numFilas;
    private ResultSet resultados;
    
    /**
     * Metodo que da de alta una examen en la tabla Examenes en la BDD
     * @param examen Examen cuyos datos se introduciran en la tabla
     * @return Devuelve el numero de filas afectadas por la insercion, devuelve 
     * -1 si no se ha podido realizar
     */
    public int altaExamen(Examen examen){
        numFilas = -1;
        // Comprobamos que la conexion siga en pie
        if(Principal.connection != null){
            // Instanciamos la sentencia
            sentenciaSQL = "INSERT INTO EXAMENES VALUES(?, ?, ?, ?, ?)";            
            try {
                // Preparamos la llamada al procedimiento
                psp = Principal.connection.prepareStatement(sentenciaSQL);
                // Asignamos el codigo del alumno
                psp.setString(1,examen.getCodAlumno());
                // Asignamos el codigo del curso
                psp.setString(2,examen.getCodCurso());
                // Asignamos el numero de examen
                psp.setInt(3,examen.getNumExamen());
                // Asignamos la fecha del examen
                psp.setString(4,examen.getDate());
                // Asignamos la nota del examen
                psp.setInt(5,examen.getNotaExamen());
                // Ejecutamos la sentencia que llama al procedimiento                
                numFilas = psp.executeUpdate();
                // Cerramos la sentencia
                psp.close();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        }
        
        return numFilas;
    }
        
    /**
     * Metodo que modifica un registro de la tabla examen
     * @param examen Objeto de la clase Examen que guarda la informacion necesaria
     * @return Devuelve el numero de filas afectadas
     */
    public int modificarExamen(Examen examen){
        numFilas = -1;
        
        if(Principal.connection != null){
            /*
            CREATE OR REPLACE PROCEDURE sp_ModificarExamen (
                xcCodAlu VARCHAR2, xcCodCurso VARCHAR2, xDFecExam VARCHAR2, 
                xnNotaExam NUMBER, xNumExamen NUMBER, xError OUT NUMBER) 
            AS
                xNR NUMBER;  
            BEGIN
                UPDATE examenes 
                SET dfecexam = TO_DATE(xDFecExam,'YYYY-MM-DD'), nNotaExam = xnNotaExam 
                WHERE cCodAlu = xcCodAlu AND cCodCurso = xcCodCurso AND nNumExam = xNumExamen;   
                UPDATE matriculas 
                SET nnotamedia = (SELECT AVG(nNotaExam) 
                FROM Examenes WHERE cCodAlu = xcCodAlu AND cCodCurso = xcCodCurso) 
                WHERE cCodAlu = xcCodAlu AND cCodCurso = xcCodCurso AND nNotaExam <> 0;
            EXCEPTION
                WHEN OTHERS THEN xError := -1;
            END;
            */       
            // Instaciamos la sentencia
            sentenciaSQL = "{call sp_ModificarExamen(?, ?, ?, ?, ?, ?)}";
            try {
                // Preparamos la llamada al procedimiento
                cst = Principal.connection.prepareCall(sentenciaSQL);
                // Asignamos el codigo del alumno
                cst.setString(1,examen.getCodAlumno());
                // Asignamos el codigo del curso
                cst.setString(2,examen.getCodCurso());   
                // Asignamos la fecha
                cst.setString(3,examen.getDate().trim());
                // Asignamos la nota
                cst.setInt(4,examen.getNotaExamen());
                // Asignamos el numero del examen
                cst.setInt(5,examen.getNumExamen());                
                // Asignamos la variable de salida que guarda el error
                cst.registerOutParameter(6,Types.NUMERIC);
                // Ejecutamos la sentencia
                cst.executeUpdate();
                // Cerramoas la sentencia 
                cst.close();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        }
        
        return numFilas;
    }
    
    /**
     * Metodo que realiza una consulta sobre la tabla Examenes de la BDD
     * @param codAlumno Codigo del alumno necesario para filtrar  
     * @param codCurso Codigo del curso necesario para filtrar
     * @return Devuelve un arrayList con objetos de la clase Examen
     */
    public ArrayList<Examen> consultarExamenes(String codAlumno, String codCurso){
        // Declaracion e inicializacion de variables
        examenes = new ArrayList<>();
        StringBuilder sbFecha;
        String fecha;
        
        if(Principal.connection != null){
            if(codAlumno.trim().equals("") && codCurso.trim().equals("")){
                // Consulta sin filtros
                sentenciaSQL = "SELECT * FROM examenes";
            }else{
                // Consulta con filtros
                sentenciaSQL = "SELECT * FROM examenes WHERE ccodalu = ? AND ccodcurso = ?";
                try {
                    // Preparamos la sentencia
                    psp = Principal.connection.prepareStatement(sentenciaSQL);
                    // Asignamos el codigo del alumno
                    psp.setString(1, codAlumno);
                    // Asignamos el codigo del curso
                    psp.setString(2, codCurso);
                    // Realizamos la consulta
                    resultados = psp.executeQuery();
                    // Recorremos el ResultSet
                    while(resultados.next()){
                        // El StringBuilder lo utilizamos para limitar la 
                        // longitud de la fecha de tal forma que NO salgan las
                        // horas, los minutos y los segundos
                        // Este bloque es para cuando se quiere escribir el JSON
                        // del alumnado cuyas fechas de examenes aun no estan
                        // a nulo
                        // Se podria hacer tambien parseando con un SimpleDateFormat
                        try{
                            sbFecha = new StringBuilder(resultados.getString("dfecexam"));
                            sbFecha.setLength(10);
                            fecha = sbFecha.toString();
                        }catch(NullPointerException npe){
                            fecha = "";
                        }
                        // Anadimos los examenes a la lista
                        examenes.add(new Examen(
                                resultados.getString("ccodalu"),
                                resultados.getString("ccodcurso"),
                                resultados.getInt("nnumexam"),
                                fecha,
                                resultados.getInt("nnotaexam")
                        ));
                    }
                    // cerramos el resultSet
                    resultados.close();
                    // cerramos el preparedStatement
                    psp.close();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                }
            }
        }
        
        return examenes;
    }
}

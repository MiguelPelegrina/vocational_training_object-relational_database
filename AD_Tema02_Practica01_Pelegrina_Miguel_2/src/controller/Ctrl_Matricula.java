/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import model.Matricula;
import view.Principal;

/**
 * Clase Controlador de los objetos de la clase Matricula
 * @author migup
 */
public class Ctrl_Matricula {
    // Declaracion e inicializacion de variables
    private ArrayList<Object[]> matriculas = new ArrayList<>();
    private CallableStatement cst;
    private PreparedStatement psp;
    private String sentenciaSQL;    
    private int numFilas;
    private ResultSet resultados;
    
    /**
     * Metodo que da de alta una matricula en la tabla matriculas de la BDD 
     * @param matricula Matricula cuyos datos se introduciran en la tabla
     * @return Devuelve el numero de filas afectadas por la insercion, devuelve 
     * -1 si no se ha podido realizar
     */
    public int altaMatricula(Matricula matricula){
        numFilas = -1;
        // Comprobamos que la conexion siga en pie
        if(Principal.connection != null){         
            /* Procedimiento almacenado en Oracle
            CREATE OR REPLACE PROCEDURE sp_AltaMatricula (
            xcCodAlu VARCHAR2, xcCodCurso VARCHAR2, xError OUT NUMBER) AS
                xNR NUMBER;
            BEGIN
                INSERT INTO matriculas VALUES(xcCodAlu,xcCodCurso,0);
            EXCEPTION
                WHEN OTHERS THEN xError := -1;
            END;
            */
            // Instaciamos la sentencia
            sentenciaSQL = "{call sp_AltaMatricula(?, ?, ?)}";            
            try {
                // Preparamos la llamada al procedimiento
                cst = Principal.connection.prepareCall(sentenciaSQL);
                // Asignamos el codigo del alumno
                cst.setString(1,matricula.getCodAlumno());
                // Asignamos el codigo del curso
                cst.setString(2,matricula.getCodCurso());
                // Asignamos la variable que guardara el error
                cst.registerOutParameter(3,Types.NUMERIC);    
                // Ejecutamos la sentencia que llama al procedimiento
                cst.executeUpdate();
                // Cerramos la sentencia
                cst.close();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        }
        
        return numFilas;
    }
    
    public ArrayList<Object[]> consultarMatriculas(String codAlumno){
        // Declaracion e inicializacion de variables
        matriculas = new ArrayList<>();
        if(Principal.connection != null){  
            /* Vista utilizada
            CREATE OR REPLACE VIEW vistaTablas AS 
            SELECT al.ccodalu, al.cnomalu, cu.ccodcurso, cu.cnomcurso, ma.nnotamedia, cu.nnumexa
            FROM alumnos al, cursos cu, matriculas ma
            WHERE al.ccodalu = ma.ccodalu AND cu.ccodcurso = ma.ccodcurso; 
            */
            // Instanciamos la sentencia
            sentenciaSQL = "SELECT * FROM VISTATABLAS WHERE ccodAlu = ?";
                         
            try {                
                // Preparamos la sentencia
                psp = Principal.connection.prepareStatement(sentenciaSQL);
                // Asignamos el codigo del alumno
                psp.setString(1, codAlumno);
                // Ejecutamos la sentencia
                resultados = psp.executeQuery();
                // Recorremos el ResultSet
                while(resultados.next()){
                    // Anadimos cada elemento de la lista
                    matriculas.add(new Object[]{
                        resultados.getString("ccodalu"),
                        resultados.getString("cnomAlu"),
                        resultados.getString("ccodcurso"),
                        resultados.getString("cnomCurso"),
                        resultados.getInt("nnotamedia")
                    });
                }
                // Cerramos el ResultSet
                resultados.close();
                // Cerramos el preparedStatement
                psp.close();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        }
        
        return matriculas;
    }
}

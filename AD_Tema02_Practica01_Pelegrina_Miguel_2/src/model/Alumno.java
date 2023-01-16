package model;

/**
 * Clase Alumno correspondiente a la tabla Alumnos de la BDD
 * @author migup
 */
public class Alumno {
    //Atributos de la instancia
    private String cod;
    private String nombre;

    /**
     * Constructor por parametros
     * @param cod
     * @param nombre 
     */
    public Alumno(String cod, String nombre) {
        this.cod = cod;
        this.nombre = nombre;
    }

    //getter
    public String getCod() {
        return cod;
    }

    public String getNombre() {
        return nombre;
    }
    
    //setter
    public void setCod(String cod) {
        this.cod = cod;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return cod + "-" + nombre;
    }   
}

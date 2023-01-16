package model;

/**
 * Clase Curso correspondiente a la tabla Cursos de la BDD
 * @author migup
 */
public class Curso {
    //Variables de la instancia
    private String cod;
    private String nombre;
    private int numExamenes;

    /**
     * Constructor por parametros
     * @param cod
     * @param nombre
     * @param numExamenes 
     */
    public Curso(String cod, String nombre, int numExamenes) {
        this.cod = cod;
        this.nombre = nombre;
        this.numExamenes = numExamenes;
    }
    
    //getter
    public String getCod() {
        return cod;
    }

    public String getNombre() {
        return nombre;
    }

    
    public int getNumExamenes() {
        return numExamenes;
    }
    
    //setter
    public void setCod(String cod) {
        this.cod = cod;
    }    
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setNumExamenes(int numExamenes) {
        this.numExamenes = numExamenes;
    }

    @Override
    public String toString() {
        return cod + "-" + nombre + "-" + numExamenes;
    }    
}

package model;

/**
 * Clase Examen correspondiente a la tabla Examanes de la BDD
 * @author migup
 */
public class Examen {
    //Atributos de la instancia
    private String codAlumno;
    private String codCurso;
    private int numExamen;
    private String date;
    private int notaExamen;
    
    /**
     * Constructor por cinco parametros
     * @param codAlumno
     * @param codCurso
     * @param numExamen
     * @param date
     * @param notaExamen 
     */
    public Examen(String codAlumno, String codCurso, int numExamen, String date, int notaExamen) {
        this.codAlumno = codAlumno;
        this.codCurso = codCurso;
        this.numExamen = numExamen;
        this.date = date;
        this.notaExamen = notaExamen;
    }

    public String getCodAlumno() {
        return codAlumno;
    }

    public String getCodCurso() {
        return codCurso;
    }

    public int getNumExamen() {
        return numExamen;
    }

    public String getDate() {
        return date;
    }

    public int getNotaExamen() {
        return notaExamen;
    }

    public void setCodAlumno(String codAlumno) {
        this.codAlumno = codAlumno;
    }

    public void setCodCurso(String codCurso) {
        this.codCurso = codCurso;
    }

    public void setNumExamen(int numExamen) {
        this.numExamen = numExamen;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setNotaExamen(int notaExamen) {
        this.notaExamen = notaExamen;
    }

    @Override
    public String toString() {
        return codAlumno + "-" + codCurso + "-" + numExamen + "-" + date + "-" + notaExamen;
    }
    
    
}

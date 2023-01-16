package model;

/**
 * Clase Matricula correspondiente a la tabla Matriculas de la BDD
 * @author migup
 */
public class Matricula {
    //Atributos de la instancia
    private String codAlumno;
    private String codCurso;
    private int notaMedia;
    
    /**
     * Constructor por 3 parametros
     * @param codAlumno
     * @param codCurso
     * @param notaMedia 
     */
    public Matricula(String codAlumno, String codCurso, int notaMedia) {
        this.codAlumno = codAlumno;
        this.codCurso = codCurso;
        this.notaMedia = notaMedia;
    }

    //getter
    public String getCodAlumno() {
        return codAlumno;
    }

    public String getCodCurso() {
        return codCurso;
    }

    public int getNotaMedia() {
        return notaMedia;
    }

    //setter
    public void setCodAlumno(String codAlumno) {
        this.codAlumno = codAlumno;
    }

    public void setCodCurso(String codCurso) {
        this.codCurso = codCurso;
    }

    public void setNotaMedia(int notaMedia) {
        this.notaMedia = notaMedia;
    }

    @Override
    public String toString() {
        return codAlumno + "-" + codCurso + "-" + notaMedia;
    }

    
}


package view;

import controller.Ctrl_AccesoBDD;
import controller.Ctrl_Alumno;
import controller.Ctrl_Curso;
import controller.Ctrl_Examen;
import controller.Ctrl_Matricula;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import model.Alumno;
import model.Curso;
import model.Examen;
import model.Matricula;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author migup
 */
public class Principal extends javax.swing.JFrame {
    // Declaracion de variables globales para todo el proyecto
    public static Connection connection;
    // Declaracion de variables
    // Variables de los controladores
    public Ctrl_AccesoBDD controlBDD = new Ctrl_AccesoBDD();
    public Ctrl_Alumno controlAlumno = new Ctrl_Alumno();
    public Ctrl_Curso controlCurso = new Ctrl_Curso();
    public Ctrl_Examen controlExamen = new Ctrl_Examen();
    public Ctrl_Matricula controlMatricula = new Ctrl_Matricula();
    // Modelos de tablas
    private DefaultTableModel dtmAlumnos = new DefaultTableModel();
    private DefaultTableModel dtmCursos = new DefaultTableModel();
    private DefaultTableModel dtmMatriculas = new DefaultTableModel();
    private DefaultTableModel dtmExamenes = new DefaultTableModel();
    
    private ArrayList<Alumno> lista = new ArrayList<>();
    
    private String codAlumno;
    private String codCurso;
    private String nombreAlumno;
    private String nombreCurso;
    private int numExamen;
    private int numExamenes;
    
    /**
     * Creates new form Principal
     */
    public Principal() {
        // Comprobamos la conexion
        connection = controlBDD.conectarOracle();
        if(connection == null){
            JOptionPane.showMessageDialog(this,"Se ha producido un error al conectar");
            System.exit(0);
        }
        initComponents();
        
        // Inicializamos los modelos de tabla
        dtmAlumnos = (DefaultTableModel) jTableAlumnos.getModel();
        dtmCursos = (DefaultTableModel) jTableCursos.getModel();
        dtmMatriculas = (DefaultTableModel) jTableMatriculas.getModel();
        dtmExamenes = (DefaultTableModel) jTableExamenes.getModel();
        
        // Asignamos los adaptatadores a las tablas
        jTableAlumnos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTableAlumnos.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt){
                int row = jTableAlumnos.rowAtPoint(evt.getPoint());  
                if (row >= 0) {
                    codAlumno = (String) jTableAlumnos.getValueAt(row, 0);                
                    nombreAlumno = (String) jTableAlumnos.getValueAt(row, 1);                      
                    rellenarTablaMatriculas();
                }
            }
        });        
        
        jTableCursos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTableCursos.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt){
                int row = jTableCursos.rowAtPoint(evt.getPoint());  
                if (row >= 0) {
                    codCurso = (String) jTableCursos.getValueAt(row, 0);                
                    nombreCurso = (String) jTableCursos.getValueAt(row, 1);    
                    numExamenes = (int) jTableCursos.getValueAt(row, 2);                    
                }
            }
        });       
        
        jTableMatriculas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTableMatriculas.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt){
                int row = jTableMatriculas.rowAtPoint(evt.getPoint());
                if(row >= 0){
                    codAlumno = (String) jTableMatriculas.getValueAt(row, 0);
                    codCurso = (String) jTableMatriculas.getValueAt(row, 2);
                    rellenarTablaExamenes();
                }
            }
        });
        
        jTableExamenes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTableExamenes.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt){
                int row = jTableExamenes.rowAtPoint(evt.getPoint());
                if(row >= 0){
                    numExamen = (int) jTableExamenes.getValueAt(row,0);
                    txtFechaExamen.setText(""+jTableExamenes.getValueAt(row, 1));
                    txtNota.setText(""+jTableExamenes.getValueAt(row, 2));
                }
            }
        });    
        
        setLocationRelativeTo(null);
        
        // Al iniciar la aplicación ser realizará la carga de datos de los 
        //JTable de Alumnos y Cursos utilizando Statement.
        rellenarTablaAlumnos();
        rellenarTablaCursos();
    }

    /**
     * Metodo que rellena jTableAlumnos con los datos de la tabla Alumnos
     */
   private void rellenarTablaAlumnos(){        
        dtmAlumnos.setRowCount(0);
        for(Alumno a : controlAlumno.consultarAlumno()){            
            dtmAlumnos.addRow(new Object[]{a.getCod(),a.getNombre()});
        }
    }
    
    /**
     * Metodo que rellena jTableCursos con los datos de la tabla Cursos
     */
    private void rellenarTablaCursos() {
        dtmCursos.setRowCount(0);
        for(Curso c : controlCurso.consultarCurso()){            
            dtmCursos.addRow(new Object[]{c.getCod(),c.getNombre(), c.getNumExamenes()});
        }
    }
    
    /**
     * Metodo que rellena jTableMatriculas con los datos de una vista que se 
     * compone de las tablas Alumnos, Cursos y Matriculas
     */
    private void rellenarTablaMatriculas() {
        // Declaracion e inicialiazacion de variables
        String sentenciaSQL;
        PreparedStatement psp;
        ResultSet resultados;
        
        dtmMatriculas.setRowCount(0);
        for(Object[] o : controlMatricula.consultarMatriculas(codAlumno)){
            dtmMatriculas.addRow(o);
        }  
    }
    
    /**
     * Metodo que rellena el jTableExamenes con los datos de la tabla Examenes
     */
    private void rellenarTablaExamenes() {
        dtmExamenes.setRowCount(0);
        for(Examen e : controlExamen.consultarExamenes(codAlumno, codCurso)){
            dtmExamenes.addRow(new Object[]{
                e.getNumExamen(),
                e.getDate(),
                e.getNotaExamen()
            });
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTableAlumnos = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableCursos = new javax.swing.JTable();
        btnMatricula = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTableMatriculas = new javax.swing.JTable();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTableExamenes = new javax.swing.JTable();
        lblFechaExamen = new javax.swing.JLabel();
        lblNota = new javax.swing.JLabel();
        txtFechaExamen = new javax.swing.JTextField();
        txtNota = new javax.swing.JTextField();
        btnActualizar = new javax.swing.JButton();
        btnXML = new javax.swing.JButton();
        btnJSON = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("AD_Practicas_Tema02_Practica01");
        setLocation(new java.awt.Point(0, 0));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jTableAlumnos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Codigo Alumno", "Nombre"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTableAlumnos);

        jTableCursos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Codigo Curso", "Nombre Curso", "Nº Examenes"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(jTableCursos);

        btnMatricula.setText("Matricular Alumno en Curso");
        btnMatricula.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMatriculaActionPerformed(evt);
            }
        });

        jTableMatriculas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Codigo Alumno", "Nombre Alumno", "Codigo Curso", "Nombre Curso", "Nota Media"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(jTableMatriculas);

        jTableExamenes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Numero Examen", "Fecha Examen", "Nota"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane4.setViewportView(jTableExamenes);

        lblFechaExamen.setText("Fecha Examen");

        lblNota.setText("Nota");

        btnActualizar.setText("Actualizar");
        btnActualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActualizarActionPerformed(evt);
            }
        });

        btnXML.setText("Listado Matricula XML");
        btnXML.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnXMLActionPerformed(evt);
            }
        });

        btnJSON.setText("Boletin JSON");
        btnJSON.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnJSONActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(layout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 438, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addGap(347, 347, 347)
                            .addComponent(btnMatricula))
                        .addGroup(layout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(jScrollPane3)))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblFechaExamen)
                                    .addComponent(lblNota))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtFechaExamen)
                                    .addComponent(txtNota)
                                    .addComponent(btnActualizar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addComponent(btnJSON, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnXML, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(139, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(btnMatricula)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblFechaExamen)
                            .addComponent(txtFechaExamen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblNota)
                            .addComponent(txtNota, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(btnActualizar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnJSON)
                        .addGap(18, 18, 18)
                        .addComponent(btnXML))
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(216, 216, 216))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnMatriculaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMatriculaActionPerformed
        // Comprobamos que haya alumno y curso elegido 
        if(codAlumno != null && codCurso != null){
            // Nos creamos una matricula
            Matricula matricula = new Matricula(codAlumno, codCurso, 0);
            // Insertamos el registro en la tabla matriculas
            controlMatricula.altaMatricula(matricula);            
            // En funcion del numero de examenes insertamos registros en la tabla examenes
            for(int i = 1; i <= numExamenes; i++){
                controlExamen.altaExamen(new Examen(codAlumno, codCurso, i, "", 0));
            }
        }else{
            JOptionPane.showMessageDialog(this, "Debe elegir tanto una fila "
                    + "de la tabla Alumnos como de la tabla Cursos");
        }
        // Actualizamos la tabla
        rellenarTablaMatriculas();
    }//GEN-LAST:event_btnMatriculaActionPerformed

    private void btnXMLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXMLActionPerformed
        // Declaracion e inicializacion de variables
        DocumentBuilder documentBuilder = null;
        Transformer xformer = null;
        Source source;
        Result result;
        
        String sentenciaSQL;
        PreparedStatement psp;
        ResultSet resultados;
        
        try {
            // Obtenemos una instancia de un documentbuilder para poder parsear 
            // el documento xml
            documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();                    
        } catch (ParserConfigurationException ex) {
            JOptionPane.showMessageDialog(this,"Error de configuracion de parseo");            
        } 
        // Nos creamos un arbol DOM
        Document doc = documentBuilder.newDocument();
        // Nos creamos el nodo raiz alumnos
        Element alumnos = doc.createElement("alumnos");
        // Anadimos el nodo raiz alumnos a la estructura del DOM
        doc.appendChild(alumnos);
        // Por cada alumno dentro de la lista 
        for(Alumno alumnoLista : controlAlumno.consultarAlumno()){
            // Nos creamos un elemento para cada alumno y lo anadimos a la 
            // estructura del elemento alumnos. Posteriormente le anadimos a 
            // cada alumno sus cursos.             
            Element elementoAlumno = doc.createElement("alumno");
            elementoAlumno.setAttribute("codigo", alumnoLista.getCod());
            elementoAlumno.setAttribute("nombre", alumnoLista.getNombre());
            alumnos.appendChild(elementoAlumno);
            
            Element elementoCursos = doc.createElement("cursos");
            elementoAlumno.appendChild(elementoCursos);
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
                    psp.setString(1, alumnoLista.getCod());
                    // Ejecutamos la consulta
                    resultados = psp.executeQuery(); 
                    // Recorremos el ResultSet
                    while(resultados.next()){ 
                        // Escribimos la informacion de las matriculas en los
                        // atributos y el texto
                        Element elementoCurso = doc.createElement("curso");
                        elementoCurso.setAttribute("codigo", resultados.getString("ccodcurso"));
                        elementoCurso.setAttribute("numero_examenes", resultados.getInt("nnumexa")+"");
                        elementoCurso.setAttribute("nota_media", resultados.getString("nnotamedia"));
                        elementoCurso.setTextContent(resultados.getString("cnomcurso"));
                        elementoCursos.appendChild(elementoCurso);
                    }
                    // Cerramos el ResultSet
                    resultados.close();
                    // Cerramos el preparedStatement
                    psp.close();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                }
            }
        }
        
        try {
            // Obtenemos una instancia de una transformador para poder convertir
            // el documento en un fichero xml
            xformer = TransformerFactory.newInstance().newTransformer();
        } catch (TransformerConfigurationException ex) {
            JOptionPane.showMessageDialog(this,"Error de configuracion de transformacion");            
        }
        // Propiedades del fichero XML de salida
        xformer.setOutputProperty(OutputKeys.METHOD, "xml");
        xformer.setOutputProperty(OutputKeys.INDENT, "yes"); 
        // Definimos la Entrada y Salida de la Transformacion
        source = new DOMSource(doc);
        result = new StreamResult(new File("alumnos.xml"));            
        try {
            // Realizamos la transformacion mediante el metodo transform()
            xformer.transform(source,result);
        } catch (TransformerException ex) {
            JOptionPane.showMessageDialog(this,"Error de transformacion");
        }
    }//GEN-LAST:event_btnXMLActionPerformed

    private void btnActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarActionPerformed
        double auxNota = -1;
        // Comprobamos que la fecha introducida es valida
        try {
            comprobarFecha(txtFechaExamen.getText()) ;
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(null,"Debe introducir una fecha "
                    + "válida con el siguiente formato yyyy-MM-dd");
        }
        // Comprobamos que la nota introducida es valida
        try{
            auxNota = comprobarDouble(txtNota.getText());
            if(auxNota < 0 || auxNota > 10){
                JOptionPane.showMessageDialog(null,"Debe introducir una nota "
                        + "entre 0 y 10");
                return;
            }
        }catch(NumberFormatException | NullPointerException e){
            JOptionPane.showMessageDialog(null,"Debe introducir un nota válida"); 
            return;
        }
        // Modificamos los datos del examen elegido
        controlExamen.modificarExamen(new Examen(codAlumno, codCurso, 
                numExamen, txtFechaExamen.getText(), 
                Integer.parseInt(txtNota.getText())));
        
        rellenarTablaMatriculas();
        rellenarTablaExamenes();
    }//GEN-LAST:event_btnActualizarActionPerformed

    private void btnJSONActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnJSONActionPerformed
        // Declaracion e inicialiazacion de variables
        BufferedWriter bw = null;
        File ficheroJSON = new File(nombreAlumno+".txt");
        
        // Obtenemos la lista de los examenes
        ArrayList<Examen> examenes = controlExamen.consultarExamenes(codAlumno, codCurso);
                
        try {
            // Instanciamos el escritor
            bw = new BufferedWriter(new FileWriter(ficheroJSON));
            // Escribimos toda la informacion
            bw.write("{\n\"examenes\":[\n\t{\n");
            for(int i = 0; i < examenes.size(); i++){
                bw.write("\t\"ccodalu\":" + "\"" + examenes.get(i).getCodAlumno() + "\",\n");
                bw.write("\t\"ccodcurso\":" + "\"" + examenes.get(i).getCodCurso()+ "\",\n");
                bw.write("\t\"nnumexam\":" + examenes.get(i).getNumExamen() + ",\n");
                bw.write("\t\"defecexam\":" + "\"" + examenes.get(i).getDate()+ "\",\n");
                bw.write("\t\"nnotaexam\":" + examenes.get(i).getNotaExamen()+ "\n");
                if(i + 1 == examenes.size()){
                    bw.write("\t}");
                }else{
                    bw.write("\t},\n");
                }
            }
            bw.write("\n]\n}");            
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null,"Error de entrada/salida");
        }finally{
            try {
                bw.close();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null,"Error de entrada/salida");
            }
        }
    }//GEN-LAST:event_btnJSONActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // Cerramos la conexion al cerrar la aplicacion
        try {
            connection.close();
        } catch (SQLException ex) {}
    }//GEN-LAST:event_formWindowClosing

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Principal().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnActualizar;
    private javax.swing.JButton btnJSON;
    private javax.swing.JButton btnMatricula;
    private javax.swing.JButton btnXML;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTable jTableAlumnos;
    private javax.swing.JTable jTableCursos;
    private javax.swing.JTable jTableExamenes;
    private javax.swing.JTable jTableMatriculas;
    private javax.swing.JLabel lblFechaExamen;
    private javax.swing.JLabel lblNota;
    private javax.swing.JTextField txtFechaExamen;
    private javax.swing.JTextField txtNota;
    // End of variables declaration//GEN-END:variables

    /* Metodos auxiliares
    Metodos de clase necesarios en diferentes vistas para comprobar la validez 
    de los datos introducidos
    */
    /**
     * Metodo que comprueba que se trata de un numero entero
     * @param campoTexto String que se comprueba
     * @return Devuelve un numero entero, -1 por defecto
     * @throws NullPointerException Se lanza si no se introducen datos
     * @throws NumberFormatException Se lanza si el formato es incorrecto
     */
    public double comprobarDouble(String campoTexto) throws 
            NullPointerException,NumberFormatException{
        double doble = -1;        
        doble = Double.parseDouble(campoTexto);        
        return doble;
    }       
    
    /**
     * Metodo que comprueba que se trata de una fecha con el formato dd/MM/yyyy
     * @param stringFecha String que se comprueba
     * @return Devuelve un String
     * @throws ParseException Se lanza si el formato es incorrecto
     */
    public String comprobarFecha(String stringFecha) throws ParseException{
        Date fecha = null;
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        
        fecha = formato.parse(stringFecha);           
        stringFecha = formato.format(fecha);
        
        return stringFecha;
    }
}

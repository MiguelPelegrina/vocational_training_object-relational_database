CREATE USER AD_TEMA02 IDENTIFIED BY AD_TEMA02;
GRANT DBA TO AD_TEMA02;
--Desde el usuario/esquema AD_TEMA02

CREATE TABLE ALUMNOS(
 cCodAlu VARCHAR2(6) CONSTRAINT PK_ALUMNOS PRIMARY KEY,
 cNomAlu VARCHAR2(100) NOT NULL
);

CREATE TABLE CURSOS(
 cCodCurso VARCHAR2(6) CONSTRAINT PK_CURSOS PRIMARY KEY,
 cNomCurso VARCHAR2(100) NOT NULL,
 nNumExa NUMBER(3) DEFAULT 1 NOT NULL 
);

CREATE TABLE MATRICULAS(
 cCodAlu VARCHAR2(6) NOT NULL,
 cCodCurso VARCHAR2(6) NOT NULL,
 nNotaMedia NUMBER(3) DEFAULT 0 NOT NULL,
 CONSTRAINT PK_MATRICULAS PRIMARY KEY (cCodAlu,cCodCurso)
);
ALTER TABLE MATRICULAS ADD CONSTRAINT FK_MATRICULAS_ALUMNO FOREIGN KEY (cCodAlu)
REFERENCES ALUMNOS(cCodAlu);
ALTER TABLE MATRICULAS ADD CONSTRAINT FK_MATRICULAS_CURSOS FOREIGN KEY (cCodCurso)
REFERENCES CURSOS(cCodCurso);

CREATE TABLE EXAMENES(
 cCodAlu VARCHAR2(6) NOT NULL,
 cCodCurso VARCHAR2(6) NOT NULL,
 nNumExam NUMBER(3) DEFAULT 1 NOT NULL,
 dFecExam DATE,
 nNotaExam NUMBER(6,2) DEFAULT 0 NOT NULL,
 CONSTRAINT PK_EXAMENES PRIMARY KEY (cCodAlu,cCodCurso,nNumExam)
);
ALTER TABLE EXAMENES ADD CONSTRAINT FK_EXAMENES_MATR FOREIGN KEY (cCodAlu,cCodCurso)
REFERENCES MATRICULAS(cCodAlu,cCodCurso);

INSERT INTO ALUMNOS VALUES ('001','Antonio');
INSERT INTO ALUMNOS VALUES ('002','Maria');
INSERT INTO CURSOS VALUES ('I001','Ingles Basico',5);
INSERT INTO CURSOS VALUES ('I002','Ingles Intermedio',8);
INSERT INTO CURSOS VALUES ('I003','Ingles Avanzado',10);
INSERT INTO CURSOS VALUES ('F001','Frances Basico',3);
INSERT INTO CURSOS VALUES ('C002','Chino Intermedio',9);
COMMIT;

--CAMBIOS REALIZADOS
--Nos creamos una vista para poder acceder a todos los datos necesarioas para 
--poder rellenar posteriormente las jTables Matriculas y Examenes
CREATE OR REPLACE VIEW vistaTablas AS 
SELECT al.ccodalu, al.cnomalu, cu.ccodcurso, cu.cnomcurso, ma.nnotamedia, cu.nnumexa
FROM alumnos al, cursos cu, matriculas ma
WHERE al.ccodalu = ma.ccodalu AND cu.ccodcurso = ma.ccodcurso; 

--Nos creamos un procedimiento para dar de alta una matricula
CREATE OR REPLACE PROCEDURE sp_AltaMatricula (xcCodAlu VARCHAR2, xcCodCurso 
VARCHAR2, xError OUT NUMBER) AS
  xNR NUMBER;
BEGIN
  INSERT INTO matriculas VALUES(xcCodAlu,xcCodCurso,0);
EXCEPTION
 WHEN OTHERS THEN xError := -1;
END;

--Nos creamos un procedimiento para modificar la fecha del examen y actualizar
--la nota media del curso matriculado cada vez que se pulse el boton de 
--actualizar de la aplicacion
CREATE OR REPLACE PROCEDURE sp_ModificarExamen (
  xcCodAlu VARCHAR2, xcCodCurso VARCHAR2, xDFecExam VARCHAR2, xnNotaExam NUMBER, 
  xNumExamen NUMBER, xError OUT NUMBER) 
AS
  xNR NUMBER;  
BEGIN
  --actualizacion de la tabla examenes
  UPDATE examenes SET dfecexam = TO_DATE(xDFecExam,'YYYY-MM-DD'), nNotaExam = xnNotaExam 
  WHERE cCodAlu = xcCodAlu AND cCodCurso = xcCodCurso AND nNumExam = xNumExamen;   
  --actualizacion de la tabla matriculas
  UPDATE matriculas SET nnotamedia = (
    --subconsulta para obtener la nota media
    SELECT AVG(nNotaExam) 
    FROM Examenes 
    WHERE cCodAlu = xcCodAlu AND cCodCurso = xcCodCurso AND nNotaExam <> 0) 
  WHERE cCodAlu = xcCodAlu AND cCodCurso = xcCodCurso;
EXCEPTION
 WHEN OTHERS THEN xError := -1;
END;
package com.micro.cursos.models.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.micro.cursos.models.entity.Curso;

//public interface CursoRepository extends CrudRepository<Curso,Long>{
public interface CursoRepository extends PagingAndSortingRepository<Curso,Long>{
	//fetch -> obtiene la lista de alumnos con la carga perezosa
	@Query("select c from Curso c join fetch c.cursoAlumnos a where a.alumnoId=?1")
	public Curso findCursoByAlumnoId(Long id);
	
	@Modifying
	@Query("delete from CursoAlumno ca where ca.alumnoId=?1")
	public void eliminarCursoAlumnoPorId(Long id);
}

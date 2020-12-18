package com.micro.cursos.services;

import com.micro.common.alumnos.models.entity.Alumno;
import com.micro.commons.services.CommonService;
import com.micro.cursos.models.entity.Curso;

public interface CursosService extends CommonService<Curso> {

	public Curso findCursoByAlumnoId(Long id);
	
	public Iterable<Long> obtenerExamenesIdsConRespuestasAlumno(Long alumnoId);
	
	public Iterable<Alumno> obtenerAlumnosPorCurso(Iterable<Long> ids);
	
	public void eliminarCursoAlumnoPorId(Long id);
}

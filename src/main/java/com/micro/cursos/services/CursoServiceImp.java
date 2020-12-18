package com.micro.cursos.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.micro.common.alumnos.models.entity.Alumno;
import com.micro.commons.services.CommonServiceImp;
import com.micro.cursos.clients.AlumnoFeingClient;
import com.micro.cursos.clients.RespuestaFeingClient;
import com.micro.cursos.models.entity.Curso;
import com.micro.cursos.models.repository.CursoRepository;

@Service
public class CursoServiceImp extends CommonServiceImp<Curso, CursoRepository> implements CursosService {

	@Autowired
	private AlumnoFeingClient clientAlumno;
	
	@Autowired
	private RespuestaFeingClient client;
	
	@Override
	@Transactional(readOnly = true)
	public Curso findCursoByAlumnoId(Long id) {		
		return repo.findCursoByAlumnoId(id);
	}

	@Override
	public Iterable<Long> obtenerExamenesIdsConRespuestasAlumno(Long alumnoId) {		
		return client.obtenerExamenesIdsConRespuestasAlumno(alumnoId) ;
	}

	@Override
	public Iterable<Alumno> obtenerAlumnosPorCurso(Iterable<Long> ids) {
		return clientAlumno.obtenerAlumnosPorCurso(ids);
	}

	@Override
	@Transactional
	public void eliminarCursoAlumnoPorId(Long id) {		
		repo.eliminarCursoAlumnoPorId(id);
	}

}

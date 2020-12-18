package com.micro.cursos.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.micro.common.alumnos.models.entity.Alumno;

@FeignClient(name ="microservicio-usuarios")
public interface AlumnoFeingClient {
	
	@GetMapping("/alumnos-por-curso")
	public Iterable<Alumno> obtenerAlumnosPorCurso(@RequestParam Iterable<Long> ids);

}

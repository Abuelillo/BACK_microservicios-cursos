package com.micro.cursos.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.commons.examenes.models.entity.Examen;
import com.micro.common.alumnos.models.entity.Alumno;
import com.micro.commons.controllers.CommonController;
import com.micro.cursos.models.entity.Curso;
import com.micro.cursos.models.entity.CursoAlumno;
import com.micro.cursos.services.CursosService;

@RestController
public class CursoController extends CommonController<Curso, CursosService> {
	
	@Value("${config.balanceador.test}")
	private String balanceadorTest;	
	
	@DeleteMapping("/eliminar-alumno/{id}")
	public ResponseEntity<?> eliminarCursoAlumnoPorId(@PathVariable Long id){
		service.eliminarCursoAlumnoPorId(id);
		return ResponseEntity.noContent().build();
	}
	
	@Override
	@GetMapping
	public ResponseEntity<?> listar(){
		List<Curso> curso = ((List<Curso>) service.findAll()).
				stream().
				map(c -> {
					c.getCursoAlumnos().forEach(ca ->{
						Alumno alumno = new Alumno();
						alumno.setId(ca.getAlumnoId());
						c.addAlumnos(alumno);
					});
					return c;
				}).collect(Collectors.toList());
		
		return ResponseEntity.ok().body(curso);
	}
	
	@Override
	@GetMapping("/pagina")
	public ResponseEntity<?> listar(Pageable pageable){
		Page<Curso>cursos = service.findAll(pageable).
							map(curso -> {
								curso.getCursoAlumnos().forEach(ca ->{
									Alumno alumno = new Alumno();
									alumno.setId(ca.getAlumnoId());
									curso.addAlumnos(alumno);
								});
								return curso;
							});
		
		
		return ResponseEntity.ok().body(cursos);
	}
	
	@Override
	@GetMapping("/{id}")
	public ResponseEntity<?> ver(@PathVariable Long id) {
		
		Optional<Curso> c = service.findById(id);
		if (c.isEmpty()) {
			return ResponseEntity.notFound().build();
		}		
		
		Curso curso = c.get();
		
		if (curso.getCursoAlumnos().isEmpty() == false) {
			List<Long> ids = curso.getCursoAlumnos().
									stream().
									map(ca -> ca.getAlumnoId()).
									collect(Collectors.toList());
			List<Alumno> alumno = (List<Alumno>) service.obtenerAlumnosPorCurso(ids);
			
			curso.setAlumnos(alumno);
		}
		
		return ResponseEntity.ok(curso);

	}
	
	@GetMapping("/balanceador-test")
	public ResponseEntity<?> balanceadorTest() {	
		Map<String, Object> response = new HashMap<String,Object>();
		response.put("balanceador", balanceadorTest);
		response.put("curso", service.findAll());
		return ResponseEntity.ok(response);
	}

	@PutMapping("/{id}")
	private ResponseEntity<?> editar(@Valid @RequestBody Curso curso, BindingResult result ,@PathVariable Long id){
		
		if (result.hasErrors()) {
			return this.validar(result);
		}
		
		Optional<Curso> o = this.service.findById(id);
		
		if (!o.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		
		Curso cursoDB = o.get();
		cursoDB.setNombre(curso.getNombre());
		
		return ResponseEntity.status(HttpStatus.CREATED).body(this.service.save(cursoDB));
	}
	
	@PutMapping("/{id}/asignar-alumnos")
	public ResponseEntity<?> asignarAlumnos(@RequestBody List<Alumno> alumnos, @PathVariable Long id){
		Optional<Curso> o = this.service.findById(id);
		if (!o.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		Curso cursoDB = o.get();
		
		alumnos.forEach(a -> {
			CursoAlumno cursoAlumno = new CursoAlumno();
			cursoAlumno.setAlumnoId(a.getId());
			cursoAlumno.setCurso(cursoDB);
			cursoDB.addCursoAlumnos(cursoAlumno);
			//cursoDB.addAlumnos(a);
		});
		
		return ResponseEntity.status(HttpStatus.CREATED).body(this.service.save(cursoDB));
	}
	
	@PutMapping("/{id}/eliminar-alumnos")
	public ResponseEntity<?> eliminarAlumnos(@RequestBody Alumno alumno, @PathVariable Long id){
		Optional<Curso> o = this.service.findById(id);
		if (!o.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		Curso cursoDB = o.get();
		
		CursoAlumno cursoAlumno = new CursoAlumno();
		cursoAlumno.setAlumnoId(alumno.getId());
		cursoDB.removeCursoAlumnos(cursoAlumno);
		
		//cursoDB.removeAlumnos(alumno);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(this.service.save(cursoDB));
	}
	
	@GetMapping("/alumno/{id}")
	public ResponseEntity<?> buscarPorAlumnoId(@PathVariable Long id){
		Curso curso = service.findCursoByAlumnoId(id);
		
		if (curso != null) {
			List<Long> examenesIds = (List<Long>) service.obtenerExamenesIdsConRespuestasAlumno(id);
			List<Examen> examenes = curso.getExamenes().stream().map(examen -> {
				if (examenesIds.contains(examen.getId())) {
					examen.setRespondido(true);
				}
				return examen;
			}).collect(Collectors.toList());
			
			curso.setExamenes(examenes);
		}
		
		return ResponseEntity.ok(curso);
	}
	
	@PutMapping("/{id}/asignar-examenes")
	public ResponseEntity<?> asignarExamen(@RequestBody List<Examen> examenes, @PathVariable Long id){
		Optional<Curso> o = this.service.findById(id);
		if (!o.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		Curso cursoDB = o.get();
		
		examenes.forEach(e -> {
			cursoDB.addExamen(e);
		});
		
		return ResponseEntity.status(HttpStatus.CREATED).body(this.service.save(cursoDB));
	}
	
	@PutMapping("/{id}/eliminar-examen")
	public ResponseEntity<?> eliminarExamen(@RequestBody Examen examen, @PathVariable Long id){
		Optional<Curso> o = this.service.findById(id);
		if (!o.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		Curso cursoDB = o.get();
		
		cursoDB.removeExamen(examen);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(this.service.save(cursoDB));
	}

}

package br.edu.ibmec.resource;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import br.edu.ibmec.service.CursoService;
import br.edu.ibmec.dto.CursoDTO;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/cursos")
public class CursoResource {

	private final CursoService cursoService;

    public CursoResource(CursoService cursoService) {
        this.cursoService = cursoService;
    }

    @GetMapping(path = "/{codigo}", produces = "application/json")
    public ResponseEntity<CursoDTO> buscarCursoPeloCodigo(@PathVariable int codigo) {
        CursoDTO cursoDTO = cursoService.buscarCurso(codigo);
        return ResponseEntity.ok(cursoDTO);
    }

    @PostMapping
    public ResponseEntity<CursoDTO> cadastrarCurso(@RequestBody CursoDTO cursoDTO) {
        CursoDTO cursoSalvo = cursoService.cadastrarCurso(cursoDTO);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{codigo}")
                .buildAndExpand(cursoSalvo.getCodigo())
                .toUri();

        return ResponseEntity.created(location).body(cursoSalvo);
    }

    @PutMapping("/{codigo}")
    public ResponseEntity<CursoDTO> alterarCurso(@PathVariable int codigo, @RequestBody CursoDTO cursoDTO) {
        CursoDTO cursoAtualizado = cursoService.alterarCurso(codigo, cursoDTO);
        return ResponseEntity.ok(cursoAtualizado);
    }

    @DeleteMapping("/{codigo}")
    public ResponseEntity<Void> removerCurso(@PathVariable int codigo) {
        cursoService.removerCurso(codigo);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(produces = "application/json")
    public ResponseEntity<List<CursoDTO>> listarCursos() {
        List<CursoDTO> listaCursos = cursoService.listarCursos();
        return ResponseEntity.ok(listaCursos);
    }
}
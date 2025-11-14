package br.edu.ibmec.resource;

import br.edu.ibmec.dto.ProfessorRequestDTO;
import br.edu.ibmec.dto.ProfessorResponseDTO;
import br.edu.ibmec.service.ProfessorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/professores")
public class ProfessorResource {

    private final ProfessorService professorService;

    public ProfessorResource(ProfessorService professorService) {
        this.professorService = professorService;
    }

    @GetMapping(path = "/{matricula}", produces = "application/json")
    public ResponseEntity<ProfessorResponseDTO> buscarProfessorPorMatricula(@PathVariable String matricula) {
        ProfessorResponseDTO professorDTO = professorService.buscarProfessor(matricula);
        return ResponseEntity.ok(professorDTO);
    }

    @GetMapping
    public ResponseEntity<List<ProfessorResponseDTO>> listarProfessores() {
        List<ProfessorResponseDTO> listaProfessores = professorService.listarProfessores();
        return ResponseEntity.ok(listaProfessores);
    }

    @PostMapping
    public ResponseEntity<ProfessorResponseDTO> cadastrarProfessor(@RequestBody ProfessorRequestDTO professorDTO) {
        ProfessorResponseDTO professorSalvo = professorService.cadastrarProfessor(professorDTO);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{matricula}")
                .buildAndExpand(professorSalvo.getMatricula())
                .toUri();

        return ResponseEntity.created(location).body(professorSalvo);
    }

    @PutMapping("/{matricula}")
    public ResponseEntity<ProfessorResponseDTO> alterarProfessor(@PathVariable String matricula, @RequestBody ProfessorRequestDTO professorDTO) {
        ProfessorResponseDTO professorAtualizado = professorService.alterarProfessor(matricula, professorDTO);
        return ResponseEntity.ok(professorAtualizado);
    }

    @DeleteMapping("/{matricula}")
    public ResponseEntity<Void> removerProfessor(@PathVariable String matricula) {
        professorService.removerProfessor(matricula);
        return ResponseEntity.noContent().build();
    }
}
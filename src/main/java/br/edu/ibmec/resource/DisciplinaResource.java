package br.edu.ibmec.resource;

import br.edu.ibmec.dto.DisciplinaDTO;
import br.edu.ibmec.service.DisciplinaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/disciplinas")
public class DisciplinaResource {
    private final DisciplinaService disciplinaService;

    public DisciplinaResource(DisciplinaService disciplinaService) {
        this.disciplinaService = disciplinaService;
    }

    @GetMapping(path = "/{codigo}")
    public ResponseEntity<DisciplinaDTO> buscarDisciplinaPorCodigo(@PathVariable Integer codigo) {
        DisciplinaDTO disciplinaDTO = disciplinaService.buscarDisciplina(codigo);
        return ResponseEntity.ok(disciplinaDTO);
    }

    @GetMapping
    public ResponseEntity<List<DisciplinaDTO>> listarDisciplinas() {
        List<DisciplinaDTO> listaDisciplinas = disciplinaService.listarDisciplinas();
        return ResponseEntity.ok(listaDisciplinas);
    }

    @PostMapping
    public ResponseEntity<DisciplinaDTO> cadastrarDisciplina(@RequestBody DisciplinaDTO disciplinaDTO) {
        DisciplinaDTO disciplinaSalva = disciplinaService.cadastrarDisciplina(disciplinaDTO);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{codigo}")
                .buildAndExpand(disciplinaSalva.getCodigo())
                .toUri();

        return ResponseEntity.created(location).body(disciplinaSalva);
    }

    @PutMapping("/{codigo}")
    public ResponseEntity<DisciplinaDTO> alterarDisciplina(@PathVariable Integer codigo, @RequestBody DisciplinaDTO disciplinaDTO) {
        DisciplinaDTO disciplinaAtualizada = disciplinaService.alterarDisciplina(codigo, disciplinaDTO);
        return ResponseEntity.ok(disciplinaAtualizada);
    }

    @DeleteMapping("/{codigo}")
    public ResponseEntity<Void> removerDisciplina(@PathVariable Integer codigo) {
        disciplinaService.removerDisciplina(codigo);
        return ResponseEntity.noContent().build();
    }
}
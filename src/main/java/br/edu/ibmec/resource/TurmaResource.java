package br.edu.ibmec.resource;

import br.edu.ibmec.dto.TurmaDTO;
import br.edu.ibmec.service.TurmaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/turmas")
public class TurmaResource {

    private final TurmaService turmaService;

    public TurmaResource(TurmaService turmaService) {
        this.turmaService = turmaService;
    }

    @GetMapping(path = "/{codigo}")
    public ResponseEntity<TurmaDTO> buscarTurmaPorCodigo(@PathVariable String codigo) {
        TurmaDTO turmaDTO = turmaService.buscarTurma(codigo);
        return ResponseEntity.ok(turmaDTO);
    }

    @GetMapping
    public ResponseEntity<List<TurmaDTO>> listarTurmas() {
        List<TurmaDTO> listaTurmas = turmaService.listarTurmas();
        return ResponseEntity.ok(listaTurmas);
    }

    @PostMapping
    public ResponseEntity<TurmaDTO> cadastrarTurma(@RequestBody TurmaDTO turmaDTO) {
        TurmaDTO turmaSalva = turmaService.cadastrarTurma(turmaDTO);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{codigo}")
                .buildAndExpand(turmaSalva.getCodigo())
                .toUri();

        return ResponseEntity.created(location).body(turmaSalva);
    }

    @PutMapping("/{codigo}")
    public ResponseEntity<TurmaDTO> alterarTurma(@PathVariable String codigo, @RequestBody TurmaDTO turmaDTO) {
        TurmaDTO turmaAtualizada = turmaService.alterarTurma(codigo, turmaDTO);
        return ResponseEntity.ok(turmaAtualizada);
    }

    @DeleteMapping("/{codigo}")
    public ResponseEntity<Void> removerTurma(@PathVariable String codigo) {
        turmaService.removerTurma(codigo);
        return ResponseEntity.noContent().build();
    }
}
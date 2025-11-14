package br.edu.ibmec.resource;

import java.net.URI;
import java.util.List;

import br.edu.ibmec.dto.AlunoResponseDTO;
import org.springframework.http.ResponseEntity;

import br.edu.ibmec.service.AlunoService;
import br.edu.ibmec.dto.AlunoRequestDTO;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


@RestController
@RequestMapping("/alunos")
public class AlunoResource {

    private final AlunoService alunoService;

    public AlunoResource(AlunoService alunoService) {
        this.alunoService = alunoService;
    }

    @GetMapping(path = "/{matricula}", produces = "application/json")
    public ResponseEntity<AlunoResponseDTO> buscarAlunoPorMatricula(@PathVariable String matricula) {
        AlunoResponseDTO alunoResponseDTO = alunoService.buscarAluno(matricula);
        return ResponseEntity.ok(alunoResponseDTO);
    }

    @GetMapping(path = "/{matricula}/mensalidade")
    public ResponseEntity<Float> calcularMensalidade(@PathVariable String matricula) {
        Float mensalidade = alunoService.calcularMensalidade(matricula);
        return ResponseEntity.ok(mensalidade);
    }

    @PostMapping()
    public ResponseEntity<AlunoResponseDTO> cadastrarAluno(@RequestBody AlunoRequestDTO alunoRequestDTO) {
        AlunoResponseDTO alunoSalvo = alunoService.cadastrarAluno(alunoRequestDTO);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{matricula}")
                .buildAndExpand(alunoSalvo.getMatricula())
                .toUri();

        return ResponseEntity.created(location).body(alunoSalvo);
    }

    @PutMapping("/{matricula}")
    public ResponseEntity<AlunoResponseDTO> alterarAluno(@PathVariable String matricula, @RequestBody AlunoRequestDTO alunoRequestDTO) {
        AlunoResponseDTO alunoAtualizado = alunoService.alterarAluno(matricula, alunoRequestDTO);
        return ResponseEntity.ok(alunoAtualizado);
    }

    @DeleteMapping("/{matricula}")
    public ResponseEntity<Void> removerAluno(@PathVariable String matricula) {
        alunoService.removerAluno(matricula);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<AlunoResponseDTO>> listarAlunos() {
        List<AlunoResponseDTO> listaAlunos = alunoService.listarAlunos();
        return ResponseEntity.ok(listaAlunos);
    }
}


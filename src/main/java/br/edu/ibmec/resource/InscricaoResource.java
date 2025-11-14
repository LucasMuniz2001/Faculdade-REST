package br.edu.ibmec.resource;

import br.edu.ibmec.dto.InscricaoRequestDTO;
import br.edu.ibmec.dto.InscricaoResponseDTO;
import br.edu.ibmec.dto.InscricaoUpdateDTO;
import br.edu.ibmec.service.InscricaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/inscricoes")
public class InscricaoResource {

    private final InscricaoService inscricaoService;

    public InscricaoResource(InscricaoService inscricaoService) {
        this.inscricaoService = inscricaoService;
    }

    // GET: Busca a inscrição pela chave composta
    @GetMapping("/aluno/{alunoMatricula}/turma/{turmaCodigo}")
    public ResponseEntity<InscricaoResponseDTO> buscarInscricao(@PathVariable String alunoMatricula, @PathVariable String turmaCodigo) {
        InscricaoResponseDTO inscricao = inscricaoService.buscarInscricao(alunoMatricula, turmaCodigo);
        return ResponseEntity.ok(inscricao);
    }

    // POST: Realiza uma nova inscrição
    @PostMapping
    public ResponseEntity<InscricaoResponseDTO> inscreverAluno(@RequestBody InscricaoRequestDTO requestDTO) {
        InscricaoResponseDTO inscricaoSalva = inscricaoService.realizarInscricao(requestDTO);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/aluno/{alunoMatricula}/turma/{turmaCodigo}")
                .buildAndExpand(inscricaoSalva.getAlunoMatricula(), inscricaoSalva.getTurmaCodigo())
                .toUri();

        return ResponseEntity.created(location).body(inscricaoSalva);
    }

    // PUT: Atualiza notas e situação final
    @PutMapping("/aluno/{alunoMatricula}/turma/{turmaCodigo}")
    public ResponseEntity<InscricaoResponseDTO> atualizarNotas(@PathVariable String alunoMatricula, @PathVariable String turmaCodigo, @RequestBody InscricaoUpdateDTO updateDTO) {
        InscricaoResponseDTO inscricaoAtualizada = inscricaoService.atualizarNotasESituacao(alunoMatricula, turmaCodigo, updateDTO);
        return ResponseEntity.ok(inscricaoAtualizada);
    }

    // DELETE: Remove a inscrição
    @DeleteMapping("/aluno/{alunoMatricula}/turma/{turmaCodigo}")
    public ResponseEntity<Void> cancelarInscricao(@PathVariable String alunoMatricula, @PathVariable String turmaCodigo) {
        inscricaoService.cancelarInscricao(alunoMatricula, turmaCodigo);
        return ResponseEntity.noContent().build();
    }
}
package br.edu.ibmec.service;

import br.edu.ibmec.dto.DisciplinaDTO;
import br.edu.ibmec.entity.Curso;
import br.edu.ibmec.entity.Disciplina;
import br.edu.ibmec.exception.EntidadeNaoEncontradaException;
import br.edu.ibmec.exception.RegraDeNegocioException;
import br.edu.ibmec.repository.CursoRepository;
import br.edu.ibmec.repository.DisciplinaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DisciplinaService {
    private final DisciplinaRepository disciplinaRepository;
    private final CursoRepository cursoRepository;

    public DisciplinaService(DisciplinaRepository disciplinaRepository, CursoRepository cursoRepository) {
        this.disciplinaRepository = disciplinaRepository;
        this.cursoRepository = cursoRepository;
    }

    public DisciplinaDTO buscarDisciplina(Integer codigo) {
        return disciplinaRepository.findById(codigo)
                .map(DisciplinaDTO::fromEntity)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Disciplina com código " + codigo + " não encontrada."));
    }

    public List<DisciplinaDTO> listarDisciplinas() {
        return disciplinaRepository.findAll().stream()
                .map(DisciplinaDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public DisciplinaDTO cadastrarDisciplina(DisciplinaDTO disciplinaDTO) {
        validarDadosDaDisciplina(disciplinaDTO);

        if (disciplinaRepository.findById(disciplinaDTO.getCodigo()).isPresent()) {
            throw new RegraDeNegocioException("Disciplina com código " + disciplinaDTO.getCodigo() + " já existe.");
        }

        Disciplina novaDisciplina = new Disciplina();
        novaDisciplina.setCodigo(disciplinaDTO.getCodigo());

        atualizarEntidadeComDTO(novaDisciplina, disciplinaDTO);

        disciplinaRepository.save(novaDisciplina);

        return DisciplinaDTO.fromEntity(novaDisciplina);
    }

    public DisciplinaDTO alterarDisciplina(Integer codigo, DisciplinaDTO disciplinaDTO) {
        Disciplina disciplinaExistente = disciplinaRepository.findById(codigo)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Disciplina com código " + codigo + " não encontrada para alteração."));

        validarDadosDaDisciplina(disciplinaDTO);

        if (!codigo.equals(disciplinaDTO.getCodigo())) {
            throw new RegraDeNegocioException("O código da Disciplina (chave primária) não pode ser alterado.");
        }

        atualizarEntidadeComDTO(disciplinaExistente, disciplinaDTO);

        disciplinaRepository.save(disciplinaExistente);

        return DisciplinaDTO.fromEntity(disciplinaExistente);
    }

    public void removerDisciplina(Integer codigo) {
        Disciplina disciplina = disciplinaRepository.findById(codigo)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Disciplina com código " + codigo + " não encontrada para remoção."));

        disciplinaRepository.delete(disciplina);
    }

    private void validarDadosDaDisciplina(DisciplinaDTO dto) {
        if (dto.getCodigo() == null || dto.getCodigo() <= 0) {
            throw new RegraDeNegocioException("O código da disciplina é obrigatório e deve ser positivo.");
        }
        if (dto.getNome() == null || dto.getNome().trim().isEmpty()) {
            throw new RegraDeNegocioException("O nome da disciplina é obrigatório.");
        }
        if (dto.getCodigoCurso() == null || dto.getCodigoCurso() <= 0) {
            throw new RegraDeNegocioException("O código do curso é inválido.");
        }
    }

    private void atualizarEntidadeComDTO(Disciplina disciplina, DisciplinaDTO disciplinaDTO) {
        disciplina.setNome(disciplinaDTO.getNome());


        if (disciplina.getCurso() == null || !disciplina.getCurso().getCodigo().equals(disciplinaDTO.getCodigoCurso())) {
            Curso curso = cursoRepository.findById(disciplinaDTO.getCodigoCurso())
                    .orElseThrow(() -> new RegraDeNegocioException("Curso com código " + disciplinaDTO.getCodigoCurso() + " não encontrado."));
            disciplina.setCurso(curso);
        }
    }
}
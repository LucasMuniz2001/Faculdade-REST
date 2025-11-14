package br.edu.ibmec.service;

import java.util.List;
import java.util.stream.Collectors;

import br.edu.ibmec.dto.AlunoRequestDTO;
import br.edu.ibmec.dto.CursoDTO;
import br.edu.ibmec.entity.Aluno;
import br.edu.ibmec.entity.Curso;
import br.edu.ibmec.entity.EstadoCivil;
import br.edu.ibmec.exception.RegraDeNegocioException;
import br.edu.ibmec.exception.EntidadeNaoEncontradaException;
import br.edu.ibmec.repository.CursoRepository;
import org.springframework.stereotype.Service;

@Service
public class CursoService {
	private CursoRepository cursoRepository;

	public CursoService(CursoRepository cursoRepository) {
        this.cursoRepository = cursoRepository;
	}

    public CursoDTO buscarCurso(int codigo) {
        return cursoRepository.findById(codigo)
                .map(CursoDTO::fromEntity)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Curso com código " + codigo + " não encontrado."));
    }

    public List<CursoDTO> listarCursos() {
        return cursoRepository.findAll().stream()
                .map(CursoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public CursoDTO cadastrarCurso(CursoDTO cursoDTO) {
        validarCurso(cursoDTO);

        if (cursoRepository.findById(cursoDTO.getCodigo()).isPresent()) {
            throw new RegraDeNegocioException("Já existe um curso com o código " + cursoDTO.getCodigo());
        }

        Curso novoCurso = new Curso();

        novoCurso.setCodigo(cursoDTO.getCodigo());

        atualizarEntidadeComDTO(novoCurso, cursoDTO);

        cursoRepository.save(novoCurso);

        return cursoDTO.fromEntity(novoCurso);
    }

    public CursoDTO alterarCurso(int codigo, CursoDTO cursoDTO) {
        Curso cursoExistente = cursoRepository.findById(codigo)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Curso com código " + codigo + " não encontrado para alteração."));

        validarCurso(cursoDTO);

        cursoExistente.setNome(cursoDTO.getNome());

        cursoRepository.save(cursoExistente);

        return cursoDTO.fromEntity(cursoExistente);
    }

    public void removerCurso(int codigo) {
        if (cursoRepository.findById(codigo).isEmpty()) {
            throw new EntidadeNaoEncontradaException("Curso com código " + codigo + " não encontrado para remoção.");
        }

        cursoRepository.deleteById(codigo);
    }

    private void validarCurso(CursoDTO cursoDTO) {
        if (cursoDTO.getCodigo()  < 1 || cursoDTO.getCodigo() > 9999) {
            throw new RegraDeNegocioException("O código do curso é inválido.");
        }
        if (cursoDTO.getNome() == null || cursoDTO.getNome().trim().isEmpty()) {
            throw new RegraDeNegocioException("O nome do curso não pode ser vazio.");
        }
        if (cursoDTO.getValorBaseDisciplina() == null || cursoDTO.getValorBaseDisciplina() <= 0.0f) {
            throw new RegraDeNegocioException("O valor base da disciplina deve ser informado e ser maior que zero.");
        }
    }

    private void atualizarEntidadeComDTO(Curso curso, CursoDTO cursoDTO) {
        curso.setNome(cursoDTO.getNome());
        curso.setValorBaseDisciplina(cursoDTO.getValorBaseDisciplina());
    }
}

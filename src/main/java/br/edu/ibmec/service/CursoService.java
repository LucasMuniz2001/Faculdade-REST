package br.edu.ibmec.service;

import java.util.List;
import java.util.stream.Collectors;

import br.edu.ibmec.dao.UniversidadeDAO;
import br.edu.ibmec.dto.CursoDTO;
import br.edu.ibmec.entity.Curso;
import br.edu.ibmec.exception.RegraDeNegocioException;
import br.edu.ibmec.exception.EntidadeNaoEncontradaException;
import org.springframework.stereotype.Service;

@Service
public class CursoService {
	private UniversidadeDAO dao;

	public CursoService(UniversidadeDAO universidadeDAO) {
		this.dao = universidadeDAO;
	}

    public CursoDTO buscarCurso(int codigo) {
        return dao.buscarCursoPeloCodigo(codigo)
                .map(this::converterParaDTO)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Curso com código " + codigo + " não encontrado."));
    }

    public List<CursoDTO> listarCursos() {
        return dao.buscarTodosCursos().stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    public CursoDTO cadastrarCurso(CursoDTO cursoDTO) {
        validarCurso(cursoDTO);

        if (dao.buscarCursoPeloCodigo(cursoDTO.getCodigo()).isPresent()) {
            throw new RegraDeNegocioException("Já existe um curso com o código " + cursoDTO.getCodigo());
        }

        Curso novoCurso = converterParaEntidade(cursoDTO);
        dao.salvarCurso(novoCurso);

        return cursoDTO;
    }

    public CursoDTO alterarCurso(int codigo, CursoDTO cursoDTO) {
        Curso cursoExistente = dao.buscarCursoPeloCodigo(codigo)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Curso com código " + codigo + " não encontrado para alteração."));

        validarCurso(cursoDTO);

        cursoExistente.setNome(cursoDTO.getNome());

        dao.atualizarCurso(cursoExistente);

        return converterParaDTO(cursoExistente);
    }

    public void removerCurso(int codigo) {
        if (dao.buscarCursoPeloCodigo(codigo).isEmpty()) {
            throw new EntidadeNaoEncontradaException("Curso com código " + codigo + " não encontrado para remoção.");
        }

        dao.removerCursoPeloCodigo(codigo);
    }

    private void validarCurso(CursoDTO cursoDTO) {
        if (cursoDTO.getCodigo() < 1 || cursoDTO.getCodigo() > 9999) {
            throw new RegraDeNegocioException("O código do curso é inválido.");
        }
        if (cursoDTO.getNome() == null || cursoDTO.getNome().trim().isEmpty()) {
            throw new RegraDeNegocioException("O nome do curso não pode ser vazio.");
        }
    }

    private Curso converterParaEntidade(CursoDTO dto) {
        return new Curso(dto.getCodigo(), dto.getNome());
    }

    private CursoDTO converterParaDTO(Curso entidade) {
        return new CursoDTO(entidade.getCodigo(), entidade.getNome());
    }
}

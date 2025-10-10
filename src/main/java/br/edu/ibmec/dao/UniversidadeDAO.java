/**
* Aplicação com serviços REST para gestão de cursos.
*
* @author  Thiago Silva de Souza
* @version 1.0
* @since   2012-02-29 
*/

package br.edu.ibmec.dao;

import java.util.*;

import br.edu.ibmec.entity.Aluno;
import br.edu.ibmec.entity.Curso;
import br.edu.ibmec.entity.EstadoCivil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import org.springframework.dao.DataIntegrityViolationException;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

@Repository
public class UniversidadeDAO {

    private final JdbcTemplate jdbcTemplate;

    private static final String SELECT_ALUNO_BY_MATRICULA = "SELECT * FROM alunos WHERE matricula = ?";
    private static final String SELECT_ALL_ALUNOS = "SELECT * FROM alunos";
    private static final String INSERT_ALUNO = "INSERT INTO alunos (matricula, nome, data_nascimento, idade, matricula_ativa, estado_civil, curso_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_ALUNO = "UPDATE alunos SET nome = ?, data_nascimento = ?, idade = ?, matricula_ativa = ?, estado_civil = ?, curso_id = ? WHERE matricula = ?";
    private static final String DELETE_ALUNO = "DELETE FROM alunos WHERE matricula = ?";
    private static final String SELECT_ALUNOS_BY_CURSO_ID = "SELECT * FROM alunos WHERE curso_id = ?";

    private static final String SELECT_CURSO_BY_CODIGO = "SELECT * FROM cursos WHERE codigo = ?";
    private static final String SELECT_ALL_CURSOS = "SELECT * FROM cursos";
    private static final String INSERT_CURSO = "INSERT INTO cursos (codigo, nome) VALUES (?, ?)";
    private static final String UPDATE_CURSO = "UPDATE cursos SET nome = ? WHERE codigo = ?";
    private static final String DELETE_CURSO = "DELETE FROM cursos WHERE codigo = ?";
    private static final String COUNT_ALUNOS_IN_CURSO = "SELECT COUNT(*) FROM alunos WHERE curso_id = ?";

    @Autowired
    public UniversidadeDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Aluno> buscarAlunoPelaMatricula(String matricula) {
        try {
            Aluno aluno = jdbcTemplate.queryForObject(SELECT_ALUNO_BY_MATRICULA, new AlunoRowMapper(), matricula);
            return Optional.ofNullable(aluno);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<Aluno> buscarTodosAlunos() {
        return jdbcTemplate.query(SELECT_ALL_ALUNOS, new AlunoRowMapper());
    }

    public void salvarAluno(Aluno aluno) {
        if (aluno == null || aluno.getMatricula() == null) {
            throw new IllegalArgumentException("Aluno ou matrícula não pode ser nulo.");
        }
        jdbcTemplate.update(INSERT_ALUNO, aluno.getMatricula(), aluno.getNome(), aluno.getDataNascimento(),
                aluno.getIdade(), aluno.isMatriculaAtiva(), aluno.getEstadoCivil().toString(), aluno.getCurso().getCodigo());
    }

    public void atualizarAluno(Aluno aluno) {
        if (aluno == null || aluno.getMatricula() == null) {
            throw new IllegalArgumentException("Aluno ou matrícula não pode ser nulo para atualização.");
        }
        jdbcTemplate.update(UPDATE_ALUNO, aluno.getNome(), aluno.getDataNascimento(), aluno.isMatriculaAtiva(),
                aluno.getEstadoCivil().toString(), aluno.getCurso().getCodigo(), aluno.getMatricula());
    }

    public void removerAlunoPelaMatricula(String matricula) {
        jdbcTemplate.update(DELETE_ALUNO, matricula);
    }

    public Optional<Curso> buscarCursoPeloCodigo(int codCurso) {
        try {
            Curso curso = jdbcTemplate.queryForObject(SELECT_CURSO_BY_CODIGO, new CursoRowMapper(), codCurso);
            if (curso != null) {
                List<Aluno> alunosDoCurso = jdbcTemplate.query(SELECT_ALUNOS_BY_CURSO_ID, new AlunoRowMapper(), codCurso);
                curso.setAlunos(alunosDoCurso);
            }
            return Optional.ofNullable(curso);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<Curso> buscarTodosCursos() {
        return jdbcTemplate.query(SELECT_ALL_CURSOS, new CursoRowMapper());
    }

    public void salvarCurso(Curso curso) {
        if (curso == null) throw new IllegalArgumentException("Curso não pode ser nulo.");
        jdbcTemplate.update(INSERT_CURSO, curso.getCodigo(), curso.getNome());
    }

    public void atualizarCurso(Curso curso) {
        if (curso == null) throw new IllegalArgumentException("Curso não pode ser nulo.");
        jdbcTemplate.update(UPDATE_CURSO, curso.getNome(), curso.getCodigo());
    }

    public void removerCursoPeloCodigo(int codCurso) {
        Integer numeroDeAlunos = jdbcTemplate.queryForObject(COUNT_ALUNOS_IN_CURSO, Integer.class, codCurso);

        if (numeroDeAlunos != null && numeroDeAlunos > 0) {
            throw new DataIntegrityViolationException("Curso com código " + codCurso + " não pode ser removido pois possui " + numeroDeAlunos + " aluno(s) matriculado(s).");
        }

        jdbcTemplate.update(DELETE_CURSO, codCurso);
    }

    private static class AlunoRowMapper implements RowMapper<Aluno> {
        @Override
        public Aluno mapRow(ResultSet rs, int rowNum) throws SQLException {
            Curso curso = new Curso();
            curso.setCodigo(rs.getInt("curso_id"));

            return Aluno.builder()
                    .matricula(rs.getString("matricula"))
                    .nome(rs.getString("nome"))
                    .dataNascimento(rs.getObject("data_nascimento", java.time.LocalDate.class))
                    .matriculaAtiva(rs.getBoolean("matricula_ativa"))
                    .estadoCivil(EstadoCivil.valueOf(rs.getString("estado_civil")))
                    .curso(curso)
                    .build();
        }
    }

    private static class CursoRowMapper implements RowMapper<Curso> {
        @Override
        public Curso mapRow(ResultSet rs, int rowNum) throws SQLException {
            return Curso.builder()
                    .codigo(rs.getInt("codigo"))
                    .nome(rs.getString("nome"))
                    .build();
        }
    }

}
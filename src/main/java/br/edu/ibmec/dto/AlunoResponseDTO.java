package br.edu.ibmec.dto;

import java.time.LocalDate;
import java.util.List;

import br.edu.ibmec.entity.Aluno;
import lombok.Data;

@Data
public class AlunoResponseDTO {
    private String matricula;
    private String nome;
    private LocalDate dataNascimento;
    private int idade;
    private boolean matriculaAtiva;
    private Float bolsaPorcentagem;
    private List<String> telefones;
    private int curso;

    public static AlunoResponseDTO fromEntity(Aluno aluno) {
        AlunoResponseDTO dto = new AlunoResponseDTO();
        dto.setMatricula(aluno.getMatricula());
        dto.setNome(aluno.getNome());
        dto.setDataNascimento(aluno.getDataNascimento());
        dto.setIdade(aluno.getIdade());
        dto.setMatriculaAtiva(aluno.isMatriculaAtiva());
        dto.setBolsaPorcentagem(aluno.getBolsaPorcentagem());
        dto.setTelefones(aluno.getTelefones());
        dto.setCurso(aluno.getCurso().getCodigo());
        return dto;
    }

}

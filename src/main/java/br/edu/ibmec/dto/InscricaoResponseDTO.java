package br.edu.ibmec.dto;

import br.edu.ibmec.entity.Inscricao;
import lombok.Data;

@Data
public class InscricaoResponseDTO {
    private String alunoMatricula;
    private String turmaCodigo;

    private Float avaliacao1;
    private Float avaliacao2;
    private Float media;
    private Integer numFaltas;
    private String situacao;

    public static InscricaoResponseDTO fromEntity(Inscricao inscricao) {
        InscricaoResponseDTO dto = new InscricaoResponseDTO();
        dto.setAlunoMatricula(inscricao.getAluno().getMatricula());
        dto.setTurmaCodigo(inscricao.getTurma().getCodigo());

        dto.setAvaliacao1(inscricao.getAvaliacao1());
        dto.setAvaliacao2(inscricao.getAvaliacao2());
        dto.setMedia(inscricao.getMedia());
        dto.setNumFaltas(inscricao.getNumFaltas());
        dto.setSituacao(inscricao.getSituacao());

        return dto;
    }
}
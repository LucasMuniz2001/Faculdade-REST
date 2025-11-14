package br.edu.ibmec.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Inscricao")
public class Inscricao {

    @EmbeddedId
    private InscricaoId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("alunoMatricula")
    @JoinColumn(name = "aluno_matricula")
    private Aluno aluno;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("turmaCodigo")
    @JoinColumn(name = "turma_codigo", referencedColumnName = "codigo")
    private Turma turma;

    @Column(name = "data_inscricao")
    private LocalDate dataInscricao;

    @Column(name = "avaliacao1")
    private Float avaliacao1;

    @Column(name = "avaliacao2")
    private Float avaliacao2;

    @Column(name = "media")
    private Float media;

    @Column(name = "num_faltas")
    private Integer numFaltas;

    @Column(name = "situacao")
    private String situacao;

    @Column(name = "status")
    private String status;
}
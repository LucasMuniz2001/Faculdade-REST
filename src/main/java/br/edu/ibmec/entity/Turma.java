package br.edu.ibmec.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Turma")
public class Turma {

    @Id
    @Column(name = "codigo")
    private String codigo;

    @Column(name = "ano")
    private Integer ano;

    @Column(name = "semestre")
    private Integer semestre;


    @ManyToOne
    @JoinColumn(name = "disciplina_codigo")
    private Disciplina disciplina;

    @ManyToOne
    @JoinColumn(name = "professor_matricula")
    private Professor professor;

    @OneToMany(mappedBy = "turma")
    private List<Inscricao> inscricoes = new ArrayList<>();
}
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
@Table(name = "Disciplina")
public class Disciplina {

    @Id
    @Column (name = "codigo")
    private Integer codigo;

    @Column(name = "nome")
    private String nome;

    @ManyToOne
    @JoinColumn(name = "codigo_curso")
    private Curso curso;

    @OneToMany(mappedBy = "disciplina")
    private List<Turma> turmas = new ArrayList<>();
}
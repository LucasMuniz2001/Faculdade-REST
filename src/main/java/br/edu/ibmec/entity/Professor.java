package br.edu.ibmec.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Professor {

    @Id
    private String matricula;

    @Column(name = "nome")
    private String nome;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @Transient
    private int idade;

    @Column(name = "matricula_ativa")
    private boolean matriculaAtiva;

    @OneToMany(mappedBy = "professor")
    private List<Turma> turmas = new ArrayList<>();

    public int getIdade() {
        if (this.dataNascimento != null) {
            return Period.between(this.dataNascimento, LocalDate.now()).getYears();
        }
        return 0;
    }
}
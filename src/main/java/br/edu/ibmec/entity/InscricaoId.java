package br.edu.ibmec.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class InscricaoId implements Serializable {

    private String alunoMatricula;
    private String turmaCodigo;
}
package br.edu.ibmec.service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;
import java.util.concurrent.ThreadLocalRandom;

import br.edu.ibmec.dto.AlunoResponseDTO;
import br.edu.ibmec.dto.AlunoRequestDTO;
import br.edu.ibmec.dto.EstadoCivilDTO;
import br.edu.ibmec.entity.Aluno;
import br.edu.ibmec.entity.Curso;
import br.edu.ibmec.entity.EstadoCivil;
import br.edu.ibmec.entity.Inscricao;
import br.edu.ibmec.repository.AlunoRepository;
import br.edu.ibmec.repository.CursoRepository;
import br.edu.ibmec.repository.InscricaoRepository;
import br.edu.ibmec.service.strategy.CalculoMensalidadeStrategy;
import br.edu.ibmec.service.strategy.MensalidadeSemBolsaStrategy;
import br.edu.ibmec.service.strategy.MensalidadeComBolsaStrategy;
import org.springframework.stereotype.Service;

import br.edu.ibmec.exception.RegraDeNegocioException;
import br.edu.ibmec.exception.EntidadeNaoEncontradaException;

@Service
public class AlunoService {
    private AlunoRepository alunoRepository;
    private CursoRepository cursoRepository;
    private InscricaoRepository inscricaoRepository;

    private final MensalidadeSemBolsaStrategy semBolsaStrategy;
    private final MensalidadeComBolsaStrategy comBolsaStrategy;

    public AlunoService(AlunoRepository alunoRepository, CursoRepository cursoRepository,
                        InscricaoRepository inscricaoRepository,
                        MensalidadeSemBolsaStrategy semBolsaStrategy,
                        MensalidadeComBolsaStrategy comBolsaStrategy) {
        this.alunoRepository = alunoRepository;
        this.cursoRepository = cursoRepository;
        this.inscricaoRepository = inscricaoRepository;
        this.semBolsaStrategy = semBolsaStrategy;
        this.comBolsaStrategy = comBolsaStrategy;
    }

    public AlunoResponseDTO buscarAluno(String matricula) {
        return alunoRepository.findById(matricula)
                .map(AlunoResponseDTO::fromEntity)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Aluno com matrícula " + matricula + " não encontrado"));
    }

    public List<AlunoResponseDTO> listarAlunos() {
        List<Aluno> alunos = alunoRepository.findAll();
        return alunos.stream().map(AlunoResponseDTO::fromEntity).collect(Collectors.toList());
    }

    public AlunoResponseDTO cadastrarAluno(AlunoRequestDTO alunoRequestDTO) {
        validarDadosDoAluno(alunoRequestDTO);

        Aluno novoAluno = new Aluno();

        String novaMatricula = gerarMatriculaUnica();
        novoAluno.setMatricula(novaMatricula);

        atualizarEntidadeComDTO(novoAluno, alunoRequestDTO);

        alunoRepository.save(novoAluno);

        return AlunoResponseDTO.fromEntity(novoAluno);
    }

    public AlunoResponseDTO alterarAluno(String matricula, AlunoRequestDTO alunoRequestDTO) {
        Aluno alunoExistente = alunoRepository.findById(matricula)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Aluno com matrícula " + matricula + " não encontrado para atualização."));

        validarDadosDoAluno(alunoRequestDTO);
        atualizarEntidadeComDTO(alunoExistente, alunoRequestDTO);

        alunoRepository.save(alunoExistente);

        return AlunoResponseDTO.fromEntity(alunoExistente);
    }

    public void removerAluno(String matricula) {
        if (alunoRepository.findById(matricula).isEmpty()) {
            throw new EntidadeNaoEncontradaException("Aluno com matrícula " + matricula + " não encontrado para remoção.");
        }

        alunoRepository.deleteById(matricula);
    }

    private void validarDadosDoAluno(AlunoRequestDTO dto) {
        if (dto.getNome() == null || dto.getNome().trim().length() < 1 || dto.getNome().length() > 20) {
            throw new RegraDeNegocioException("O nome do aluno é obrigatório e deve ter entre 1 e 20 caracteres.");
        }
        if (dto.getDataNascimento() == null) {
            throw new RegraDeNegocioException("A data de nascimento é obrigatória.");
        }
        if (dto.getBolsaPorcentagem() != null && (dto.getBolsaPorcentagem() < 0.0f || dto.getBolsaPorcentagem() > 100.0f)) {
            throw new RegraDeNegocioException("A porcentagem de bolsa deve ser entre 0 e 100.");
        }
    }

    private void atualizarEntidadeComDTO(Aluno aluno, AlunoRequestDTO alunoRequestDTO) {
        aluno.setNome(alunoRequestDTO.getNome());
        aluno.setDataNascimento(alunoRequestDTO.getDataNascimento());
        aluno.setMatriculaAtiva(alunoRequestDTO.isMatriculaAtiva());
        aluno.setTelefones(alunoRequestDTO.getTelefones());
        aluno.setBolsaPorcentagem(alunoRequestDTO.getBolsaPorcentagem()); // Mapeamento da bolsa

        EstadoCivil estadoCivil = converterEstadoCivil(alunoRequestDTO.getEstadoCivil());
        aluno.setEstadoCivil(estadoCivil);

        if (aluno.getCurso() == null || !aluno.getCurso().getCodigo().equals(alunoRequestDTO.getCurso())) {
            Curso novoCurso = cursoRepository.findById(alunoRequestDTO.getCurso())
                    .orElseThrow(() -> new RegraDeNegocioException("O curso com código " + alunoRequestDTO.getCurso() + " não foi encontrado."));

            aluno.setCurso(novoCurso);
        }
    }

    private String gerarMatriculaUnica() {
        LocalDate agora = LocalDate.now();

        int anoAtual = agora.getYear();
        int mesAtual = agora.getMonthValue();

        String mesFormatado = String.format("%02d", mesAtual);

        int numeroAleatorio = ThreadLocalRandom.current().nextInt(10000, 99999);

        return "" + anoAtual + mesFormatado + numeroAleatorio;
    }

    private EstadoCivil converterEstadoCivil(EstadoCivilDTO estadoCivilDTO) {
        if (estadoCivilDTO == null) {
            return null;
        }
        return EstadoCivil.valueOf(estadoCivilDTO.name());
    }

    // MÉTODO DE CÁLCULO DA MENSALIDADE (Contexto do Strategy)
    public Float calcularMensalidade(String matricula) {
        Aluno aluno = alunoRepository.findById(matricula)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Aluno com matrícula " + matricula + " não encontrado."));

        List<Inscricao> inscricoes = aluno.getInscricoes();

        if (inscricoes == null || inscricoes.isEmpty()) {
            return 0.0f;
        }

        // 1. Cálculo do valor base (Soma do valor de cada disciplina única)
        Float valorBase = (float) inscricoes.stream()
                .map(inscricao -> inscricao.getTurma().getDisciplina())
                .distinct()
                .mapToDouble(disciplina -> {
                    Curso cursoDaDisciplina = disciplina.getCurso();
                    if (cursoDaDisciplina == null || cursoDaDisciplina.getValorBaseDisciplina() == null) {
                        return 0.0f;
                    }
                    return (float) cursoDaDisciplina.getValorBaseDisciplina();
                })
                .sum();

        // 2. SELEÇÃO DA ESTRATÉGIA (O Contexto decide qual algoritmo usar)
        CalculoMensalidadeStrategy strategy;

        Float bolsaPorcentagem = aluno.getBolsaPorcentagem();

        if (bolsaPorcentagem != null && bolsaPorcentagem > 0.0f) {
            strategy = comBolsaStrategy; // Estratégia de desconto
        } else {
            strategy = semBolsaStrategy; // Estratégia sem desconto
        }

        // 3. APLICAÇÃO DA ESTRATÉGIA SELECIONADA
        return strategy.aplicarDesconto(valorBase, bolsaPorcentagem);
    }
}
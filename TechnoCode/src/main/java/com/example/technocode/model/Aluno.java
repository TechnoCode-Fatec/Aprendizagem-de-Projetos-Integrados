package com.example.technocode.model;

import com.example.technocode.model.dao.Connector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Classe modelo para representar um Aluno
 */
public class Aluno {
    private String nome;
    private String email;
    private String senha;
    private String orientador; // email do orientador
    private String professorTG; // email do professor de TG
    private String disciplinaTG; // TG1, TG2 ou TG1/TG2
    private String curso;

    /**
     * Constructor completo para Aluno
     * @param nome Nome do aluno
     * @param email Email do aluno
     * @param senha Senha do aluno
     * @param orientador Email do orientador
     * @param professorTG Email do professor de TG
     * @param disciplinaTG Disciplina TG (TG1, TG2 ou TG1/TG2)
     * @param curso Curso do aluno (pode ser null)
     */
    public Aluno(String nome, String email, String senha, String orientador, String professorTG, String disciplinaTG, String curso) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.orientador = orientador;
        this.professorTG = professorTG;
        this.disciplinaTG = disciplinaTG;
        this.curso = curso;
    }
    
    /**
     * Constructor sem disciplinaTG (para compatibilidade)
     */
    public Aluno(String nome, String email, String senha, String orientador, String professorTG, String curso) {
        this(nome, email, senha, orientador, professorTG, null, curso);
    }

    /**
     * Constructor sem senha (útil para consultas)
     */
    public Aluno(String nome, String email, String orientador, String curso) {
        this.nome = nome;
        this.email = email;
        this.orientador = orientador;
        this.curso = curso;
    }

    // Getters e Setters
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOrientador() {
        return orientador;
    }

    public void setOrientador(String orientador) {
        this.orientador = orientador;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    public String getProfessorTG() {
        return professorTG;
    }

    public void setProfessorTG(String professorTG) {
        this.professorTG = professorTG;
    }

    public String getDisciplinaTG() {
        return disciplinaTG;
    }

    public void setDisciplinaTG(String disciplinaTG) {
        this.disciplinaTG = disciplinaTG;
    }

    // ============ MÉTODOS DAO ============

    /**
     * Cadastra um novo aluno no banco de dados
     */
    public void cadastrar() {
        String insertSql = "INSERT INTO aluno (nome, email, senha, orientador, professor_tg, disciplina_tg) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = new Connector().getConnection();
             PreparedStatement pst = con.prepareStatement(insertSql)) {

            pst.setString(1, this.nome);
            pst.setString(2, this.email);
            pst.setString(3, this.senha);
            pst.setString(4, this.orientador);
            pst.setString(5, this.professorTG);
            pst.setString(6, this.disciplinaTG);
            pst.executeUpdate();

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Erro ao cadastrar aluno!", ex);
        }
    }

    /**
     * Busca lista de alunos por orientador
     */
    public static List<Map<String, String>> buscarPorOrientador(String emailOrientador) {
        List<Map<String, String>> alunos = new ArrayList<>();
        try (Connection conn = new Connector().getConnection()) {
            String selectAlunos = "SELECT a.nome, a.email, COALESCE(pt.disciplina, 'N/A') as disciplina " +
                    "FROM aluno a " +
                    "LEFT JOIN professor_tg pt ON a.professor_tg = pt.email " +
                    "WHERE a.orientador = ?";
            PreparedStatement pst = conn.prepareStatement(selectAlunos);
            pst.setString(1, emailOrientador);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Map<String, String> aluno = new HashMap<>();
                aluno.put("nome", rs.getString("nome"));
                aluno.put("email", rs.getString("email"));
                String disciplina = rs.getString("disciplina");
                // Formata a disciplina para exibição (TG1 -> TG 1, TG2 -> TG 2)
                String disciplinaFormatada = formatarDisciplina(disciplina);
                aluno.put("professor_tg", disciplinaFormatada);
                alunos.add(aluno);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return alunos;
    }
    
    /**
     * Formata a disciplina para exibição
     * TG1 -> TG 1, TG2 -> TG 2, TG1/TG2 -> TG 1/TG 2
     */
    public static String formatarDisciplina(String disciplina) {
        if (disciplina == null || disciplina.equals("N/A")) {
            return disciplina;
        }
        // Converte "TG1" para "TG 1", "TG2" para "TG 2", "TG1/TG2" para "TG 1/TG 2"
        return disciplina.replace("TG1", "TG 1").replace("TG2", "TG 2");
    }

    /**
     * Busca dados de um aluno por email
     */
    public static Map<String, String> buscarDadosPorEmail(String emailAluno) {
        Map<String, String> dadosAluno = new HashMap<>();
        try (Connection conn = new Connector().getConnection()) {
            String selectAluno = "SELECT nome, email, disciplina_tg FROM aluno WHERE email = ?";
            PreparedStatement pst = conn.prepareStatement(selectAluno);
            pst.setString(1, emailAluno);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                dadosAluno.put("nome", rs.getString("nome"));
                dadosAluno.put("email", rs.getString("email"));
                dadosAluno.put("disciplina_tg", rs.getString("disciplina_tg"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar dados do aluno", e);
        }
        return dadosAluno;
    }

    /**
     * Verifica se existe um aluno com email e senha fornecidos (para login)
     */
    public static boolean autenticar(String email, String senha) {
        try (Connection con = new Connector().getConnection()) {
            String sql = "SELECT * FROM aluno WHERE email = ? AND senha = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, email);
            pst.setString(2, senha);
            ResultSet rs = pst.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Conta seções aprovadas e enviadas por aluno
     * Considera apenas versões mais recentes para contagem de enviadas
     * @param emailAluno Email do aluno
     * @return Map com "aprovadas" (int) e "enviadas" (int)
     */
    public static Map<String, Integer> contarSecoesPorAluno(String emailAluno) {
        Map<String, Integer> resultado = new HashMap<>();
        resultado.put("aprovadas", 0);
        resultado.put("enviadas", 0);
        
        try (Connection conn = new Connector().getConnection()) {
            // Conta seções de apresentação enviadas (cada aluno tem apenas 1 seção de apresentação, independente de versões)
            // Verifica se existe pelo menos uma versão de apresentação (retorna 0 ou 1)
            String sqlApresentacaoEnviadas = "SELECT CASE WHEN COUNT(*) > 0 THEN 1 ELSE 0 END as total FROM secao_apresentacao WHERE aluno = ?";
            PreparedStatement pst1 = conn.prepareStatement(sqlApresentacaoEnviadas);
            pst1.setString(1, emailAluno);
            ResultSet rs1 = pst1.executeQuery();
            int apresentacoesEnviadas = rs1.next() ? rs1.getInt("total") : 0;
            
            // Conta seções de API enviadas (combinações distintas de semestre_curso, ano, semestre_ano)
            // Cada combinação única conta como uma seção, independente da versão
            String sqlApiEnviadas = "SELECT COUNT(DISTINCT CONCAT(semestre_curso, '-', ano, '-', semestre_ano)) as total " +
                                    "FROM secao_api WHERE aluno = ?";
            PreparedStatement pst2 = conn.prepareStatement(sqlApiEnviadas);
            pst2.setString(1, emailAluno);
            ResultSet rs2 = pst2.executeQuery();
            int apisEnviadas = rs2.next() ? rs2.getInt("total") : 0;
            
            int totalEnviadas = apresentacoesEnviadas + apisEnviadas;
            resultado.put("enviadas", totalEnviadas);
            
            // Conta seções de apresentação aprovadas (onde todos os campos estão aprovados)
            String sqlApresentacaoAprovadas = "SELECT COUNT(*) as total FROM ( " +
                    "SELECT aluno, MAX(versao) as versao_recente " +
                    "FROM secao_apresentacao WHERE aluno = ? " +
                    "GROUP BY aluno " +
                    ") AS versoes_recentes " +
                    "INNER JOIN secao_apresentacao fa ON " +
                    "  versoes_recentes.aluno = fa.aluno AND " +
                    "  versoes_recentes.versao_recente = fa.versao " +
                    "WHERE fa.status_nome = 'Aprovado' AND fa.status_idade = 'Aprovado' " +
                    "AND fa.status_curso = 'Aprovado' AND fa.status_motivacao = 'Aprovado' " +
                    "AND fa.status_historico = 'Aprovado' AND fa.status_historico_profissional = 'Aprovado' " +
                    "AND fa.status_github = 'Aprovado' AND fa.status_linkedin = 'Aprovado' " +
                    "AND fa.status_conhecimentos = 'Aprovado'";
            PreparedStatement pst3 = conn.prepareStatement(sqlApresentacaoAprovadas);
            pst3.setString(1, emailAluno);
            ResultSet rs3 = pst3.executeQuery();
            int apresentacoesAprovadas = rs3.next() ? rs3.getInt("total") : 0;
            
            // Conta seções de API aprovadas (onde todos os campos estão aprovados)
            String sqlApiAprovadas = "SELECT COUNT(*) as total FROM ( " +
                    "SELECT aluno, semestre_curso, ano, semestre_ano, MAX(versao) as versao_recente " +
                    "FROM secao_api WHERE aluno = ? " +
                    "GROUP BY aluno, semestre_curso, ano, semestre_ano " +
                    ") AS versoes_recentes " +
                    "INNER JOIN secao_api fa ON " +
                    "  versoes_recentes.aluno = fa.aluno AND " +
                    "  versoes_recentes.semestre_curso = fa.semestre_curso AND " +
                    "  versoes_recentes.ano = fa.ano AND " +
                    "  versoes_recentes.semestre_ano = fa.semestre_ano AND " +
                    "  versoes_recentes.versao_recente = fa.versao " +
                    "WHERE fa.status_empresa = 'Aprovado' AND fa.status_descricao_empresa = 'Aprovado' " +
                    "AND fa.status_repositorio = 'Aprovado' AND fa.status_problema = 'Aprovado' " +
                    "AND fa.status_solucao = 'Aprovado' AND fa.status_tecnologias = 'Aprovado' " +
                    "AND fa.status_contribuicoes = 'Aprovado' AND fa.status_hard_skills = 'Aprovado' " +
                    "AND fa.status_soft_skills = 'Aprovado'";
            PreparedStatement pst4 = conn.prepareStatement(sqlApiAprovadas);
            pst4.setString(1, emailAluno);
            ResultSet rs4 = pst4.executeQuery();
            int apisAprovadas = rs4.next() ? rs4.getInt("total") : 0;
            
            int totalAprovadas = apresentacoesAprovadas + apisAprovadas;
            resultado.put("aprovadas", totalAprovadas);
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return resultado;
    }
}


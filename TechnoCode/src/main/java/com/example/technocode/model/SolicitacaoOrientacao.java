package com.example.technocode.model;

import com.example.technocode.model.dao.Connector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Classe modelo para representar uma Solicitação de Orientação
 */
public class SolicitacaoOrientacao {
    private int id;
    private String aluno; // email do aluno
    private String orientador; // email do orientador
    private String status; // 'Pendente', 'Aceita', 'Recusada'
    private String mensagemOrientador;
    private Timestamp dataSolicitacao;
    private Timestamp dataResposta;

    /**
     * Constructor completo
     */
    public SolicitacaoOrientacao(String aluno, String orientador, String status, String mensagemOrientador) {
        this.aluno = aluno;
        this.orientador = orientador;
        this.status = status;
        this.mensagemOrientador = mensagemOrientador;
    }

    /**
     * Constructor para criar nova solicitação
     */
    public SolicitacaoOrientacao(String aluno, String orientador) {
        this.aluno = aluno;
        this.orientador = orientador;
        this.status = "Pendente";
        this.mensagemOrientador = null;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAluno() {
        return aluno;
    }

    public void setAluno(String aluno) {
        this.aluno = aluno;
    }

    public String getOrientador() {
        return orientador;
    }

    public void setOrientador(String orientador) {
        this.orientador = orientador;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMensagemOrientador() {
        return mensagemOrientador;
    }

    public void setMensagemOrientador(String mensagemOrientador) {
        this.mensagemOrientador = mensagemOrientador;
    }

    public Timestamp getDataSolicitacao() {
        return dataSolicitacao;
    }

    public void setDataSolicitacao(Timestamp dataSolicitacao) {
        this.dataSolicitacao = dataSolicitacao;
    }

    public Timestamp getDataResposta() {
        return dataResposta;
    }

    public void setDataResposta(Timestamp dataResposta) {
        this.dataResposta = dataResposta;
    }

    // ============ MÉTODOS DAO ============

    /**
     * Cria uma nova solicitação de orientação
     */
    public void criar() {
        String insertSql = "INSERT INTO solicitacao_orientacao (aluno, orientador, status) VALUES (?, ?, ?)";

        try (Connection con = new Connector().getConnection();
             PreparedStatement pst = con.prepareStatement(insertSql)) {

            pst.setString(1, this.aluno);
            pst.setString(2, this.orientador);
            pst.setString(3, this.status);
            pst.executeUpdate();

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Erro ao criar solicitação de orientação!", ex);
        }
    }

    /**
     * Aceita uma solicitação de orientação
     * Atualiza o orientador do aluno e marca a solicitação como aceita
     */
    public void aceitar() {
        String updateSql = "UPDATE solicitacao_orientacao SET status = 'Aceita', data_resposta = CURRENT_TIMESTAMP WHERE id = ?";
        String updateAlunoSql = "UPDATE aluno SET orientador = ? WHERE email = ?";

        try (Connection con = new Connector().getConnection()) {
            con.setAutoCommit(false);

            try {
                // Atualiza a solicitação
                PreparedStatement pstSolicitacao = con.prepareStatement(updateSql);
                pstSolicitacao.setInt(1, this.id);
                pstSolicitacao.executeUpdate();

                // Atualiza o orientador do aluno
                PreparedStatement pstAluno = con.prepareStatement(updateAlunoSql);
                pstAluno.setString(1, this.orientador);
                pstAluno.setString(2, this.aluno);
                pstAluno.executeUpdate();

                con.commit();
                this.status = "Aceita";
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Erro ao aceitar solicitação de orientação!", ex);
        }
    }

    /**
     * Recusa uma solicitação de orientação
     */
    public void recusar(String mensagem) {
        String updateSql = "UPDATE solicitacao_orientacao SET status = 'Recusada', mensagem_orientador = ?, data_resposta = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection con = new Connector().getConnection();
             PreparedStatement pst = con.prepareStatement(updateSql)) {

            pst.setString(1, mensagem);
            pst.setInt(2, this.id);
            pst.executeUpdate();

            this.status = "Recusada";
            this.mensagemOrientador = mensagem;

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Erro ao recusar solicitação de orientação!", ex);
        }
    }

    /**
     * Busca todas as solicitações pendentes de um orientador
     */
    public static List<Map<String, String>> buscarPendentesPorOrientador(String emailOrientador) {
        List<Map<String, String>> solicitacoes = new ArrayList<>();
        String sql = "SELECT so.id, so.aluno, so.orientador, so.status, so.mensagem_orientador, " +
                     "so.data_solicitacao, so.data_resposta, a.nome as nome_aluno " +
                     "FROM solicitacao_orientacao so " +
                     "INNER JOIN aluno a ON so.aluno = a.email " +
                     "WHERE so.orientador = ? AND so.status = 'Pendente' " +
                     "ORDER BY so.data_solicitacao DESC";

        try (Connection conn = new Connector().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, emailOrientador);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Map<String, String> solicitacao = new HashMap<>();
                solicitacao.put("id", String.valueOf(rs.getInt("id")));
                solicitacao.put("aluno", rs.getString("aluno"));
                solicitacao.put("nome_aluno", rs.getString("nome_aluno"));
                solicitacao.put("orientador", rs.getString("orientador"));
                solicitacao.put("status", rs.getString("status"));
                solicitacao.put("mensagem_orientador", rs.getString("mensagem_orientador"));
                solicitacao.put("data_solicitacao", rs.getTimestamp("data_solicitacao") != null ? 
                    rs.getTimestamp("data_solicitacao").toString() : null);
                solicitacao.put("data_resposta", rs.getTimestamp("data_resposta") != null ? 
                    rs.getTimestamp("data_resposta").toString() : null);
                solicitacoes.add(solicitacao);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao buscar solicitações pendentes", e);
        }

        return solicitacoes;
    }

    /**
     * Busca todas as solicitações de um aluno
     */
    public static List<Map<String, String>> buscarPorAluno(String emailAluno) {
        List<Map<String, String>> solicitacoes = new ArrayList<>();
        String sql = "SELECT so.id, so.aluno, so.orientador, so.status, so.mensagem_orientador, " +
                     "so.data_solicitacao, so.data_resposta, o.nome as nome_orientador " +
                     "FROM solicitacao_orientacao so " +
                     "INNER JOIN orientador o ON so.orientador = o.email " +
                     "WHERE so.aluno = ? " +
                     "ORDER BY so.data_solicitacao DESC";

        try (Connection conn = new Connector().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, emailAluno);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Map<String, String> solicitacao = new HashMap<>();
                solicitacao.put("id", String.valueOf(rs.getInt("id")));
                solicitacao.put("aluno", rs.getString("aluno"));
                solicitacao.put("orientador", rs.getString("orientador"));
                solicitacao.put("nome_orientador", rs.getString("nome_orientador"));
                solicitacao.put("status", rs.getString("status"));
                solicitacao.put("mensagem_orientador", rs.getString("mensagem_orientador"));
                solicitacao.put("data_solicitacao", rs.getTimestamp("data_solicitacao") != null ? 
                    rs.getTimestamp("data_solicitacao").toString() : null);
                solicitacao.put("data_resposta", rs.getTimestamp("data_resposta") != null ? 
                    rs.getTimestamp("data_resposta").toString() : null);
                solicitacoes.add(solicitacao);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao buscar solicitações do aluno", e);
        }

        return solicitacoes;
    }

    /**
     * Busca uma solicitação por ID
     */
    public static SolicitacaoOrientacao buscarPorId(int id) {
        String sql = "SELECT * FROM solicitacao_orientacao WHERE id = ?";

        try (Connection conn = new Connector().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                SolicitacaoOrientacao solicitacao = new SolicitacaoOrientacao(
                    rs.getString("aluno"),
                    rs.getString("orientador"),
                    rs.getString("status"),
                    rs.getString("mensagem_orientador")
                );
                solicitacao.setId(rs.getInt("id"));
                solicitacao.setDataSolicitacao(rs.getTimestamp("data_solicitacao"));
                solicitacao.setDataResposta(rs.getTimestamp("data_resposta"));
                return solicitacao;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao buscar solicitação por ID", e);
        }

        return null;
    }

    /**
     * Verifica se já existe uma solicitação pendente do aluno para o orientador
     */
    public static boolean existeSolicitacaoPendente(String emailAluno, String emailOrientador) {
        String sql = "SELECT COUNT(*) FROM solicitacao_orientacao " +
                     "WHERE aluno = ? AND orientador = ? AND status = 'Pendente'";

        try (Connection conn = new Connector().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, emailAluno);
            pst.setString(2, emailOrientador);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao verificar solicitação pendente", e);
        }

        return false;
    }
}


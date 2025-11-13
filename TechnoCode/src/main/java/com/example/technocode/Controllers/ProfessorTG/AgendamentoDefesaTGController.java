package com.example.technocode.Controllers.ProfessorTG;

import com.example.technocode.Controllers.LoginController;
import com.example.technocode.model.ProfessorTG;
import com.example.technocode.model.dao.Connector;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller para a tela de Agendamento de Defesas de TG
 * Permite ao professor agendar, editar e excluir defesas dos alunos de sua disciplina
 */
public class AgendamentoDefesaTGController {

    @FXML
    private ComboBox<String> comboAluno;

    @FXML
    private DatePicker datePickerData;

    @FXML
    private TextField txtHorario;

    @FXML
    private TextField txtSala;

    @FXML
    private TableView<AgendamentoInfo> tabelaAgendamentos;

    @FXML
    private TableColumn<AgendamentoInfo, String> colAluno;

    @FXML
    private TableColumn<AgendamentoInfo, String> colData;

    @FXML
    private TableColumn<AgendamentoInfo, String> colHorario;

    @FXML
    private TableColumn<AgendamentoInfo, String> colSala;

    @FXML
    private Button btnEditar;

    @FXML
    private Button btnExcluir;

    private String emailProfessorLogado;
    private String disciplinaProfessor;
    private Integer agendamentoSelecionadoId; // ID do agendamento selecionado para edição

    @FXML
    private void initialize() {
        // Obtém o email do professor logado
        emailProfessorLogado = LoginController.getEmailLogado();
        
        if (emailProfessorLogado == null || emailProfessorLogado.isBlank()) {
            mostrarErro("Erro", "Não foi possível identificar o professor logado.");
            return;
        }

        // Busca os dados do professor (incluindo disciplina)
        Map<String, String> dadosProfessor = ProfessorTG.buscarDadosPorEmail(emailProfessorLogado);
        disciplinaProfessor = dadosProfessor.get("disciplina");

        // Configura as colunas da tabela
        configurarTabela();

        // Carrega os dados iniciais
        carregarAlunos();
        carregarAgendamentos();
    }

    /**
     * Configura as colunas da tabela de agendamentos
     */
    private void configurarTabela() {
        colAluno.setCellValueFactory(new PropertyValueFactory<>("nomeAluno"));
        colData.setCellValueFactory(new PropertyValueFactory<>("data"));
        colHorario.setCellValueFactory(new PropertyValueFactory<>("horario"));
        colSala.setCellValueFactory(new PropertyValueFactory<>("sala"));

        // Permite seleção de linha
        tabelaAgendamentos.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                agendamentoSelecionadoId = newSelection.getId();
                btnEditar.setDisable(false);
                btnExcluir.setDisable(false);
            } else {
                agendamentoSelecionadoId = null;
                btnEditar.setDisable(true);
                btnExcluir.setDisable(true);
            }
        });

        // Inicialmente desabilita os botões de editar e excluir
        btnEditar.setDisable(true);
        btnExcluir.setDisable(true);
    }

    /**
     * Carrega os alunos da disciplina do professor no ComboBox
     */
    private void carregarAlunos() {
        try (Connection conn = new Connector().getConnection()) {
            String sql = "SELECT nome, email FROM aluno WHERE professor_tg = ? ORDER BY nome";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, emailProfessorLogado);
            ResultSet rs = pst.executeQuery();

            ObservableList<String> alunos = FXCollections.observableArrayList();
            while (rs.next()) {
                String nome = rs.getString("nome");
                alunos.add(nome);
            }

            comboAluno.setItems(alunos);

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarErro("Erro", "Erro ao carregar lista de alunos: " + e.getMessage());
        }
    }

    /**
     * Carrega os agendamentos do professor na tabela
     */
    @FXML
    private void carregarAgendamentos() {
        try (Connection conn = new Connector().getConnection()) {
            String sql = "SELECT ad.id, a.nome as nome_aluno, ad.data_defesa, ad.horario, ad.sala " +
                        "FROM agendamento_defesa_tg ad " +
                        "INNER JOIN aluno a ON ad.email_aluno = a.email " +
                        "WHERE ad.email_professor = ? " +
                        "ORDER BY ad.data_defesa, ad.horario";
            
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, emailProfessorLogado);
            ResultSet rs = pst.executeQuery();

            ObservableList<AgendamentoInfo> agendamentos = FXCollections.observableArrayList();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            while (rs.next()) {
                int id = rs.getInt("id");
                String nomeAluno = rs.getString("nome_aluno");
                LocalDate data = rs.getDate("data_defesa").toLocalDate();
                String horario = rs.getString("horario");
                String sala = rs.getString("sala");

                agendamentos.add(new AgendamentoInfo(
                    id,
                    nomeAluno,
                    data.format(formatter),
                    horario,
                    sala
                ));
            }

            tabelaAgendamentos.setItems(agendamentos);

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarErro("Erro", "Erro ao carregar agendamentos: " + e.getMessage());
        }
    }

    /**
     * Agenda uma nova defesa
     */
    @FXML
    private void agendarDefesa() {
        // Validações
        if (comboAluno.getValue() == null || comboAluno.getValue().isBlank()) {
            mostrarErro("Validação", "Por favor, selecione um aluno.");
            return;
        }

        if (datePickerData.getValue() == null) {
            mostrarErro("Validação", "Por favor, selecione uma data.");
            return;
        }

        String horario = txtHorario.getText().trim();
        if (horario.isEmpty()) {
            mostrarErro("Validação", "Por favor, informe o horário.");
            return;
        }

        if (!validarFormatoHorario(horario)) {
            mostrarErro("Validação", "Formato de horário inválido. Use o formato HH:MM (ex: 14:30).");
            return;
        }

        String sala = txtSala.getText().trim();
        if (sala.isEmpty()) {
            mostrarErro("Validação", "Por favor, informe a sala.");
            return;
        }

        // Verifica conflitos de horário/sala
        if (verificarConflito(datePickerData.getValue(), horario, sala, null)) {
            mostrarErro("Conflito", "Já existe um agendamento para este horário e sala na mesma data.");
            return;
        }

        // Busca o email do aluno selecionado
        String nomeAluno = comboAluno.getValue();
        String emailAluno = buscarEmailAlunoPorNome(nomeAluno);
        
        if (emailAluno == null) {
            mostrarErro("Erro", "Não foi possível encontrar o email do aluno selecionado.");
            return;
        }

        // Insere o agendamento no banco
        try (Connection conn = new Connector().getConnection()) {
            String sql = "INSERT INTO agendamento_defesa_tg (email_professor, email_aluno, data_defesa, horario, sala) " +
                        "VALUES (?, ?, ?, ?, ?)";
            
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, emailProfessorLogado);
            pst.setString(2, emailAluno);
            pst.setDate(3, java.sql.Date.valueOf(datePickerData.getValue()));
            pst.setString(4, horario);
            pst.setString(5, sala);
            
            pst.executeUpdate();

            mostrarSucesso("Sucesso", "Defesa agendada com sucesso!");
            limparFormulario();
            carregarAgendamentos();

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarErro("Erro", "Erro ao agendar defesa: " + e.getMessage());
        }
    }

    /**
     * Edita um agendamento existente
     */
    @FXML
    private void editarAgendamento() {
        if (agendamentoSelecionadoId == null) {
            mostrarErro("Validação", "Por favor, selecione um agendamento para editar.");
            return;
        }

        // Validações
        if (datePickerData.getValue() == null) {
            mostrarErro("Validação", "Por favor, selecione uma data.");
            return;
        }

        String horario = txtHorario.getText().trim();
        if (horario.isEmpty()) {
            mostrarErro("Validação", "Por favor, informe o horário.");
            return;
        }

        if (!validarFormatoHorario(horario)) {
            mostrarErro("Validação", "Formato de horário inválido. Use o formato HH:MM (ex: 14:30).");
            return;
        }

        String sala = txtSala.getText().trim();
        if (sala.isEmpty()) {
            mostrarErro("Validação", "Por favor, informe a sala.");
            return;
        }

        // Verifica conflitos (excluindo o próprio agendamento que está sendo editado)
        if (verificarConflito(datePickerData.getValue(), horario, sala, agendamentoSelecionadoId)) {
            mostrarErro("Conflito", "Já existe outro agendamento para este horário e sala na mesma data.");
            return;
        }

        // Atualiza o agendamento no banco
        try (Connection conn = new Connector().getConnection()) {
            String sql = "UPDATE agendamento_defesa_tg " +
                        "SET data_defesa = ?, horario = ?, sala = ? " +
                        "WHERE id = ? AND email_professor = ?";
            
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setDate(1, java.sql.Date.valueOf(datePickerData.getValue()));
            pst.setString(2, horario);
            pst.setString(3, sala);
            pst.setInt(4, agendamentoSelecionadoId);
            pst.setString(5, emailProfessorLogado);
            
            int rowsAffected = pst.executeUpdate();

            if (rowsAffected > 0) {
                mostrarSucesso("Sucesso", "Agendamento atualizado com sucesso!");
                limparFormulario();
                carregarAgendamentos();
            } else {
                mostrarErro("Erro", "Não foi possível atualizar o agendamento.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarErro("Erro", "Erro ao editar agendamento: " + e.getMessage());
        }
    }

    /**
     * Exclui um agendamento
     */
    @FXML
    private void excluirAgendamento() {
        if (agendamentoSelecionadoId == null) {
            mostrarErro("Validação", "Por favor, selecione um agendamento para excluir.");
            return;
        }

        // Confirmação
        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Confirmar Exclusão");
        confirmacao.setHeaderText("Deseja realmente excluir este agendamento?");
        confirmacao.setContentText("Esta ação não pode ser desfeita.");

        if (confirmacao.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try (Connection conn = new Connector().getConnection()) {
                String sql = "DELETE FROM agendamento_defesa_tg WHERE id = ? AND email_professor = ?";
                
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setInt(1, agendamentoSelecionadoId);
                pst.setString(2, emailProfessorLogado);
                
                int rowsAffected = pst.executeUpdate();

                if (rowsAffected > 0) {
                    mostrarSucesso("Sucesso", "Agendamento excluído com sucesso!");
                    limparFormulario();
                    carregarAgendamentos();
                } else {
                    mostrarErro("Erro", "Não foi possível excluir o agendamento.");
                }

            } catch (SQLException e) {
                e.printStackTrace();
                mostrarErro("Erro", "Erro ao excluir agendamento: " + e.getMessage());
            }
        }
    }

    /**
     * Limpa o formulário
     */
    @FXML
    private void limparFormulario() {
        comboAluno.setValue(null);
        datePickerData.setValue(null);
        txtHorario.clear();
        txtSala.clear();
        agendamentoSelecionadoId = null;
        tabelaAgendamentos.getSelectionModel().clearSelection();
        btnEditar.setDisable(true);
        btnExcluir.setDisable(true);
    }

    /**
     * Atualiza a tabela de agendamentos
     */
    @FXML
    private void atualizarTabela() {
        carregarAgendamentos();
    }

    /**
     * Volta para a tela anterior (dashboard)
     */

    /**
     * Verifica se há conflito de horário/sala para o professor
     * @param data Data da defesa
     * @param horario Horário da defesa
     * @param sala Sala da defesa
     * @param idExcluir ID do agendamento a excluir da verificação (para edição)
     * @return true se houver conflito, false caso contrário
     */
    private boolean verificarConflito(LocalDate data, String horario, String sala, Integer idExcluir) {
        try (Connection conn = new Connector().getConnection()) {
            String sql = "SELECT COUNT(*) as total " +
                        "FROM agendamento_defesa_tg " +
                        "WHERE email_professor = ? " +
                        "AND data_defesa = ? " +
                        "AND horario = ? " +
                        "AND sala = ?";
            
            if (idExcluir != null) {
                sql += " AND id != ?";
            }
            
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, emailProfessorLogado);
            pst.setDate(2, java.sql.Date.valueOf(data));
            pst.setString(3, horario);
            pst.setString(4, sala);
            
            if (idExcluir != null) {
                pst.setInt(5, idExcluir);
            }
            
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("total") > 0;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }

    /**
     * Valida o formato do horário (HH:MM)
     */
    private boolean validarFormatoHorario(String horario) {
        return horario.matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$");
    }

    /**
     * Busca o email do aluno pelo nome
     */
    private String buscarEmailAlunoPorNome(String nomeAluno) {
        try (Connection conn = new Connector().getConnection()) {
            String sql = "SELECT email FROM aluno WHERE nome = ? AND professor_tg = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, nomeAluno);
            pst.setString(2, emailProfessorLogado);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                return rs.getString("email");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Mostra mensagem de erro
     */
    private void mostrarErro(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(titulo);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    /**
     * Mostra mensagem de sucesso
     */
    private void mostrarSucesso(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sucesso");
        alert.setHeaderText(titulo);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    /**
     * Classe auxiliar para representar um agendamento na tabela
     */
    public static class AgendamentoInfo {
        private final Integer id;
        private final String nomeAluno;
        private final String data;
        private final String horario;
        private final String sala;

        public AgendamentoInfo(Integer id, String nomeAluno, String data, String horario, String sala) {
            this.id = id;
            this.nomeAluno = nomeAluno;
            this.data = data;
            this.horario = horario;
            this.sala = sala;
        }

        public Integer getId() {
            return id;
        }

        public String getNomeAluno() {
            return nomeAluno;
        }

        public String getData() {
            return data;
        }

        public String getHorario() {
            return horario;
        }

        public String getSala() {
            return sala;
        }
    }
}


package com.example.technocode.Controllers.Orientador;

import com.example.technocode.dao.Connector;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class TelaProfessorTGController {

    @FXML private Label lblTotalAlunos;
    @FXML private Label lblTotalOrientadores;
    @FXML private Label lblProjetosAprovados;
    @FXML private Label lblProjetosRevisar;

    @FXML private ComboBox<String> filtroCurso;
    @FXML private ComboBox<String> filtroOrientador;
    @FXML private ComboBox<String> filtroStatus;
    @FXML private TextField buscaTexto;

    @FXML private TableView<OrientadorResumo> tabelaOrientadores;
    @FXML private TableColumn<OrientadorResumo, String> colOrientadorNome;
    @FXML private TableColumn<OrientadorResumo, String> colOrientadorEmail;
    @FXML private TableColumn<OrientadorResumo, Integer> colQtdAlunos;

    @FXML private TableView<AlunoResumo> tabelaAlunos;
    @FXML private TableColumn<AlunoResumo, String> colAlunoNome;
    @FXML private TableColumn<AlunoResumo, String> colAlunoEmail;
    @FXML private TableColumn<AlunoResumo, String> colAlunoCurso;
    @FXML private TableColumn<AlunoResumo, String> colAlunoOrientador;
    @FXML private TableColumn<AlunoResumo, String> colAlunoAndamento;

    private final ObservableList<OrientadorResumo> orientadoresData = FXCollections.observableArrayList();
    private final ObservableList<AlunoResumo> alunosData = FXCollections.observableArrayList();

    private FilteredList<AlunoResumo> alunosFiltrados;
    private FilteredList<OrientadorResumo> orientadoresFiltrados;

    @FXML
    public void initialize() {
        configurarTabelas();
        configurarFiltros();
        carregarCombosFiltro();
        carregarDashboards();
        carregarTabelas();
    }

    private void configurarTabelas() {
        colOrientadorNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colOrientadorEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colQtdAlunos.setCellValueFactory(new PropertyValueFactory<>("qtdAlunos"));

        colAlunoNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colAlunoEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colAlunoCurso.setCellValueFactory(new PropertyValueFactory<>("curso"));
        colAlunoOrientador.setCellValueFactory(new PropertyValueFactory<>("orientadorNome"));
        colAlunoAndamento.setCellValueFactory(new PropertyValueFactory<>("andamento"));

        alunosFiltrados = new FilteredList<>(alunosData, a -> true);
        orientadoresFiltrados = new FilteredList<>(orientadoresData, o -> true);

        SortedList<AlunoResumo> alunosOrdenados = new SortedList<>(alunosFiltrados);
        alunosOrdenados.comparatorProperty().bind(tabelaAlunos.comparatorProperty());
        tabelaAlunos.setItems(alunosOrdenados);

        SortedList<OrientadorResumo> orientadoresOrdenados = new SortedList<>(orientadoresFiltrados);
        orientadoresOrdenados.comparatorProperty().bind(tabelaOrientadores.comparatorProperty());
        tabelaOrientadores.setItems(orientadoresOrdenados);

        tabelaOrientadores.setStyle("-fx-control-inner-background: #ffffff; -fx-text-background-color: black;");
        tabelaAlunos.setStyle("-fx-control-inner-background: #ffffff; -fx-text-background-color: black;");
    }

    private void configurarFiltros() {
        filtroStatus.setItems(FXCollections.observableArrayList("Todos", "Aprovado", "Revisar", "Sem feedback"));
        filtroStatus.getSelectionModel().selectFirst();

        // Atualiza filtros dinamicamente
        Runnable aplicar = this::aplicarFiltros;
        filtroCurso.valueProperty().addListener((obs, o, n) -> aplicar.run());
        filtroOrientador.valueProperty().addListener((obs, o, n) -> aplicar.run());
        filtroStatus.valueProperty().addListener((obs, o, n) -> aplicar.run());
        buscaTexto.textProperty().addListener((obs, o, n) -> aplicar.run());

        // Doble click em orientador -> filtra alunos por ele
        tabelaOrientadores.setRowFactory(tv -> {
            TableRow<OrientadorResumo> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    OrientadorResumo or = row.getItem();
                    filtroOrientador.getSelectionModel().select(or.getNome());
                }
            });
            return row;
        });
    }

    private void carregarCombosFiltro() {
        try (Connection c = new Connector().getConnection()) {
            // Cursos
            List<String> cursos = new ArrayList<>();
            try (PreparedStatement ps = c.prepareStatement("SELECT DISTINCT curso FROM aluno WHERE curso IS NOT NULL AND curso <> '' ORDER BY curso");
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) cursos.add(rs.getString(1));
            }
            cursos.add(0, "Todos");
            filtroCurso.setItems(FXCollections.observableArrayList(cursos));
            filtroCurso.getSelectionModel().selectFirst();

            // Orientadores por nome
            List<String> orientadores = new ArrayList<>();
            try (PreparedStatement ps = c.prepareStatement("SELECT nome FROM orientador ORDER BY nome");
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) orientadores.add(rs.getString(1));
            }
            orientadores.add(0, "Todos");
            filtroOrientador.setItems(FXCollections.observableArrayList(orientadores));
            filtroOrientador.getSelectionModel().selectFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void carregarDashboards() {
        try (Connection c = new Connector().getConnection()) {
            // Totais simples
            lblTotalAlunos.setText(String.valueOf(contar(c, "SELECT COUNT(*) FROM aluno")));
            lblTotalOrientadores.setText(String.valueOf(contar(c, "SELECT COUNT(*) FROM orientador")));

            // Projetos aprovados/revisar com base nos feedbacks mais recentes
            int aprovados = contar(c, "SELECT COUNT(*) FROM (\n" +
                    "  SELECT a.email,\n" +
                    "         MAX(CASE WHEN fa.status_nome='Revisar' OR fa.status_idade='Revisar' OR fa.status_curso='Revisar' OR fa.status_motivacao='Revisar' OR fa.status_historico='Revisar' OR fa.status_github='Revisar' OR fa.status_linkedin='Revisar' OR fa.status_conhecimentos='Revisar' THEN 1 ELSE 0 END) tem_revisar_apr,\n" +
                    "         MAX(CASE WHEN fapi.status_problema='Revisar' OR fapi.status_solucao='Revisar' OR fapi.status_tecnologias='Revisar' OR fapi.status_contribuicoes='Revisar' OR fapi.status_hard_skills='Revisar' OR fapi.status_soft_skills='Revisar' THEN 1 ELSE 0 END) tem_revisar_api\n" +
                    "  FROM aluno a\n" +
                    "  LEFT JOIN feedback_apresentacao fa ON fa.aluno = a.email\n" +
                    "  LEFT JOIN feedback_api fapi ON fapi.aluno = a.email\n" +
                    "  GROUP BY a.email\n" +
                    ") t WHERE COALESCE(tem_revisar_apr,0)=0 AND COALESCE(tem_revisar_api,0)=0");

            int revisar = contar(c, "SELECT COUNT(*) FROM (\n" +
                    "  SELECT a.email,\n" +
                    "         MAX(CASE WHEN fa.status_nome='Revisar' OR fa.status_idade='Revisar' OR fa.status_curso='Revisar' OR fa.status_motivacao='Revisar' OR fa.status_historico='Revisar' OR fa.status_github='Revisar' OR fa.status_linkedin='Revisar' OR fa.status_conhecimentos='Revisar' THEN 1 ELSE 0 END) tem_revisar_apr,\n" +
                    "         MAX(CASE WHEN fapi.status_problema='Revisar' OR fapi.status_solucao='Revisar' OR fapi.status_tecnologias='Revisar' OR fapi.status_contribuicoes='Revisar' OR fapi.status_hard_skills='Revisar' OR fapi.status_soft_skills='Revisar' THEN 1 ELSE 0 END) tem_revisar_api\n" +
                    "  FROM aluno a\n" +
                    "  LEFT JOIN feedback_apresentacao fa ON fa.aluno = a.email\n" +
                    "  LEFT JOIN feedback_api fapi ON fapi.aluno = a.email\n" +
                    "  GROUP BY a.email\n" +
                    ") t WHERE COALESCE(tem_revisar_apr,0)=1 OR COALESCE(tem_revisar_api,0)=1");

            lblProjetosAprovados.setText(String.valueOf(aprovados));
            lblProjetosRevisar.setText(String.valueOf(revisar));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int contar(Connection c, String sql) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
            return 0;
        }
    }

    private void carregarTabelas() {
        orientadoresData.clear();
        alunosData.clear();

        try (Connection c = new Connector().getConnection()) {
            // Orientadores com número de alunos
            String sqlOrient = "SELECT o.nome, o.email, COUNT(a.email) qtd FROM orientador o LEFT JOIN aluno a ON a.orientador = o.email GROUP BY o.email, o.nome ORDER BY o.nome";
            try (PreparedStatement ps = c.prepareStatement(sqlOrient); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    orientadoresData.add(new OrientadorResumo(
                            rs.getString("nome"),
                            rs.getString("email"),
                            rs.getInt("qtd")
                    ));
                }
            }

            // Alunos com orientador, curso e andamento baseado em feedbacks
            String sqlAlunos = "SELECT a.nome, a.email, a.curso, o.nome orientador_nome, o.email orientador_email FROM aluno a LEFT JOIN orientador o ON o.email = a.orientador ORDER BY a.nome";
            try (PreparedStatement ps = c.prepareStatement(sqlAlunos); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String emailAluno = rs.getString("email");
                    String andamento = calcularAndamentoAluno(c, emailAluno);
                    alunosData.add(new AlunoResumo(
                            rs.getString("nome"),
                            emailAluno,
                            rs.getString("curso"),
                            rs.getString("orientador_nome"),
                            rs.getString("orientador_email"),
                            andamento
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        aplicarFiltros();
    }

    private String calcularAndamentoAluno(Connection c, String emailAluno) {
        boolean temRevisarApresentacao = false;
        boolean temRevisarApi = false;
        boolean temFeedback = false;

        try (PreparedStatement ps = c.prepareStatement(
                "SELECT\n" +
                "  MAX(CASE WHEN status_nome='Revisar' OR status_idade='Revisar' OR status_curso='Revisar' OR status_motivacao='Revisar' OR status_historico='Revisar' OR status_github='Revisar' OR status_linkedin='Revisar' OR status_conhecimentos='Revisar' THEN 1 ELSE 0 END) tem_revisar,\n" +
                "  COUNT(*) qtd\n" +
                "FROM feedback_apresentacao WHERE aluno = ?")) {
            ps.setString(1, emailAluno);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    temRevisarApresentacao = rs.getInt("tem_revisar") == 1;
                    temFeedback = rs.getInt("qtd") > 0 || temFeedback;
                }
            }
        } catch (SQLException ignored) {}

        try (PreparedStatement ps = c.prepareStatement(
                "SELECT\n" +
                "  MAX(CASE WHEN status_problema='Revisar' OR status_solucao='Revisar' OR status_tecnologias='Revisar' OR status_contribuicoes='Revisar' OR status_hard_skills='Revisar' OR status_soft_skills='Revisar' THEN 1 ELSE 0 END) tem_revisar,\n" +
                "  COUNT(*) qtd\n" +
                "FROM feedback_api WHERE aluno = ?")) {
            ps.setString(1, emailAluno);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    temRevisarApi = rs.getInt("tem_revisar") == 1;
                    temFeedback = rs.getInt("qtd") > 0 || temFeedback;
                }
            }
        } catch (SQLException ignored) {}

        if (!temFeedback) return "Sem feedback";
        if (temRevisarApresentacao || temRevisarApi) return "Revisar";
        return "Aprovado";
    }

    private void aplicarFiltros() {
        final String cursoSel = valorOuNulo(filtroCurso);
        final String orientadorSel = valorOuNulo(filtroOrientador);
        final String statusSel = valorOuNulo(filtroStatus);
        final String busca = buscaTexto.getText() == null ? "" : buscaTexto.getText().toLowerCase(Locale.ROOT).trim();

        alunosFiltrados.setPredicate(a -> {
            if (cursoSel != null && !Objects.equals(cursoSel, "Todos") && (a.getCurso() == null || !a.getCurso().equalsIgnoreCase(cursoSel)))
                return false;
            if (orientadorSel != null && !Objects.equals(orientadorSel, "Todos") && (a.getOrientadorNome() == null || !a.getOrientadorNome().equalsIgnoreCase(orientadorSel)))
                return false;
            if (statusSel != null && !Objects.equals(statusSel, "Todos") && (a.getAndamento() == null || !a.getAndamento().equalsIgnoreCase(statusSel)))
                return false;

            if (!busca.isEmpty()) {
                String texto = (a.getNome() + " " + a.getEmail() + " " + Objects.toString(a.getCurso(), "") + " " + Objects.toString(a.getOrientadorNome(), "")).toLowerCase(Locale.ROOT);
                return texto.contains(busca);
            }
            return true;
        });

        orientadoresFiltrados.setPredicate(o -> {
            if (!busca.isEmpty()) {
                String texto = (o.getNome() + " " + o.getEmail()).toLowerCase(Locale.ROOT);
                return texto.contains(busca);
            }
            return true;
        });
    }

    private String valorOuNulo(ComboBox<String> cb) {
        return cb == null ? null : cb.getSelectionModel().getSelectedItem();
    }

    @FXML
    private void acaoAtualizar(ActionEvent e) {
        carregarDashboards();
        carregarTabelas();
    }

    @FXML
    private void acaoVoltar(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/technocode/login.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public static class OrientadorResumo {
        private final SimpleStringProperty nome = new SimpleStringProperty();
        private final SimpleStringProperty email = new SimpleStringProperty();
        private final SimpleIntegerProperty qtdAlunos = new SimpleIntegerProperty();

        public OrientadorResumo(String nome, String email, int qtdAlunos) {
            this.nome.set(nome);
            this.email.set(email);
            this.qtdAlunos.set(qtdAlunos);
        }
        public String getNome() { return nome.get(); }
        public String getEmail() { return email.get(); }
        public int getQtdAlunos() { return qtdAlunos.get(); }
    }

    public static class AlunoResumo {
        private final SimpleStringProperty nome = new SimpleStringProperty();
        private final SimpleStringProperty email = new SimpleStringProperty();
        private final SimpleStringProperty curso = new SimpleStringProperty();
        private final SimpleStringProperty orientadorNome = new SimpleStringProperty();
        private final SimpleStringProperty orientadorEmail = new SimpleStringProperty();
        private final SimpleStringProperty andamento = new SimpleStringProperty();

        public AlunoResumo(String nome, String email, String curso, String orientadorNome, String orientadorEmail, String andamento) {
            this.nome.set(nome);
            this.email.set(email);
            this.curso.set(curso);
            this.orientadorNome.set(orientadorNome);
            this.orientadorEmail.set(orientadorEmail);
            this.andamento.set(andamento);
        }

        public String getNome() { return nome.get(); }
        public String getEmail() { return email.get(); }
        public String getCurso() { return curso.get(); }
        public String getOrientadorNome() { return orientadorNome.get(); }
        public String getOrientadorEmail() { return orientadorEmail.get(); }
        public String getAndamento() { return andamento.get(); }
    }
}



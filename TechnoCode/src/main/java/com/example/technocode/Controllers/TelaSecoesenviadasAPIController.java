package com.example.technocode.Controllers;


import com.example.technocode.Objetos.Seção;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TelaSecoesenviadasAPIController {

    private Map<String, Boolean> validacoes = new HashMap<>();

    @FXML private CheckBox validarSemestreCurso, validarAno, validarSemestre,
            validarEmpresa, validarProblema, validarSolucao, validarRepositorio,
            validarTecnologias, validarCP, validarHS, validarSS;

    @FXML private TextArea alunoTextSemestreCurso, alunoTextAno, alunoTextSemestre,
            alunoTextEmpresa, alunoTextProblema, alunoTextSolucao, alunoTextRepositorio,
            alunoTextTecnologias, alunoTextCP, alunoTextHS, alunoTextSS;


    @FXML private TextArea feedbackTextSemestreCurso, feedbackTextAno, feedbackTextSemestre,
            feedbackTextEmpresa, feedbackTextProblema, feedbackTextSolucao, feedbackTextRepositorio,
            feedbackTextTecnologias, feedbackTextCP, feedbackTextHS, feedbackTextSS;

    @FXML private CheckBox feedbackSemestreCurso, feedbackAno, feedbackSemestre, feedbackEmpresa,
            feedbackProblema, feedbackSolucao, feedbackRepositorio, feedbackTecnologias, feedbackCP,
            feedbackHS, feedbackSS;

    @FXML
    public void initialize() {
        // Inicializa todas as TextAreas como invisíveis
        feedbackTextSemestreCurso.setVisible(false);
        feedbackTextAno.setVisible(false);
        feedbackTextSemestre.setVisible(false);
        feedbackTextEmpresa.setVisible(false);
        feedbackTextProblema.setVisible(false);
        feedbackTextSolucao.setVisible(false);
        feedbackTextRepositorio.setVisible(false);
        feedbackTextTecnologias.setVisible(false);
        feedbackTextCP.setVisible(false);
        feedbackTextHS.setVisible(false);
        feedbackTextSS.setVisible(false);
    }

    @FXML
    private void voltarTelaOrientador(ActionEvent event) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/technocode/tela-entregasDoAluno.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Erro ao voltar para tela do orientador: " + e.getMessage());
            throw e;
        }
    }

    @FXML
    public void feedbackSecao(ActionEvent event) {
        CheckBox checkBox = (CheckBox) event.getSource();
        TextArea feedbackArea = null;


        // Determina qual TextArea deve ser mostrada/escondida
        if (checkBox == feedbackSemestreCurso) {
            feedbackArea = feedbackTextSemestreCurso;
        } else if (checkBox == feedbackAno) {
            feedbackArea = feedbackTextAno;
        } else if (checkBox == feedbackSemestre) {
            feedbackArea = feedbackTextSemestre;
        } else if (checkBox == feedbackEmpresa) {
            feedbackArea = feedbackTextEmpresa;
        } else if (checkBox == feedbackProblema) {
            feedbackArea = feedbackTextProblema;
        } else if (checkBox == feedbackSolucao) {
            feedbackArea = feedbackTextSolucao;
        } else if (checkBox == feedbackRepositorio) {
            feedbackArea = feedbackTextRepositorio;
        } else if (checkBox == feedbackTecnologias) {
            feedbackArea = feedbackTextTecnologias;
        } else if (checkBox == feedbackCP) {
            feedbackArea = feedbackTextCP;
        } else if (checkBox == feedbackHS) {
            feedbackArea = feedbackTextSS;
        } else if (checkBox == feedbackSS) {
            feedbackArea = feedbackTextSS;
        }

        // Mostra ou esconde a área de feedback e limpa o texto quando esconde
        if (feedbackArea != null) {
            feedbackArea.setVisible(checkBox.isSelected());
            if (!checkBox.isSelected()) {
                feedbackArea.clear(); // Limpa o texto quando esconde
            } else {
                // Configura um texto padrão ou placeholder quando mostra
                feedbackArea.setPromptText("Digite seu feedback aqui...");

                // Ajusta o tamanho para um campo menor
                feedbackArea.setPrefHeight(100); // Altura menor
                feedbackArea.setWrapText(true); // Permite quebra de linha automática
            }
        }
    }

    @FXML
    public void enviarFeedback(ActionEvent event) {
        // Coleta todos os feedbacks preenchidos
        Map<String, String> feedbacks = new HashMap<>();

        if (feedbackSemestreCurso.isSelected() && !feedbackTextSemestreCurso.getText().isEmpty()) {
            feedbacks.put("Semestre do Curso ", feedbackTextSemestreCurso.getText());
        }
        if (feedbackAno.isSelected() && !feedbackTextAno.getText().isEmpty()) {
            feedbacks.put("Ano do projeto", feedbackTextAno.getText());
        }
        if (feedbackSemestre.isSelected() && !feedbackTextSemestre.getText().isEmpty()) {
            feedbacks.put("Semestre do ano (1,2)", feedbackTextSemestre.getText());
        }
        if (feedbackEmpresa.isSelected() && !feedbackTextEmpresa.getText().isEmpty()) {
            feedbacks.put("Empresa parceira", feedbackTextEmpresa.getText());
        }
        if (feedbackProblema.isSelected() && !feedbackTextProblema.getText().isEmpty()) {
            feedbacks.put("Problema", feedbackTextProblema.getText());
        }
        if (feedbackSolucao.isSelected() && !feedbackTextSolucao.getText().isEmpty()) {
            feedbacks.put("Solução", feedbackTextSolucao.getText());
        }
        if (feedbackRepositorio.isSelected() && !feedbackTextRepositorio.getText().isEmpty()) {
            feedbacks.put("Link Repositório", feedbackTextRepositorio.getText());
        }
        if (feedbackTecnologias.isSelected() && !feedbackTextTecnologias.getText().isEmpty()) {
            feedbacks.put("Tecnologias Utilizadas", feedbackTextTecnologias.getText());
        }
        if (feedbackCP.isSelected() && !feedbackTextCP.getText().isEmpty()) {
            feedbacks.put("Contribuições pessoais", feedbackTextCP.getText());
        }
        if (feedbackHS.isSelected() && !feedbackTextHS.getText().isEmpty()) {
            feedbacks.put("Hard Skills", feedbackTextHS.getText());
        }
        if (feedbackSS.isSelected() && !feedbackTextSS.getText().isEmpty()) {
            feedbacks.put("Soft Skills", feedbackTextSS.getText());
        }

        // Aqui você implementa a lógica para salvar os feedbacks
        for (Map.Entry<String, String> feedback : feedbacks.entrySet()) {
            System.out.println("Feedback para " + feedback.getKey() + ": " + feedback.getValue());
        }

        // Mostra mensagem de confirmação
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sucesso");
        alert.setHeaderText(null);
        alert.setContentText("Feedbacks enviados com sucesso!");
        alert.showAndWait();
    }


    @FXML
    public void validarSecao(ActionEvent event) {
        CheckBox checkBox = (CheckBox) event.getSource();
        boolean isValidado = checkBox.isSelected();

        String secaoValidada = "";
        String mensagemAdicional = "";

        // Identifica qual seção está sendo validada
        if (checkBox == validarSemestreCurso) {
            secaoValidada = "Semsetre do Curso";
            validacoes.put("Semestre do Curso", isValidado);
            mensagemAdicional = isValidado ? "Nome do aluno foi aprovado" : "Semestre do Curso precisa de revisão";
        } else if (checkBox == validarAno) {
            secaoValidada = "Ano do Projeto";
            validacoes.put("Ano do Projeto", isValidado);
            mensagemAdicional = isValidado ? "Idade foi confirmada" : "Ano do Projeto precisa ser verificada";
        } else if (checkBox == validarSemestre) {
            secaoValidada = "Semestre do Ano (1,2)";
            validacoes.put("Semestre do Ano (1,2)", isValidado);
            mensagemAdicional = isValidado ? "Informações do curso foram confirmadas" : "Semestre do Ano (1,2) precisam ser revisadas";
        } else if (checkBox == validarEmpresa) {
            secaoValidada = "Empresa Parceira";
            validacoes.put("Empresa Parceira", isValidado);
            mensagemAdicional = isValidado ? "Motivação foi aprovada" : "Empresa Parceira precisa ser melhorada";
        } else if (checkBox == validarProblema) {
            secaoValidada = "Problema do Projeto";
            validacoes.put("Problema do Projeto", isValidado);
            mensagemAdicional = isValidado ? "Histórico foi aprovado" : "Problema do Projeto precisa ser complementado";
        } else if (checkBox == validarSolucao) {
            secaoValidada = "Solução para o Problema";
            validacoes.put("Solução para o Problema", isValidado);
            mensagemAdicional = isValidado ? "Perfil do GitHub foi aprovado" : "Solução para o Problema precisa ser atualizado";
        } else if (checkBox == validarRepositorio) {
            secaoValidada = "Link do Repositorio";
            validacoes.put("Link do Repositorio", isValidado);
            mensagemAdicional = isValidado ? "Perfil do LinkedIn foi aprovado" : "Link do Repositorio precisa ser atualizado";
        } else if (checkBox == validarTecnologias) {
            secaoValidada = "Tecnologias Utilizadas";
            validacoes.put("Tecnologias Utilizadas", isValidado);
            mensagemAdicional = isValidado ? "Conhecimentos foram aprovados" : "Tecnologias Utilizadas precisam ser revisados";
        } else if (checkBox == validarCP) {
            secaoValidada = "Contribuições Pessoais";
            validacoes.put("Contribuições Pessoais", isValidado);
            mensagemAdicional = isValidado ? "Perfil do GitHub foi aprovado" : "Contribuições Pessoais precisa ser atualizado";
        } else if (checkBox == validarHS) {
            secaoValidada = "Hard Skills";
            validacoes.put("Hard Skills", isValidado);
            mensagemAdicional = isValidado ? "Perfil do LinkedIn foi aprovado" : "Hard Skills precisa ser atualizado";
        } else if (checkBox == validarSS) {
            secaoValidada = "Soft Skills";
            validacoes.put("Soft Skills", isValidado);
            mensagemAdicional = isValidado ? "Conhecimentos foram aprovados" : "Soft Skills precisam ser revisados";

            // Exibe mensagem de confirmação com detalhes
            if (!secaoValidada.isEmpty()) {
                String status = isValidado ? "validada" : "invalidada";
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Status da Validação");
                alert.setHeaderText("Seção: " + secaoValidada);
                alert.setContentText(mensagemAdicional);
                alert.showAndWait();

                // Atualiza o status da seção no banco de dados ou sistema
                atualizarStatusSecao(secaoValidada, isValidado);
            }
        }
    }

        private void atualizarStatusSecao (String secao,boolean status){
            try {
                // TODO: Implementar a lógica de atualização no banco de dados
                System.out.println("Atualizando status da seção " + secao + ": " + status);

                // Aqui você pode adicionar a lógica para salvar no banco de dados
                // Exemplo de como poderia ser:
                // String sql = "UPDATE secao_apresentacao SET status = ? WHERE secao = ? AND aluno = ?";
                // PreparedStatement pst = connection.prepareStatement(sql);
                // pst.setBoolean(1, status);
                // pst.setString(2, secao);
                // pst.setString(3, alunoAtual);
                // pst.executeUpdate();

            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erro");
                alert.setHeaderText("Erro ao atualizar status");
                alert.setContentText("Não foi possível atualizar o status da seção no banco de dados.");
                alert.showAndWait();
                e.printStackTrace();
            }
        }

        // Método para verificar se todas as seções foram validadas
        public boolean todasSecoesValidadas () {
            for (Boolean validado : validacoes.values()) {
                if (!validado) {
                    return false;
                }
            }
            return !validacoes.isEmpty();
        }



        public void setDadosDoAluno (Map < String, String > dados){
            if (dados != null) {
                alunoTextSemestreCurso.setText(dados.getOrDefault("Semestre do Curso", ""));
                alunoTextAno.setText(dados.getOrDefault("Ano do projeto", ""));
                alunoTextSemestre.setText(dados.getOrDefault("Semestre do ano (1,2)", ""));
                alunoTextEmpresa.setText(dados.getOrDefault("Empresa Parceira", ""));
                alunoTextProblema.setText(dados.getOrDefault("Problema do Projeto", ""));
                alunoTextSolucao.setText(dados.getOrDefault("Solução do Projeto", ""));
                alunoTextRepositorio.setText(dados.getOrDefault("Link do Repositorio", ""));
                alunoTextTecnologias.setText(dados.getOrDefault("Tecnologias Utilizadas", ""));
                alunoTextCP.setText(dados.getOrDefault("Contribuições Pessoais", ""));
                alunoTextHS.setText(dados.getOrDefault("Hard Skills", ""));
                alunoTextSS.setText(dados.getOrDefault("Soft Skills", ""));
            }
        }
    }
DROP DATABASE IF EXISTS technotg;

CREATE DATABASE technotg;

DROP USER IF EXISTS 'technocode'@'localhost';

CREATE USER 'technocode'@'localhost' IDENTIFIED BY 'pass123';

GRANT SELECT, INSERT, DELETE, UPDATE ON technotg.* TO 'technocode'@'localhost';

USE technotg;

CREATE TABLE orientador(
    email VARCHAR(120) PRIMARY KEY NOT NULL,
    nome VARCHAR(250) NOT NULL,
    senha VARCHAR(100) NOT NULL
);

CREATE TABLE professor_tg(
    email VARCHAR(120) PRIMARY KEY NOT NULL,
    nome VARCHAR(250) NOT NULL,
    senha VARCHAR(100) NOT NULL,
    disciplina VARCHAR(10) NOT NULL
);

CREATE TABLE aluno(
    email VARCHAR(120) PRIMARY KEY NOT NULL,
    nome VARCHAR(250) NOT NULL,
    senha VARCHAR(100) NOT NULL,
    orientador VARCHAR(120),
    professor_tg VARCHAR(120) NOT NULL,
    disciplina_tg ENUM('TG1', 'TG2', 'TG1/TG2') NOT NULL,
    FOREIGN KEY (orientador) REFERENCES orientador (email),
    FOREIGN KEY (professor_tg) REFERENCES professor_tg (email)
);

CREATE TABLE solicitacao_orientacao (
    id INT AUTO_INCREMENT PRIMARY KEY,
    aluno VARCHAR(120) NOT NULL,
    orientador VARCHAR(120) NOT NULL,
    status ENUM('Pendente', 'Aceita', 'Recusada') NOT NULL DEFAULT 'Pendente',
    mensagem_orientador TEXT,
    data_solicitacao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_resposta DATETIME,
    FOREIGN KEY (aluno) REFERENCES aluno(email),
    FOREIGN KEY (orientador) REFERENCES orientador(email)
);

CREATE TABLE secao_api(
    aluno VARCHAR(120) NOT NULL,
    semestre_curso VARCHAR(18) NOT NULL,
    ano YEAR NOT NULL,
    semestre_ano ENUM('1','2') NOT NULL,
    versao INT NOT NULL,
    empresa VARCHAR(120) NOT NULL,
    descricao_empresa TEXT,
    problema TEXT NOT NULL,
    solucao TEXT NOT NULL,
    link_repositorio VARCHAR(255) NOT NULL,
    tecnologias TEXT NOT NULL,
    contribuicoes TEXT NOT NULL,
    hard_skills TEXT NOT NULL,
    soft_skills TEXT NOT NULL,
    horario_secao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status_empresa ENUM('Aprovado', 'Revisar') DEFAULT NULL,
    feedback_empresa TEXT,
    status_descricao_empresa ENUM('Aprovado', 'Revisar') DEFAULT NULL,
    feedback_descricao_empresa TEXT,
    status_problema ENUM('Aprovado', 'Revisar') DEFAULT NULL,
    feedback_problema TEXT,
    status_solucao ENUM('Aprovado', 'Revisar') DEFAULT NULL,
    feedback_solucao TEXT,
    status_repositorio ENUM('Aprovado', 'Revisar') DEFAULT NULL,
    feedback_repositorio TEXT,
    status_tecnologias ENUM('Aprovado', 'Revisar') DEFAULT NULL,
    feedback_tecnologias TEXT,
    status_contribuicoes ENUM('Aprovado', 'Revisar') DEFAULT NULL,
    feedback_contribuicoes TEXT,
    status_hard_skills ENUM('Aprovado', 'Revisar') DEFAULT NULL,
    feedback_hard_skills TEXT,
    status_soft_skills ENUM('Aprovado', 'Revisar') DEFAULT NULL,
    feedback_soft_skills TEXT,
    horario_feedback DATETIME,
    PRIMARY KEY (aluno, semestre_curso, ano, semestre_ano, versao),
    FOREIGN KEY (aluno) REFERENCES aluno(email)
);

CREATE TABLE secao_apresentacao(
    aluno VARCHAR(120) NOT NULL,
    nome VARCHAR(255) NOT NULL,
    idade DATE NOT NULL,
    curso VARCHAR(50) NOT NULL,
    versao INT NOT NULL,
    motivacao TEXT NOT NULL,
    historico TEXT NOT NULL,
    link_github VARCHAR(255) NOT NULL,
    link_linkedin VARCHAR(255) NOT NULL,
    principais_conhecimentos TEXT NOT NULL,
    historico_profissional TEXT NOT NULL,
    horario_secao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status_nome ENUM('Aprovado', 'Revisar') DEFAULT NULL,
    feedback_nome TEXT,
    status_idade ENUM('Aprovado', 'Revisar') DEFAULT NULL,
    feedback_idade TEXT,
    status_curso ENUM('Aprovado', 'Revisar') DEFAULT NULL,
    feedback_curso TEXT,
    status_motivacao ENUM('Aprovado', 'Revisar') DEFAULT NULL,
    feedback_motivacao TEXT,
    status_historico ENUM('Aprovado', 'Revisar') DEFAULT NULL,
    feedback_historico TEXT,
    status_github ENUM('Aprovado', 'Revisar') DEFAULT NULL,
    feedback_github TEXT,
    status_linkedin ENUM('Aprovado', 'Revisar') DEFAULT NULL,
    feedback_linkedin TEXT,
    status_conhecimentos ENUM('Aprovado', 'Revisar') DEFAULT NULL,
    feedback_conhecimentos TEXT,
    status_historico_profissional ENUM('Aprovado', 'Revisar') DEFAULT NULL,
    feedback_historico_profissional TEXT,
    horario_feedback DATETIME,
    PRIMARY KEY(aluno, versao),
    FOREIGN KEY (aluno) REFERENCES aluno(email)
);

CREATE TABLE agendamento_defesa_tg (
    email_professor VARCHAR(120) NOT NULL,
    email_aluno VARCHAR(120) NOT NULL,
    data_defesa DATE NOT NULL,
    horario TIME NOT NULL,
    sala VARCHAR(50) NOT NULL,
    FOREIGN KEY (email_professor) REFERENCES professor_tg(email)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    FOREIGN KEY (email_aluno) REFERENCES aluno(email)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    PRIMARY KEY (data_defesa, horario)
);


DROP DATABASE IF EXISTS technotg;
CREATE DATABASE technotg;

DROP USER IF EXISTS 'technocode'@'localhost';
create user 'technocode'@'localhost' identified by 'pass123';

grant select, insert, delete, update on technotg.* to 'technocode'@'localhost';

USE technotg;

CREATE TABLE orientador(
		email VARCHAR(120) PRIMARY KEY NOT NULL,
        nome VARCHAR(250) NOT NULL,
        senha VARCHAR(100) NOT NULL
);

CREATE TABLE aluno(
		email VARCHAR(120) PRIMARY KEY NOT NULL,
        nome VARCHAR(250) NOT NULL,
        senha VARCHAR(100) NOT NULL,
        orientador VARCHAR(250),
        curso VARCHAR(10),
        FOREIGN KEY (orientador) REFERENCES orientador (email)
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
        link_repositorio varchar(255) NOT NULL,
        tecnologias TEXT NOT NULL,
        contribuicoes TEXT NOT NULL,
        hard_skills TEXT NOT NULL,
        soft_skills TEXT NOT NULL,
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
        PRIMARY KEY(aluno, versao),
        FOREIGN KEY (aluno) REFERENCES aluno(email)
);

CREATE TABLE feedback_apresentacao(
		id INT AUTO_INCREMENT PRIMARY KEY,
        horario DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, 
        
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
        
        aluno VARCHAR(120) NOT NULL,
        versao INT NOT NULL,
        FOREIGN KEY (aluno, versao) REFERENCES secao_apresentacao(aluno, versao)
);

CREATE TABLE feedback_api (
    id INT AUTO_INCREMENT PRIMARY KEY,
    horario DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    status_problema ENUM('Aprovado', 'Revisar') DEFAULT NULL,
    feedback_problema TEXT,
    
    status_solucao ENUM('Aprovado', 'Revisar') DEFAULT NULL,
    feedback_solucao TEXT,
    
    status_tecnologias ENUM('Aprovado', 'Revisar') DEFAULT NULL,
    feedback_tecnologias TEXT,
    
    status_contribuicoes ENUM('Aprovado', 'Revisar') DEFAULT NULL,
    feedback_contribuicoes TEXT,
    
    status_hard_skills ENUM('Aprovado', 'Revisar') DEFAULT NULL,
    feedback_hard_skills TEXT,
    
    status_soft_skills ENUM('Aprovado', 'Revisar') DEFAULT NULL,
    feedback_soft_skills TEXT,

    aluno VARCHAR(250) NOT NULL,
    semestre_curso VARCHAR(18) NOT NULL,
    ano YEAR NOT NULL,
    semestre_ano ENUM('1', '2') NOT NULL,
    versao INT NOT NULL,
    
    FOREIGN KEY (aluno, semestre_curso, ano, semestre_ano, versao)
        REFERENCES secao_api(aluno, semestre_curso, ano, semestre_ano, versao)
);

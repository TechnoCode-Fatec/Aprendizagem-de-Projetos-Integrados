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
        FOREIGN KEY (orientador) REFERENCES orientador (email)
);

CREATE TABLE secao_api(
		aluno VARCHAR(250) NOT NULL,
		semestre_curso VARCHAR(18) NOT NULL,
        ano YEAR NOT NULL,
        semestre_ano ENUM('1','2') NOT NULL,
        versao INT NOT NULL,
		empresa TEXT NOT NULL,
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
        idade TINYINT NOT NULL,
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
		titulo VARCHAR(150) NOT NULL,
        horario DATETIME NOT NULL, 
        conteudo TEXT NOT NULL,
        aluno VARCHAR(120) NOT NULL,
        versao INT NOT NULL,
        PRIMARY KEY(titulo, horario, aluno, versao),
        FOREIGN KEY (aluno, versao) REFERENCES secao_apresentacao(aluno, versao)
);

CREATE TABLE feedback_api(
		titulo VARCHAR(150) NOT NULL,
        horario DATETIME NOT NULL, 
        conteudo TEXT NOT NULL,
		aluno VARCHAR(250) NOT NULL,
		semestre_curso VARCHAR(18) NOT NULL,
        ano YEAR NOT NULL,
        semestre_ano ENUM('1','2') NOT NULL,
        versao INT NOT NULL,
        PRIMARY KEY (titulo, horario, aluno, semestre_curso, ano, semestre_ano, versao),
        FOREIGN KEY (aluno, semestre_curso, ano, semestre_ano, versao) REFERENCES secao_api(aluno, semestre_curso, ano, semestre_ano, versao)
);

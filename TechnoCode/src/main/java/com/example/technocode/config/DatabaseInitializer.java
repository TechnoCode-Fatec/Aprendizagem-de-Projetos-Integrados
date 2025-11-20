package com.example.technocode.config;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Classe responsável por inicializar o banco de dados executando o script SQL
 * de criação do schema na primeira execução da aplicação.
 */
public class DatabaseInitializer {
    
    private static final Logger logger = Logger.getLogger(DatabaseInitializer.class.getName());
    private static final String ADMIN_URL = "jdbc:mysql://localhost:3306?useTimezone=true&serverTimezone=UTC";
    private static final String ADMIN_USER = "root";
    private static final String ADMIN_PASSWORD = "fatec";
    private static final String SCHEMA_FILE = "/database/schema.sql";
    
    /**
     * Inicializa o banco de dados executando o script SQL.
     * Este método verifica se o banco já existe antes de executar o script.
     * 
     * @return true se a inicialização foi bem-sucedida, false caso contrário
     */
    public static boolean initialize() {
        try {
            // Verifica se o banco já está inicializado
            if (isDatabaseInitialized()) {
                logger.info("Banco de dados já está inicializado. Pulando inicialização.");
                return true;
            }
            
            logger.info("Banco de dados não encontrado. Iniciando inicialização...");
            
            // Carrega o script SQL do arquivo
            List<String> statements = loadSqlStatements();
            
            if (statements.isEmpty()) {
                logger.warning("Nenhum comando SQL encontrado no arquivo de schema.");
                return false;
            }
            
            // Executa os comandos usando conexão administrativa
            try (Connection conn = getAdminConnection()) {
                executeStatements(conn, statements);
                logger.info("Banco de dados inicializado com sucesso!");
                return true;
            }
            
        } catch (Exception e) {
            logger.severe("Erro ao inicializar banco de dados: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Verifica se o banco de dados já está inicializado.
     * Verifica se o banco existe e se as tabelas principais já foram criadas.
     * 
     * @return true se o banco está inicializado, false caso contrário
     */
    private static boolean isDatabaseInitialized() {
        String dbUrl = "jdbc:mysql://localhost:3306/technotg?useTimezone=true&serverTimezone=UTC";
        
        try (Connection conn = DriverManager.getConnection(dbUrl, ADMIN_USER, ADMIN_PASSWORD)) {
            DatabaseMetaData metaData = conn.getMetaData();
            
            // Lista de tabelas principais que devem existir
            String[] requiredTables = {
                "orientador",
                "professor_tg",
                "aluno",
                "solicitacao_orientacao",
                "secao_api",
                "secao_apresentacao",
                "agendamento_defesa_tg"
            };
            
            // Verifica se todas as tabelas principais existem
            for (String tableName : requiredTables) {
                try (ResultSet rs = metaData.getTables(null, null, tableName, null)) {
                    if (!rs.next()) {
                        logger.info("Tabela '" + tableName + "' não encontrada. Banco precisa ser inicializado.");
                        return false;
                    }
                }
            }
            
            logger.info("Todas as tabelas principais encontradas. Banco já está inicializado.");
            return true;
            
        } catch (SQLException e) {
            // Se não conseguir conectar ao banco, significa que ele não existe
            String errorMsg = e.getMessage();
            if (errorMsg != null && errorMsg.contains("Unknown database")) {
                logger.info("Banco de dados 'technotg' não encontrado. Será criado.");
                return false;
            }
            // Outros erros podem ser problemas de conexão, mas assumimos que precisa inicializar
            logger.warning("Erro ao verificar banco de dados: " + errorMsg);
            return false;
        }
    }
    
    /**
     * Carrega e processa o arquivo SQL, dividindo-o em comandos individuais.
     * 
     * @return Lista de comandos SQL para execução
     */
    private static List<String> loadSqlStatements() throws Exception {
        List<String> statements = new ArrayList<>();
        
        try (InputStream inputStream = DatabaseInitializer.class.getResourceAsStream(SCHEMA_FILE)) {
            if (inputStream == null) {
                throw new Exception("Arquivo de schema não encontrado: " + SCHEMA_FILE);
            }
            
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                
                StringBuilder currentStatement = new StringBuilder();
                String line;
                
                while ((line = reader.readLine()) != null) {
                    // Remove comentários de linha
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("--")) {
                        continue;
                    }
                    
                    // Remove comentários inline (simplificado)
                    line = line.replaceAll("--.*$", "").trim();
                    
                    if (line.isEmpty()) {
                        continue;
                    }
                    
                    currentStatement.append(line).append(" ");
                    
                    // Se a linha termina com ponto e vírgula, finaliza o comando
                    if (line.endsWith(";")) {
                        String statement = currentStatement.toString().trim();
                        if (!statement.isEmpty() && statement.length() > 1) {
                            // Remove o ponto e vírgula final para execução
                            statement = statement.substring(0, statement.length() - 1).trim();
                            if (!statement.isEmpty()) {
                                statements.add(statement);
                            }
                        }
                        currentStatement.setLength(0);
                    }
                }
                
                // Adiciona o último comando se não terminou com ponto e vírgula
                String lastStatement = currentStatement.toString().trim();
                if (!lastStatement.isEmpty() && !lastStatement.equals(";")) {
                    statements.add(lastStatement);
                }
            }
        }
        
        return statements;
    }
    
    /**
     * Obtém uma conexão administrativa com o MySQL (sem especificar banco).
     * 
     * @return Conexão administrativa
     */
    private static Connection getAdminConnection() throws SQLException {
        try {
            return DriverManager.getConnection(ADMIN_URL, ADMIN_USER, ADMIN_PASSWORD);
        } catch (SQLException e) {
            logger.warning("Tentando conexão sem senha falhou. Verifique se o MySQL está configurado corretamente.");
            throw new SQLException("Não foi possível conectar ao MySQL como administrador. " +
                    "Certifique-se de que o MySQL está rodando e as credenciais estão corretas.", e);
        }
    }
    
    /**
     * Executa os comandos SQL na ordem especificada.
     * Separa comandos administrativos (CREATE DATABASE, CREATE USER, GRANT) 
     * dos comandos que precisam do banco technotg (CREATE TABLE).
     * 
     * @param conn Conexão administrativa com o banco de dados
     * @param statements Lista de comandos SQL para executar
     */
    private static void executeStatements(Connection conn, List<String> statements) throws SQLException {
        List<String> adminStatements = new ArrayList<>();
        List<String> schemaStatements = new ArrayList<>();
        boolean foundUseStatement = false;
        
        // Separa comandos administrativos dos comandos de schema
        for (String sql : statements) {
            String upperSql = sql.trim().toUpperCase();
            
            if (upperSql.startsWith("USE ")) {
                foundUseStatement = true;
                logger.info("Comando USE encontrado - separando comandos administrativos dos comandos de schema");
                continue;
            }
            
            if (upperSql.startsWith("CREATE DATABASE") || 
                upperSql.startsWith("DROP DATABASE") ||
                upperSql.startsWith("CREATE USER") ||
                upperSql.startsWith("DROP USER") ||
                upperSql.startsWith("GRANT ") ||
                upperSql.startsWith("REVOKE ")) {
                adminStatements.add(sql);
            } else {
                if (foundUseStatement) {
                    schemaStatements.add(sql);
                } else {
                    adminStatements.add(sql);
                }
            }
        }
        
        // Executa comandos administrativos primeiro
        try (Statement stmt = conn.createStatement()) {
            for (String sql : adminStatements) {
                executeStatement(stmt, sql, true);
            }
        }
        
        // Conecta ao banco technotg e executa comandos de schema
        if (!schemaStatements.isEmpty()) {
            String dbUrl = "jdbc:mysql://localhost:3306/technotg?useTimezone=true&serverTimezone=UTC";
            try (Connection dbConn = DriverManager.getConnection(dbUrl, ADMIN_USER, ADMIN_PASSWORD);
                 Statement stmt = dbConn.createStatement()) {
                
                logger.info("Conectando ao banco technotg para criar tabelas...");
                for (String sql : schemaStatements) {
                    executeStatement(stmt, sql, false);
                }
            }
        }
    }
    
    /**
     * Executa um comando SQL individual com tratamento de erros.
     * 
     * @param stmt Statement para execução
     * @param sql Comando SQL a ser executado
     * @param isAdminCommand Se true, trata erros de "already exists" como avisos
     */
    private static void executeStatement(Statement stmt, String sql, boolean isAdminCommand) {
        try {
            String preview = sql.length() > 50 ? sql.substring(0, 50) + "..." : sql;
            logger.info("Executando: " + preview);
            stmt.execute(sql);
            
        } catch (SQLException e) {
            String errorMsg = e.getMessage();
            
            // Para comandos administrativos, alguns erros são esperados
            if (isAdminCommand && errorMsg != null && 
                (errorMsg.contains("already exists") || 
                 errorMsg.contains("does not exist"))) {
                logger.info("Comando administrativo: " + errorMsg + " (pode ser esperado)");
                return;
            }
            
            // Para comandos de schema, erros de "already exists" também são aceitáveis
            if (errorMsg != null && errorMsg.contains("already exists")) {
                logger.info("Objeto já existe: " + errorMsg + " (continuando...)");
                return;
            }
            
            // Outros erros são logados como warnings
            logger.warning("Erro ao executar comando SQL: " + errorMsg);
            logger.warning("Comando: " + sql.substring(0, Math.min(100, sql.length())));
        }
    }
}


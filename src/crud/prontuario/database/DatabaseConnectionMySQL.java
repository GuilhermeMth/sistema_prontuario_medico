package crud.prontuario.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import crud.prontuario.util.ConfigLoader;

public class DatabaseConnectionMySQL implements IConnection {

    private final String USERNAME = ConfigLoader.getValor("DB_USER");
    private final String PASSWORD = ConfigLoader.getValor("DB_PASSWORD");
    private final String ADDRESS = ConfigLoader.getValor("DB_ADDRESS");
    private final String PORT = ConfigLoader.getValor("DB_PORT");
    private final String DATABASE = ConfigLoader.getValor("DB_SCHEMA");

    private Connection connection;

    @Override
    public Connection getConnection() {
        try {
            // Conecta sem selecionar banco para criar o schema
            String rootUrl = String.format("jdbc:mysql://%s:%s/", ADDRESS, PORT);
            connection = DriverManager.getConnection(rootUrl, USERNAME, PASSWORD);

            // Cria o banco se não existir
            try (Statement stmt = connection.createStatement()) {
                stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DATABASE);
            }

            // Conecta ao banco
            String dbUrl = rootUrl + DATABASE + "?useSSL=false&allowPublicKeyRetrieval=true";
            connection = DriverManager.getConnection(dbUrl, USERNAME, PASSWORD);

            // Cria as tabelas, se necessário
            createTablesIfNotExists();

            return connection;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void createTablesIfNotExists() throws SQLException {
        try (Statement stmt = connection.createStatement()) {

            // Criação da tabela de pacientes
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS pacientes (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    nome VARCHAR(100) NOT NULL,
                    cpf VARCHAR(14) UNIQUE NOT NULL
                );
            """);

            // Criação da tabela de exames
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS exames (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    descricao VARCHAR(255) NOT NULL,
                    data DATETIME NOT NULL,
                    paciente_id INT NOT NULL,
                    FOREIGN KEY (paciente_id) REFERENCES pacientes(id)
                        ON DELETE CASCADE
                );
            """);
        }
    }

    @Override
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
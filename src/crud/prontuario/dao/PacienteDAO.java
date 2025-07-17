package crud.prontuario.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import crud.prontuario.database.IConnection;
import crud.prontuario.model.Exame;
import crud.prontuario.model.Paciente;

/**
 * DAO para gerenciamento da entidade Paciente no banco de dados.
 * Implementa as operações básicas de CRUD (Create, Read, Update, Delete).
 * 
 * Aqui, também há associação com ExameDAO para tratar exames relacionados ao paciente.
 */
public class PacienteDAO implements IEntityDAO<Paciente> {

    private IConnection conn; // Interface para conexão ao banco, favorece abstração e flexibilidade
    private IEntityDAO<Exame> exameDAO; // DAO para manipular exames relacionados ao paciente

    public PacienteDAO(IConnection conn) {
        this.conn = conn;
        this.exameDAO = new ExameDAO(conn); // Instancia o DAO de Exame com a mesma conexão
    }

    /**
     * Cria um novo paciente no banco.
     * 
     * Valida se CPF e Nome foram fornecidos (não podem ser nulos).
     * Utiliza PreparedStatement para prevenir SQL Injection e inserir dados de forma segura.
     * Usa RETURN_GENERATED_KEYS para obter o ID gerado pelo banco e atualizar o objeto paciente.
     * 
     * Após criar o paciente, cria os exames associados, se houver.
     * Fecha a conexão para liberar recursos.
     */
    @Override
    public void create(Paciente t) {
        if (t.getCpf() == null || t.getNome() == null) {
            System.out.println("[ERRO] CPF e Nome são obrigatórios para o cadastro de um paciente.");
            return;
        }
        try {
            PreparedStatement pstm = conn.getConnection()
                .prepareStatement("INSERT INTO pacientes (nome, cpf) VALUES (?, ?);",
                                  PreparedStatement.RETURN_GENERATED_KEYS);
            pstm.setString(1, t.getNome());
            pstm.setString(2, t.getCpf());
            int affectedRows = pstm.executeUpdate();

            if (affectedRows == 0) {
                System.out.println("[ERRO] Nenhum paciente foi cadastrado. Verifique os dados informados.");
                return;
            }

            ResultSet generatedKeys = pstm.getGeneratedKeys();
            if (generatedKeys.next()) {
                long generatedId = generatedKeys.getLong(1);
                t.setId(generatedId); // Atualiza o objeto com o ID gerado
            } else {
                System.out.println("[ERRO] Não foi possível recuperar o ID do paciente recém-cadastrado.");
            }
            pstm.close();

        } catch (SQLException e) {
            System.out.println("[ERRO] Falha ao cadastrar paciente: " + e.getMessage());
        }

        // Caso o paciente tenha exames associados no objeto, cria-os também no banco
        if (t.getExames() != null) {
            for (Exame ex : t.getExames()) {
                exameDAO.create(ex);
            }
        }

        // Fecha a conexão para evitar vazamento de recursos
        conn.closeConnection();
    }

    /**
     * Busca um paciente pelo ID.
     * 
     * Usa PreparedStatement para consulta parametrizada, evitando SQL Injection.
     * Mapeia os dados do ResultSet para o objeto Paciente.
     */
    @Override
    public Paciente findById(Long id) {
        Paciente p = null;
        try {
            PreparedStatement pstm = conn.getConnection()
                .prepareStatement("SELECT * FROM pacientes WHERE ID = ?;");
            pstm.setLong(1, id);
            ResultSet rs = pstm.executeQuery();
            if (rs.next()) {
                p = new Paciente();
                p.setCpf(rs.getString("cpf"));
                p.setId(rs.getLong("id"));
                p.setNome(rs.getString("nome"));
            } else {
                System.out.println("[INFO] Nenhum paciente encontrado com o ID informado.");
            }
            pstm.close();
        } catch (SQLException e) {
            System.out.println("[ERRO] Falha ao buscar paciente: " + e.getMessage());
        }
        return p;
    }

    /**
     * Remove um paciente pelo ID.
     * 
     * Usa PreparedStatement para a exclusão segura.
     */
    @Override
    public void delete(Paciente t) {
        try {
            PreparedStatement pstm = conn.getConnection()
                .prepareStatement("DELETE FROM pacientes WHERE ID = ?;");
            pstm.setLong(1, t.getId());
            int deleted = pstm.executeUpdate();
            if (deleted == 0) {
                System.out.println("[INFO] Nenhum paciente foi removido. Verifique se o ID está correto.");
            }
            pstm.close();
        } catch (SQLException e) {
            System.out.println("[ERRO] Falha ao remover paciente: " + e.getMessage());
        }
    }

    /**
     * Retorna todos os pacientes cadastrados.
     * 
     * Monta uma lista de objetos Paciente a partir do ResultSet.
     */
    @Override
    public List<Paciente> findAll() {
        List<Paciente> pacientes = new ArrayList<>();
        try {
            PreparedStatement pstm = conn.getConnection()
                .prepareStatement("SELECT * FROM pacientes;");
            ResultSet rs = pstm.executeQuery();
            while (rs.next()) {
                pacientes.add(new Paciente(rs.getLong("id"), rs.getString("nome"), rs.getString("cpf")));
            }
            pstm.close();
        } catch (SQLException e) {
            System.out.println("[ERRO] Falha ao buscar pacientes: " + e.getMessage());
        }
        return pacientes;
    }

    /**
     * Atualiza os dados de um paciente.
     * 
     * Usa PreparedStatement para parametrizar a query de update.
     * Caso nenhuma linha seja atualizada, informa para facilitar debug.
     */
    @Override
    public void update(Paciente t) {
        try {
            PreparedStatement pstm = conn.getConnection()
                .prepareStatement("UPDATE pacientes SET nome = ?, cpf = ? WHERE id = ?;");
            pstm.setString(1, t.getNome());
            pstm.setString(2, t.getCpf());
            pstm.setLong(3, t.getId());
            int updated = pstm.executeUpdate();
            if (updated == 0) {
                System.out.println("[INFO] Nenhum paciente foi atualizado. Verifique o ID informado.");
            }
            pstm.close();
        } catch (SQLException e) {
            System.out.println("[ERRO] Falha ao atualizar paciente: " + e.getMessage());
        }
    }
}

package crud.prontuario.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import crud.prontuario.database.IConnection;
import crud.prontuario.model.Exame;

/**
 * DAO para gerenciamento da entidade Exame no banco de dados MySQL.
 * Implementa operações básicas de CRUD (Create, Read, Update, Delete).
 * 
 * Utiliza JDBC para manipulação das consultas SQL, com PreparedStatement para evitar SQL Injection.
 */
public class ExameDAO implements IEntityDAO<Exame> {
	
	private IConnection conn; // Interface para conexão com o banco, facilita a troca da implementação (ex: MySQL, PostgreSQL, etc.)
	
	public ExameDAO(IConnection connection) {
		this.conn = connection;
	}

	/**
	 * Insere um novo exame no banco.
	 * Utiliza PreparedStatement com RETURN_GENERATED_KEYS para obter o ID gerado automaticamente pelo banco.
	 * 
	 * Por que PreparedStatement?
	 * - Evita SQL Injection.
	 * - Permite parametrizar valores sem necessidade de concatenar strings.
	 * 
	 * Por que obter o ID gerado?
	 * - Para manter o objeto em memória consistente com o banco (sincronização do ID).
	 */
	@Override
	public void create(Exame t) {
		try {
			PreparedStatement pstm = conn.getConnection()
					.prepareStatement("INSERT INTO exames (descricao, data, paciente_id) VALUES (?, ?, ?);",
						PreparedStatement.RETURN_GENERATED_KEYS);
			pstm.setString(1, t.getDescricao());
			// Timestamp.valueOf converte LocalDateTime para Timestamp do SQL
			pstm.setTimestamp(2, Timestamp.valueOf(t.getData()));
			pstm.setLong(3, t.getPacienteId());
			int affectedRows = pstm.executeUpdate();

			if (affectedRows == 0) {
				System.out.println("[ERRO] Nenhuma linha foi inserida para o exame.");
				return;
			}

			ResultSet generatedKeys = pstm.getGeneratedKeys();
			if (generatedKeys.next()) {
				long generatedId = generatedKeys.getLong(1);
				t.setId(generatedId);
			} else {
				System.out.println("[ERRO] Falha ao obter o ID gerado para o exame.");
			}
			pstm.close();
		} catch (SQLException e) {
			System.out.println("[ERRO] Falha ao inserir exame: " + e.getMessage());
		}
	}

	/**
	 * Consulta um exame pelo seu ID.
	 * 
	 * Uso do PreparedStatement para parametrizar a consulta e evitar SQL Injection.
	 * 
	 * Converte o Timestamp retornado para LocalDateTime para usar a API Java 8+ de datas.
	 */
	@Override
	public Exame findById(Long id) {
		Exame ex = null;
		try {
			PreparedStatement pstm = conn.getConnection()
					.prepareStatement("SELECT * FROM exames WHERE ID = ?;");
			pstm.setLong(1, id);
			ResultSet rs = pstm.executeQuery();
			if(rs.next()) {
				ex = new Exame();
				ex.setId(rs.getLong("id"));
				ex.setDescricao(rs.getString("descricao"));
				// Conversão do timestamp SQL para LocalDateTime Java
				ex.setData(rs.getTimestamp("data").toLocalDateTime());
				ex.setPacienteId(rs.getLong("paciente_id"));
			}
			pstm.close();
		} catch (SQLException e) {
			System.out.println("[ERRO] Falha ao buscar exame por ID: " + e.getMessage());
		}
		return ex;
	}

	/**
	 * Remove um exame do banco pelo seu ID.
	 * 
	 * Usado PreparedStatement para segurança e parametrização.
	 */
	@Override
	public void delete(Exame t) {
		try {
			PreparedStatement pstm = conn.getConnection()
					.prepareStatement("DELETE FROM exames WHERE ID = ?;");
			pstm.setLong(1, t.getId());
			int deleted = pstm.executeUpdate();
			if (deleted == 0) {
				System.out.println("[ERRO] Nenhum exame foi deletado. Verifique o ID.");
			}
			pstm.close();
		} catch (SQLException e) {
			System.out.println("[ERRO] Falha ao deletar exame: " + e.getMessage());
		}
	}

	/**
	 * Retorna uma lista de todos os exames cadastrados.
	 * 
	 * Itera sobre o ResultSet, convertendo cada linha para objeto Exame.
	 * Utiliza ArrayList para manter a lista dinamicamente.
	 */
	@Override
	public List<Exame> findAll() {
		List<Exame> exames = new ArrayList<>();
		try {
			PreparedStatement pstm = conn.getConnection()
					.prepareStatement("SELECT * FROM exames;");
			ResultSet rs = pstm.executeQuery();
			while(rs.next()) {
				exames.add(new Exame(
					rs.getLong("id"),
					rs.getString("descricao"),
					rs.getTimestamp("data").toLocalDateTime(),
					rs.getLong("paciente_id")
				));
			}
			pstm.close();
		} catch (SQLException e) {
			System.out.println("[ERRO] Falha ao listar exames: " + e.getMessage());
		}
		return exames;
	}

	/**
	 * Atualiza os dados de um exame existente.
	 * 
	 * Todos os campos são atualizados com base no objeto passado.
	 * É importante garantir que o ID exista para não perder dados.
	 * 
	 * Retorna mensagem caso nenhuma linha seja atualizada para auxiliar na depuração.
	 */
	@Override
	public void update(Exame t) {
		try {
			PreparedStatement pstm = conn.getConnection()
					.prepareStatement("UPDATE exames SET descricao = ?, data = ?, paciente_id = ? WHERE id = ?;");
			pstm.setString(1, t.getDescricao());
			pstm.setTimestamp(2, Timestamp.valueOf(t.getData()));
			pstm.setLong(3, t.getPacienteId());
			pstm.setLong(4, t.getId());
			int updated = pstm.executeUpdate();
			if (updated == 0) {
				System.out.println("[ERRO] Nenhum exame foi atualizado. Verifique o ID.");
			}
			pstm.close();
		} catch (SQLException e) {
			System.out.println("[ERRO] Falha ao atualizar exame: " + e.getMessage());
		}
	}
}

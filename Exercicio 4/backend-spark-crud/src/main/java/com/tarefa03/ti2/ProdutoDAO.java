package com.tarefa03.ti2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDAO {

    private static final String URL = "jdbc:postgresql://localhost:5432/db_produtos";
    private static final String USER = "postgres";
    private static final String PASS = "25felipe";

    private Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    public boolean inserir(Produto produto) {
        String sql = "INSERT INTO produto (nome, preco, quantidade) VALUES (?, ?, ?)";

        // O try-with-resources (Java 7+) garante o fechamento automático da conexão
        try (Connection conn = conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, produto.getNome());
            pstmt.setDouble(2, produto.getPreco());
            pstmt.setInt(3, produto.getQuantidade());

            // executeUpdate() retorna o número de linhas afetadas no banco
            int linhasAfetadas = pstmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            System.err.println("Erro crítico ao inserir: " + e.getMessage());
            return false;
        }
    }

    // =========================================================================
    // READ (R do CRUD) - Deserialização de Relacional para Objeto
    // =========================================================================
    public List<Produto> listar() {
        List<Produto> produtos = new ArrayList<>();
        String sql = "SELECT * FROM produto ORDER BY id ASC";

        try (Connection conn = conectar(); PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {

            // O ResultSet é um cursor que percorre as linhas retornadas pelo banco
            while (rs.next()) {
                Produto p = new Produto();
                p.setId(rs.getInt("id"));
                p.setNome(rs.getString("nome"));
                p.setPreco(rs.getDouble("preco"));
                p.setQuantidade(rs.getInt("quantidade"));

                produtos.add(p);
            }

        } catch (SQLException e) {
            System.err.println("Erro crítico ao listar: " + e.getMessage());
        }
        return produtos;
    }

    // =========================================================================
    // UPDATE (U do CRUD)
    // =========================================================================
    public boolean atualizar(Produto produto) {
        String sql = "UPDATE produto SET nome = ?, preco = ?, quantidade = ? WHERE id = ?";

        try (Connection conn = conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, produto.getNome());
            pstmt.setDouble(2, produto.getPreco());
            pstmt.setInt(3, produto.getQuantidade());
            pstmt.setInt(4, produto.getId());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Erro crítico ao atualizar: " + e.getMessage());
            return false;
        }
    }

    // =========================================================================
    // DELETE (D do CRUD)
    // =========================================================================
    public boolean excluir(int id) {
        String sql = "DELETE FROM produto WHERE id = ?";

        try (Connection conn = conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Erro crítico ao excluir: " + e.getMessage());
            return false;
        }
    }
}

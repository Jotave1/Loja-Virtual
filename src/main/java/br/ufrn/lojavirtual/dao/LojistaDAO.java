package br.ufrn.lojavirtual.dao;

import br.ufrn.lojavirtual.model.Lojista;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LojistaDAO {

    public void salvar(Lojista lojista) {
        String sql = "INSERT INTO lojista (nome, email, senha) VALUES (?, ?, ?)";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, lojista.getNome());
            stmt.setString(2, lojista.getEmail());
            stmt.setString(3, lojista.getSenha());
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Lojista buscarPorEmailSenha(String email, String senha) {
        String sql = "SELECT * FROM lojista WHERE email = ? AND senha = ?";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, senha);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Lojista lojista = new Lojista();
                lojista.setId(rs.getLong("id"));
                lojista.setNome(rs.getString("nome"));
                lojista.setEmail(rs.getString("email"));
                lojista.setSenha(rs.getString("senha"));
                return lojista;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}

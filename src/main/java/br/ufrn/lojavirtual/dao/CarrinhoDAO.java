package br.ufrn.lojavirtual.dao;

import br.ufrn.lojavirtual.model.CarrinhoItem;
import br.ufrn.lojavirtual.model.Produto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CarrinhoDAO {

    private final ProdutoDAO produtoDAO = new ProdutoDAO();

    private Long obterOuCriarCarrinhoId(Long clienteId) {
        String select = "SELECT id FROM carrinho WHERE cliente_id = ?";
        String insert = "INSERT INTO carrinho (cliente_id) VALUES (?)";

        try (Connection conn = Conexao.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(select);
            stmt.setLong(1, clienteId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getLong("id");
            }

            stmt = conn.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
            stmt.setLong(1, clienteId);
            stmt.executeUpdate();

            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getLong(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void adicionarProduto(Long clienteId, Long produtoId) {
        Long carrinhoId = obterOuCriarCarrinhoId(clienteId);

        if (carrinhoId == null) return;

        String select = "SELECT quantidade FROM carrinho_item WHERE carrinho_id = ? AND produto_id = ?";
        String insert = "INSERT INTO carrinho_item (carrinho_id, produto_id, quantidade) VALUES (?, ?, 1)";
        String update = "UPDATE carrinho_item SET quantidade = quantidade + 1 WHERE carrinho_id = ? AND produto_id = ?";

        try (Connection conn = Conexao.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(select);
            stmt.setLong(1, carrinhoId);
            stmt.setLong(2, produtoId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                PreparedStatement up = conn.prepareStatement(update);
                up.setLong(1, carrinhoId);
                up.setLong(2, produtoId);
                up.executeUpdate();
            } else {
                PreparedStatement ins = conn.prepareStatement(insert);
                ins.setLong(1, carrinhoId);
                ins.setLong(2, produtoId);
                ins.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<CarrinhoItem> buscarItensDoCarrinho(Long clienteId) {
        List<CarrinhoItem> itens = new ArrayList<>();

        String sql = """
            SELECT ci.quantidade, p.id AS pid, p.nome, p.descricao, p.preco, p.estoque
            FROM carrinho_item ci
            JOIN carrinho c ON c.id = ci.carrinho_id
            JOIN produto p ON p.id = ci.produto_id
            WHERE c.cliente_id = ?
        """;

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, clienteId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Produto produto = new Produto();
                produto.setId(rs.getLong("pid"));
                produto.setNome(rs.getString("nome"));
                produto.setDescricao(rs.getString("descricao"));
                produto.setPreco(rs.getDouble("preco"));
                produto.setEstoque(rs.getInt("estoque"));

                CarrinhoItem item = new CarrinhoItem();
                item.setProduto(produto);
                item.setQuantidade(rs.getInt("quantidade"));

                itens.add(item);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return itens;
    }

    public void esvaziar(Long clienteId) {
        String sql = """
            DELETE FROM carrinho_item
            WHERE carrinho_id = (
                SELECT id FROM carrinho WHERE cliente_id = ?
            )
        """;

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, clienteId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

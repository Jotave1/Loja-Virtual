package br.ufrn.lojavirtual.controller;

import br.ufrn.lojavirtual.dao.ProdutoDAO;
import br.ufrn.lojavirtual.model.Lojista;
import br.ufrn.lojavirtual.model.Produto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Controller
@RequestMapping("/produto")
public class ProdutoController {

    private final ProdutoDAO produtoDAO = new ProdutoDAO();

    @RequestMapping("/cadastro")
    public void mostrarCadastro(HttpServletResponse response) throws IOException {
        response.sendRedirect("/cadastro-produto.html");
    }

    @RequestMapping("/salvar")
    public void salvar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        Lojista lojista = (Lojista) session.getAttribute("lojista");

        if (lojista == null) {
            response.sendRedirect("/login-lojista.html");
            return;
        }

        String nome = request.getParameter("nome");
        String descricao = request.getParameter("descricao");
        double preco = Double.parseDouble(request.getParameter("preco"));
        int estoque = Integer.parseInt(request.getParameter("estoque"));

        Produto p = new Produto();
        p.setNome(nome);
        p.setDescricao(descricao);
        p.setPreco(preco);
        p.setEstoque(estoque);
        p.setLojista(lojista);

        produtoDAO.salvar(p);

        response.sendRedirect("/produto/meus");
    }

    @RequestMapping("/meus")
    public void listarDoLojista(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        Lojista lojista = (Lojista) session.getAttribute("lojista");

        if (lojista == null) {
            response.sendRedirect("/login-lojista.html");
            return;
        }

        List<Produto> produtos = produtoDAO.buscarPorLojistaId(lojista.getId());

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println("<html><body>");
        out.println("<h1>Produtos da minha loja</h1>");
        out.println("<table border='1'>");
        out.println("<tr><th>Nome</th><th>Descrição</th><th>Preço</th><th>Estoque</th><th>Ação</th></tr>");

        for (Produto p : produtos) {
            out.printf(
                    "<tr><td>%s</td><td>%s</td><td>R$ %.2f</td><td>%d</td>" +
                            "<td><a href='/produto/remover?id=%d'>Remover</a></td></tr>",
                    p.getNome(), p.getDescricao(), p.getPreco(), p.getEstoque(), p.getId()
            );
        }

        out.println("</table>");
        out.println("<br><a href='/produto.html'>Adicionar novo produto</a>");
        out.println("<br><a href='/index.html'>Voltar ao menu</a>");
        out.println("</body></html>");
    }

    @RequestMapping("/remover")
    public void remover(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String idParam = request.getParameter("id");
        if (idParam != null) {
            Long id = Long.parseLong(idParam);
            produtoDAO.excluir(id);
        }
        response.sendRedirect("/produto/meus");
    }
    @RequestMapping("/listar")
    public void listarParaCliente(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<Produto> produtos = produtoDAO.buscarTodos();

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println("<html><body>");
        out.println("<h1>Produtos Disponíveis</h1>");
        out.println("<table border='1'>");
        out.println("<tr><th>Nome</th><th>Descrição</th><th>Preço</th><th>Estoque</th><th>Ação</th></tr>");

        for (Produto p : produtos) {
            out.printf("<tr><td>%s</td><td>%s</td><td>R$ %.2f</td><td>%d</td>", p.getNome(), p.getDescricao(), p.getPreco(), p.getEstoque());

            if (p.getEstoque() > 0) {
                out.printf("<td><a href='/carrinho/adicionar?id=%d'>Adicionar</a></td></tr>", p.getId());
            } else {
                out.println("<td>Indisponível</td></tr>");
            }
        }

        out.println("</table>");
        out.println("<br><a href='/carrinho/ver'>Ver Carrinho</a>");
        out.println("<br><a href='/index.html'>Voltar ao menu</a>");
        out.println("</body></html>");
    }


}

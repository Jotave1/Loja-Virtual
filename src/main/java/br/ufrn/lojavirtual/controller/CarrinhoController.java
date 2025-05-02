package br.ufrn.lojavirtual.controller;

import br.ufrn.lojavirtual.dao.CarrinhoDAO;
import br.ufrn.lojavirtual.dao.ProdutoDAO;
import br.ufrn.lojavirtual.model.CarrinhoItem;
import br.ufrn.lojavirtual.model.Cliente;
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
@RequestMapping("/carrinho")
public class CarrinhoController {

    private final CarrinhoDAO carrinhoDAO = new CarrinhoDAO();
    private final ProdutoDAO produtoDAO = new ProdutoDAO();

    @RequestMapping("/adicionar")
    public void adicionar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        Cliente cliente = (Cliente) session.getAttribute("cliente");

        if (cliente == null) {
            response.sendRedirect("/login-cliente.html");
            return;
        }

        Long produtoId = Long.parseLong(request.getParameter("id"));
        carrinhoDAO.adicionarProduto(cliente.getId(), produtoId);

        response.sendRedirect("/carrinho/ver");
    }

    @RequestMapping("/ver")
    public void verCarrinho(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        Cliente cliente = (Cliente) session.getAttribute("cliente");

        if (cliente == null) {
            response.sendRedirect("/login-cliente.html");
            return;
        }

        List<CarrinhoItem> itens = carrinhoDAO.buscarItensDoCarrinho(cliente.getId());

        double total = itens.stream()
                .mapToDouble(i -> i.getProduto().getPreco() * i.getQuantidade())
                .sum();

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println("<html><body>");
        out.println("<h1>Meu Carrinho</h1>");
        out.println("<table border='1'>");
        out.println("<tr><th>Produto</th><th>Quantidade</th><th>Preço</th><th>Subtotal</th></tr>");

        for (CarrinhoItem item : itens) {
            Produto p = item.getProduto();
            double subtotal = p.getPreco() * item.getQuantidade();

            out.printf(
                    "<tr><td>%s</td><td>%d</td><td>R$ %.2f</td><td>R$ %.2f</td></tr>",
                    p.getNome(), item.getQuantidade(), p.getPreco(), subtotal
            );
        }

        out.printf("</table><h3>Total: R$ %.2f</h3>", total);
        out.println("<br><a href='/carrinho/finalizar'>Finalizar Compra</a>");
        out.println("<br><a href='/carrinho/esvaziar'>Esvaziar Carrinho</a>");
        out.println("<br><a href='/produto/listar'>Voltar para produtos</a>");
        out.println("</body></html>");
    }

    @RequestMapping("/esvaziar")
    public void esvaziar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        Cliente cliente = (Cliente) session.getAttribute("cliente");

        if (cliente != null) {
            carrinhoDAO.esvaziar(cliente.getId());
        }

        response.sendRedirect("/carrinho/ver");
    }

    @RequestMapping("/finalizar")
    public void finalizar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        Cliente cliente = (Cliente) session.getAttribute("cliente");

        if (cliente != null) {
            carrinhoDAO.esvaziar(cliente.getId());
        }

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html><body>");
        out.println("<h2>Compra finalizada com sucesso!</h2>");
        out.println("<br><a href='/produto/listar'>Voltar para produtos</a>");
        out.println("</body></html>");
    }
}

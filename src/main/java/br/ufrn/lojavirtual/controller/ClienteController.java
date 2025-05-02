package br.ufrn.lojavirtual.controller;

import br.ufrn.lojavirtual.dao.ClienteDAO;
import br.ufrn.lojavirtual.model.Cliente;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@Controller
@RequestMapping("/cliente")
public class ClienteController {

    private final ClienteDAO clienteDAO = new ClienteDAO();

    @RequestMapping("/cadastro")
    public void mostrarCadastro(HttpServletResponse response) throws IOException {
        response.sendRedirect("/cadastro-cliente.html");
    }

    @RequestMapping("/salvar")
    public void salvar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String nome = request.getParameter("nome");
        String email = request.getParameter("email");
        String senha = request.getParameter("senha");

        Cliente cliente = new Cliente();
        cliente.setNome(nome);
        cliente.setEmail(email);
        cliente.setSenha(senha);

        clienteDAO.salvar(cliente);

        response.sendRedirect("/login-cliente.html");
    }

    @RequestMapping("/login")
    public void mostrarLogin(HttpServletResponse response) throws IOException {
        response.sendRedirect("/login-cliente.html");
    }

    @RequestMapping("/autenticar")
    public void autenticar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String email = request.getParameter("email");
        String senha = request.getParameter("senha");

        Cliente cliente = clienteDAO.buscarPorEmailSenha(email, senha);

        if (cliente != null) {
            HttpSession session = request.getSession();
            session.setAttribute("cliente", cliente);
            response.sendRedirect("/produto/listar");
        } else {
            response.sendRedirect("/login-cliente.html?erro=1");
        }
    }
}

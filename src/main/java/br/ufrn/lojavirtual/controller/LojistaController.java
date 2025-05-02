package br.ufrn.lojavirtual.controller;

import br.ufrn.lojavirtual.dao.LojistaDAO;
import br.ufrn.lojavirtual.model.Lojista;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@Controller
@RequestMapping("/lojista")
public class LojistaController {

    private final LojistaDAO lojistaDAO = new LojistaDAO();

    @RequestMapping("/cadastro")
    public void mostrarCadastro(HttpServletResponse response) throws IOException {
        response.sendRedirect("/cadastro-lojista.html");
    }

    @RequestMapping("/salvar")
    public void salvar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String nome = request.getParameter("nome");
        String email = request.getParameter("email");
        String senha = request.getParameter("senha");

        Lojista lojista = new Lojista();
        lojista.setNome(nome);
        lojista.setEmail(email);
        lojista.setSenha(senha);

        lojistaDAO.salvar(lojista);

        response.sendRedirect("/login-lojista.html");
    }

    @RequestMapping("/login")
    public void mostrarLogin(HttpServletResponse response) throws IOException {
        response.sendRedirect("/login-lojista.html");
    }

    @RequestMapping("/autenticar")
    public void autenticar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String email = request.getParameter("email");
        String senha = request.getParameter("senha");

        Lojista lojista = lojistaDAO.buscarPorEmailSenha(email, senha);

        if (lojista != null) {
            HttpSession session = request.getSession();
            session.setAttribute("lojista", lojista);
            response.sendRedirect("/produto/meus");
        } else {
            response.sendRedirect("/login-lojista.html?erro=1");
        }
    }
}

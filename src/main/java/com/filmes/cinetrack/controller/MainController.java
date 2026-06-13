package com.filmes.cinetrack.controller;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.filmes.cinetrack.model.MinhaLista;
import com.filmes.cinetrack.model.MinhaListaService;
import com.filmes.cinetrack.model.TmdbService;
import com.filmes.cinetrack.model.Usuario;
import com.filmes.cinetrack.model.UsuarioService;

import jakarta.servlet.http.HttpSession;

@Controller
public class MainController {

    @Autowired
    ApplicationContext context;

    @GetMapping("/")
    public String index(HttpSession session) {
        return session.getAttribute("usuario") != null ? "redirect:/filmes" : "redirect:/login";
    }

    // ── AUTH ──────────────────────────────────────────────────────────────────
    @GetMapping("/login")
    public String loginPage(HttpSession session) {
        if (session.getAttribute("usuario") != null) return "redirect:/filmes";
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String senha,
                        HttpSession session, Model model) {
        UsuarioService us = context.getBean(UsuarioService.class);
        try {
            Usuario usuario = us.obterPorEmail(email);
            if (usuario != null && us.verificarSenha(senha, usuario.getSenha())) {
                session.setAttribute("usuario", usuario);
                return "redirect:/filmes";
            }
        } catch (Exception e) {}
        model.addAttribute("erro", "E-mail ou senha inválidos.");
        return "login";
    }

    @GetMapping("/registro")
    public String registroPage(HttpSession session, Model model) {
        if (session.getAttribute("usuario") != null) return "redirect:/filmes";
        model.addAttribute("usuario", new Usuario());
        return "registro";
    }

    @PostMapping("/registro")
    public String registro(@ModelAttribute Usuario usuario, Model model) {
        UsuarioService us = context.getBean(UsuarioService.class);
        if (us.existePorEmail(usuario.getEmail())) {
            model.addAttribute("erro", "Este e-mail já está cadastrado.");
            return "registro";
        }
        us.inserirUsuario(usuario);
        return "redirect:/login?cadastrado=true";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    // ── FILMES ────────────────────────────────────────────────────────────────
    @GetMapping("/filmes")
    public String filmes(@RequestParam(required = false) String filtro,
                         @RequestParam(required = false) String busca,
                         HttpSession session, Model model) {
        if (session.getAttribute("usuario") == null) return "redirect:/login";
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        TmdbService ts = context.getBean(TmdbService.class);
        MinhaListaService mls = context.getBean(MinhaListaService.class);
        var lista = (busca != null && !busca.isBlank())
                ? ts.buscar(busca, "movie") : ts.filmes(filtro);
        Set<Long> idsNaLista = mls.obterPorUsuario(usuario.getId())
                .stream().map(MinhaLista::getTmdbId).collect(Collectors.toSet());
        model.addAttribute("itens", lista);
        model.addAttribute("idsNaLista", idsNaLista);
        model.addAttribute("usuario", usuario);
        model.addAttribute("filtro", filtro != null ? filtro : "popular");
        model.addAttribute("busca", busca);
        return "filmes";
    }

    // ── SÉRIES ────────────────────────────────────────────────────────────────
    @GetMapping("/series")
    public String series(@RequestParam(required = false) String filtro,
                         @RequestParam(required = false) String busca,
                         HttpSession session, Model model) {
        if (session.getAttribute("usuario") == null) return "redirect:/login";
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        TmdbService ts = context.getBean(TmdbService.class);
        MinhaListaService mls = context.getBean(MinhaListaService.class);
        var lista = (busca != null && !busca.isBlank())
                ? ts.buscar(busca, "tv") : ts.series(filtro);
        Set<Long> idsNaLista = mls.obterPorUsuario(usuario.getId())
                .stream().map(MinhaLista::getTmdbId).collect(Collectors.toSet());
        model.addAttribute("itens", lista);
        model.addAttribute("idsNaLista", idsNaLista);
        model.addAttribute("usuario", usuario);
        model.addAttribute("filtro", filtro != null ? filtro : "popular");
        model.addAttribute("busca", busca);
        return "series";
    }

    // ── ADICIONAR ─────────────────────────────────────────────────────────────
    @PostMapping("/adicionar")
    public String adicionar(@RequestParam long tmdbId, @RequestParam String titulo,
                            @RequestParam String tipo,
                            @RequestParam(required = false) String posterPath,
                            @RequestParam(required = false, defaultValue = "/filmes") String origem,
                            HttpSession session) {
        if (session.getAttribute("usuario") == null) return "redirect:/login";
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        MinhaListaService mls = context.getBean(MinhaListaService.class);
        if (!mls.existePorUsuarioETmdb(usuario.getId(), tmdbId)) {
            mls.inserir(new MinhaLista(usuario.getId(), tmdbId, titulo, tipo, posterPath, "quero assistir", null));
        }
        return "redirect:" + origem;
    }

    // ── LISTA ─────────────────────────────────────────────────────────────────
    @GetMapping("/lista")
    public String lista(HttpSession session, Model model) {
        if (session.getAttribute("usuario") == null) return "redirect:/login";
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        MinhaListaService mls = context.getBean(MinhaListaService.class);
        model.addAttribute("itens", mls.obterPorUsuario(usuario.getId()));
        model.addAttribute("usuario", usuario);
        return "lista";
    }

    @GetMapping("/lista/{id}/editar")
    public String formEditarItem(@PathVariable int id, HttpSession session, Model model) {
        if (session.getAttribute("usuario") == null) return "redirect:/login";
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        MinhaListaService mls = context.getBean(MinhaListaService.class);
        MinhaLista item = mls.obterPorId(id);
        if (item == null || item.getUsuarioId() != usuario.getId()) return "redirect:/lista";
        model.addAttribute("id", id);
        model.addAttribute("item", item);
        return "formeditar";
    }

    @PostMapping("/lista/{id}/editar")
    public String editarItem(@PathVariable int id, @ModelAttribute MinhaLista item, HttpSession session) {
        if (session.getAttribute("usuario") == null) return "redirect:/login";
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        MinhaListaService mls = context.getBean(MinhaListaService.class);
        MinhaLista existente = mls.obterPorId(id);
        if (existente == null || existente.getUsuarioId() != usuario.getId()) return "redirect:/lista";
        mls.atualizar(id, item);
        return "redirect:/lista";
    }

    @PostMapping("/lista/{id}/excluir")
    public String excluirItem(@PathVariable int id, HttpSession session) {
        if (session.getAttribute("usuario") == null) return "redirect:/login";
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        MinhaListaService mls = context.getBean(MinhaListaService.class);
        MinhaLista existente = mls.obterPorId(id);
        if (existente == null || existente.getUsuarioId() != usuario.getId()) return "redirect:/lista";
        mls.excluir(id);
        return "redirect:/lista";
    }

    // ── PERFIL ────────────────────────────────────────────────────────────────
    @GetMapping("/perfil")
    public String perfil(HttpSession session, Model model) {
        if (session.getAttribute("usuario") == null) return "redirect:/login";
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        MinhaListaService mls = context.getBean(MinhaListaService.class);
        List<MinhaLista> itens = mls.obterPorUsuario(usuario.getId());
        model.addAttribute("usuario",       usuario);
        model.addAttribute("total",         itens.size());
        model.addAttribute("assistidos",    itens.stream().filter(i -> "assistido".equals(i.getStatus())).count());
        model.addAttribute("assistindo",    itens.stream().filter(i -> "assistindo".equals(i.getStatus())).count());
        model.addAttribute("queroAssistir", itens.stream().filter(i -> "quero assistir".equals(i.getStatus())).count());
        model.addAttribute("totalFilmes",   itens.stream().filter(i -> "movie".equals(i.getTipo())).count());
        model.addAttribute("totalSeries",   itens.stream().filter(i -> "tv".equals(i.getTipo())).count());
        return "perfil";
    }

    @PostMapping("/perfil/dados")
    public String editarDados(@RequestParam String nome, @RequestParam String email,
                              HttpSession session, RedirectAttributes ra) {
        if (session.getAttribute("usuario") == null) return "redirect:/login";
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        UsuarioService us = context.getBean(UsuarioService.class);
        if (!email.equals(usuario.getEmail()) && us.existePorEmail(email)) {
            ra.addFlashAttribute("erro", "Este e-mail já está em uso.");
            return "redirect:/perfil";
        }
        usuario.setNome(nome);
        usuario.setEmail(email);
        us.atualizar(usuario);
        session.setAttribute("usuario", usuario);
        ra.addFlashAttribute("sucesso", "Dados atualizados com sucesso!");
        return "redirect:/perfil";
    }

    @PostMapping("/perfil/senha")
    public String alterarSenha(@RequestParam String senhaAtual,
                               @RequestParam String novaSenha,
                               @RequestParam String confirmarSenha,
                               HttpSession session, RedirectAttributes ra) {
        if (session.getAttribute("usuario") == null) return "redirect:/login";
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        UsuarioService us = context.getBean(UsuarioService.class);
        if (!us.verificarSenha(senhaAtual, usuario.getSenha())) {
            ra.addFlashAttribute("erro", "Senha atual incorreta.");
            return "redirect:/perfil";
        }
        if (!novaSenha.equals(confirmarSenha)) {
            ra.addFlashAttribute("erro", "Nova senha e confirmação não coincidem.");
            return "redirect:/perfil";
        }
        if (novaSenha.length() < 6) {
            ra.addFlashAttribute("erro", "A nova senha deve ter pelo menos 6 caracteres.");
            return "redirect:/perfil";
        }
        us.alterarSenha(usuario, novaSenha);
        session.setAttribute("usuario", us.obterPorEmail(usuario.getEmail()));
        ra.addFlashAttribute("sucesso", "Senha alterada com sucesso!");
        return "redirect:/perfil";
    }

    // ── DETALHE AJAX ──────────────────────────────────────────────────────────
    @GetMapping("/api/detalhe")
    @ResponseBody
    public java.util.Map<String, Object> detalheApi(@RequestParam long tmdbId,
                                                     @RequestParam String tipo,
                                                     HttpSession session) {
        if (session.getAttribute("usuario") == null) return java.util.Map.of("erro", "não autenticado");
        TmdbService ts = context.getBean(TmdbService.class);
        return ts.detalhe(tmdbId, tipo);
    }

    // ── CATÁLOGO ──────────────────────────────────────────────────────────────
    @GetMapping("/catalogo")
    public String catalogo(HttpSession session) {
        if (session.getAttribute("usuario") == null) return "redirect:/login";
        return "redirect:/filmes";
    }
}

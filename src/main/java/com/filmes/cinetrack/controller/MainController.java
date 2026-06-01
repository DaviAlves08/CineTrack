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

    // ── RAIZ ─────────────────────────────────────────────────────────────────
    @GetMapping("/")
    public String index(HttpSession session) {
        return session.getAttribute("usuario") != null
                ? "redirect:/filmes"
                : "redirect:/login";
    }

    // ══════════════════════════════════════════════════════════════════════════
    // AUTH
    // ══════════════════════════════════════════════════════════════════════════

    @GetMapping("/login")
    public String loginPage(HttpSession session) {
        if (session.getAttribute("usuario") != null) return "redirect:/filmes";
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String senha,
                        HttpSession session,
                        Model model) {
        UsuarioService us = context.getBean(UsuarioService.class);
        try {
            Usuario usuario = us.obterPorEmail(email);
            if (usuario != null && usuario.getSenha().equals(senha)) {
                session.setAttribute("usuario", usuario);
                return "redirect:/filmes";
            }
        } catch (Exception e) { /* email não encontrado */ }

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

    // ══════════════════════════════════════════════════════════════════════════
    // FILMES
    // ══════════════════════════════════════════════════════════════════════════

    @GetMapping("/filmes")
    public String filmes(@RequestParam(required = false) String filtro,
                         @RequestParam(required = false) String busca,
                         HttpSession session,
                         Model model) {
        if (session.getAttribute("usuario") == null) return "redirect:/login";

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        TmdbService tmdb = context.getBean(TmdbService.class);

        var lista = (busca != null && !busca.isBlank())
                ? tmdb.buscar(busca, "movie")
                : tmdb.filmes(filtro);

        MinhaListaService mls = context.getBean(MinhaListaService.class);
        Set<Long> idsNaLista = mls.obterPorUsuario(usuario.getId())
                .stream().map(MinhaLista::getTmdbId).collect(Collectors.toSet());

        model.addAttribute("itens",      lista);
        model.addAttribute("idsNaLista", idsNaLista);
        model.addAttribute("usuario",    usuario);
        model.addAttribute("filtro",     filtro != null ? filtro : "popular");
        model.addAttribute("busca",      busca);
        return "filmes";
    }

    // ══════════════════════════════════════════════════════════════════════════
    // SÉRIES
    // ══════════════════════════════════════════════════════════════════════════

    @GetMapping("/series")
    public String series(@RequestParam(required = false) String filtro,
                         @RequestParam(required = false) String busca,
                         HttpSession session,
                         Model model) {
        if (session.getAttribute("usuario") == null) return "redirect:/login";

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        TmdbService tmdb = context.getBean(TmdbService.class);

        var lista = (busca != null && !busca.isBlank())
                ? tmdb.buscar(busca, "tv")
                : tmdb.series(filtro);

        MinhaListaService mls = context.getBean(MinhaListaService.class);
        Set<Long> idsNaLista = mls.obterPorUsuario(usuario.getId())
                .stream().map(MinhaLista::getTmdbId).collect(Collectors.toSet());

        model.addAttribute("itens",      lista);
        model.addAttribute("idsNaLista", idsNaLista);
        model.addAttribute("usuario",    usuario);
        model.addAttribute("filtro",     filtro != null ? filtro : "popular");
        model.addAttribute("busca",      busca);
        return "series";
    }

    // ══════════════════════════════════════════════════════════════════════════
    // ADICIONAR À LISTA (compartilhado por filmes e séries)
    // ══════════════════════════════════════════════════════════════════════════

    @PostMapping("/adicionar")
    public String adicionar(@RequestParam long   tmdbId,
                            @RequestParam String titulo,
                            @RequestParam String tipo,
                            @RequestParam(required = false) String posterPath,
                            @RequestParam(required = false, defaultValue = "/filmes") String origem,
                            HttpSession session) {
        if (session.getAttribute("usuario") == null) return "redirect:/login";

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        MinhaListaService mls = context.getBean(MinhaListaService.class);

        if (!mls.existePorUsuarioETmdb(usuario.getId(), tmdbId)) {
            MinhaLista item = new MinhaLista(
                usuario.getId(), tmdbId, titulo, tipo, posterPath, "quero assistir", null
            );
            mls.inserir(item);
        }
        return "redirect:" + origem;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // MINHA LISTA
    // ══════════════════════════════════════════════════════════════════════════

    @GetMapping("/lista")
    public String lista(HttpSession session, Model model) {
        if (session.getAttribute("usuario") == null) return "redirect:/login";

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        MinhaListaService mls = context.getBean(MinhaListaService.class);
        List<MinhaLista> itens = mls.obterPorUsuario(usuario.getId());

        model.addAttribute("itens",   itens);
        model.addAttribute("usuario", usuario);
        return "lista";
    }

    @GetMapping("/lista/{id}/editar")
    public String formEditarItem(Model model,
                                 @PathVariable int id,
                                 HttpSession session) {
        if (session.getAttribute("usuario") == null) return "redirect:/login";

        MinhaListaService mls = context.getBean(MinhaListaService.class);
        model.addAttribute("id",   id);
        model.addAttribute("item", mls.obterPorId(id));
        return "formeditar";
    }

    @PostMapping("/lista/{id}/editar")
    public String editarItem(@PathVariable int id,
                             @ModelAttribute MinhaLista item,
                             HttpSession session) {
        if (session.getAttribute("usuario") == null) return "redirect:/login";

        MinhaListaService mls = context.getBean(MinhaListaService.class);
        mls.atualizar(id, item);
        return "redirect:/lista";
    }

    @PostMapping("/lista/{id}/excluir")
    public String excluirItem(@PathVariable int id, HttpSession session) {
        if (session.getAttribute("usuario") == null) return "redirect:/login";

        MinhaListaService mls = context.getBean(MinhaListaService.class);
        mls.excluir(id);
        return "redirect:/lista";
    }

    // ══════════════════════════════════════════════════════════════════════════
    // PERFIL
    // ══════════════════════════════════════════════════════════════════════════

    @GetMapping("/perfil")
    public String perfil(HttpSession session, Model model) {
        if (session.getAttribute("usuario") == null) return "redirect:/login";

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        MinhaListaService mls = context.getBean(MinhaListaService.class);
        List<MinhaLista> itens = mls.obterPorUsuario(usuario.getId());

        long assistidos   = itens.stream().filter(i -> "assistido".equals(i.getStatus())).count();
        long assistindo   = itens.stream().filter(i -> "assistindo".equals(i.getStatus())).count();
        long queroAssistir = itens.stream().filter(i -> "quero assistir".equals(i.getStatus())).count();
        long filmes       = itens.stream().filter(i -> "movie".equals(i.getTipo())).count();
        long series       = itens.stream().filter(i -> "tv".equals(i.getTipo())).count();

        model.addAttribute("usuario",      usuario);
        model.addAttribute("total",        itens.size());
        model.addAttribute("assistidos",   assistidos);
        model.addAttribute("assistindo",   assistindo);
        model.addAttribute("queroAssistir",queroAssistir);
        model.addAttribute("totalFilmes",  filmes);
        model.addAttribute("totalSeries",  series);
        return "perfil";
    }
}

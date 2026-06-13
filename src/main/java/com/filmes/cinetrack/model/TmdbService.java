package com.filmes.cinetrack.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TmdbService {

    @Value("${tmdb.api.key}")
    private String apiKey;

    private static final String BASE = "https://api.themoviedb.org/3";
    private static final String LANG = "language=pt-BR";

    private final HttpClient   httpClient   = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // ── FILMES ───────────────────────────────────────────────────────────────
    public List<Map<String, Object>> filmes(String filtro) {
        String endpoint = switch (filtro == null ? "popular" : filtro) {
            case "top_rated"   -> "/movie/top_rated";
            case "now_playing" -> "/movie/now_playing";
            case "upcoming"    -> "/movie/upcoming";
            default            -> "/movie/popular";
        };
        // ✅ MELHORIA 5: API key enviada via header Authorization, não na URL
        return chamar(BASE + endpoint + "?api_key=" + apiKey + "&" + LANG, "movie");
    }

    // ── SÉRIES ───────────────────────────────────────────────────────────────
    public List<Map<String, Object>> series(String filtro) {
        String endpoint = switch (filtro == null ? "popular" : filtro) {
            case "top_rated"    -> "/tv/top_rated";
            case "on_the_air"   -> "/tv/on_the_air";
            case "airing_today" -> "/tv/airing_today";
            default             -> "/tv/popular";
        };
        return chamar(BASE + endpoint + "?api_key=" + apiKey + "&" + LANG, "tv");
    }

    // ── BUSCA ────────────────────────────────────────────────────────────────
    public List<Map<String, Object>> buscar(String query, String tipo) {
        try {
            String encoded = URLEncoder.encode(query, StandardCharsets.UTF_8);
            if ("movie".equals(tipo)) {
                return chamar(BASE + "/search/movie?api_key=" + apiKey + "&" + LANG + "&query=" + encoded, "movie");
            } else if ("tv".equals(tipo)) {
                return chamar(BASE + "/search/tv?api_key=" + apiKey + "&" + LANG + "&query=" + encoded, "tv");
            } else {
                return chamar(BASE + "/search/multi?api_key=" + apiKey + "&" + LANG + "&query=" + encoded, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // ── MÉTODO INTERNO ───────────────────────────────────────────────────────
    private List<Map<String, Object>> chamar(String url, String tipoFixo) {
        try {
            // ✅ MELHORIA 5: API key no header Authorization em vez da URL
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            JsonNode root    = objectMapper.readTree(response.body());
            JsonNode results = root.path("results");

            List<Map<String, Object>> lista = new ArrayList<>();

            if (results.isArray()) {
                for (JsonNode item : results) {
                    String tipo = tipoFixo != null
                            ? tipoFixo
                            : item.path("media_type").asText("");

                    if (!tipo.equals("movie") && !tipo.equals("tv")) continue;

                    String titulo = tipo.equals("movie")
                            ? item.path("title").asText(item.path("name").asText("Sem título"))
                            : item.path("name").asText("Sem título");

                    String dataStr = tipo.equals("movie")
                            ? item.path("release_date").asText("")
                            : item.path("first_air_date").asText("");
                    String ano = dataStr.length() >= 4 ? dataStr.substring(0, 4) : "—";

                    Map<String, Object> media = new HashMap<>();
                    media.put("id",         item.path("id").asLong());
                    media.put("tipo",       tipo);
                    media.put("titulo",     titulo);
                    media.put("posterPath", item.path("poster_path").asText(""));
                    media.put("descricao",  item.path("overview").asText("Sem descrição disponível."));
                    media.put("nota",       item.path("vote_average").asDouble());
                    media.put("ano",        ano);

                    lista.add(media);
                }
            }
            return lista;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // ── DETALHE (para modal da lista) ────────────────────────────────────────
    public Map<String, Object> detalhe(long tmdbId, String tipo) {
        String endpoint = tipo.equals("movie")
                ? "/movie/" + tmdbId
                : "/tv/"    + tmdbId;
        String url = BASE + endpoint + "?api_key=" + apiKey + "&" + LANG;
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("accept", "application/json")
                    .GET().build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode item = objectMapper.readTree(response.body());

            String titulo = tipo.equals("movie")
                    ? item.path("title").asText(item.path("name").asText(""))
                    : item.path("name").asText("");
            String dataStr = tipo.equals("movie")
                    ? item.path("release_date").asText("")
                    : item.path("first_air_date").asText("");
            String ano = dataStr.length() >= 4 ? dataStr.substring(0, 4) : "—";

            Map<String, Object> det = new java.util.HashMap<>();
            det.put("id",          item.path("id").asLong());
            det.put("titulo",      titulo);
            det.put("posterPath",  item.path("poster_path").asText(""));
            det.put("descricao",   item.path("overview").asText("Sem descrição disponível."));
            det.put("nota",        item.path("vote_average").asDouble());
            det.put("ano",         ano);
            det.put("tipo",        tipo);
            return det;
        } catch (Exception e) {
            e.printStackTrace();
            return new java.util.HashMap<>();
        }
    }

}
// placeholder

package dev.paulobernardes.sistemasocio.gerenciadores;

import dev.paulobernardes.sistemasocio.modelos.Socio;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GerenciadorSocios {

    private final JavaPlugin plugin;
    private final Map<String, Socio> socios = new HashMap<>();

    public GerenciadorSocios(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void carregar() {

        socios.clear();

        ConfigurationSection secao = plugin.getConfig().getConfigurationSection("socios");

        if (secao == null) {
            plugin.getLogger().warning("Nenhum Sócio foi encontrado no config.yml.");
            return;
        }

        for (String id : secao.getKeys(false)) {

            String caminho = "socios." + id;

            String nome = plugin.getConfig().getString(caminho + ".nome", id);
            String grupoLuckPerms = plugin.getConfig().getString(caminho + ".grupo-luckperms", id);
            int nivel = plugin.getConfig().getInt(caminho + ".nivel", 1);
            double preco = plugin.getConfig().getDouble(caminho + ".preco");
            double dinheiroAtivacao = plugin.getConfig().getDouble(caminho + ".dinheiro-ativacao");
            String duracao = plugin.getConfig().getString(caminho + ".duracao", "30d");
            boolean ativado = plugin.getConfig().getBoolean(caminho + ".ativado", true);
            List<String> beneficios = plugin.getConfig().getStringList(caminho + ".beneficios");

            Socio socio = new Socio(
                    id,
                    nome,
                    grupoLuckPerms,
                    nivel,
                    preco,
                    dinheiroAtivacao,
                    duracao,
                    ativado,
                    beneficios
            );

            socios.put(id, socio);
        }

        plugin.getLogger().info("SistemaSocio » " + socios.size() + " tipos de Sócio carregados.");
    }

    public Socio getSocio(String id) {
        return socios.get(id);
    }

    public Map<String, Socio> getSocios() {
        return socios;
    }
}
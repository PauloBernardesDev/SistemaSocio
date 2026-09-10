package dev.paulobernardes.sistemasocio;

import dev.paulobernardes.sistemasocio.eventos.SocioMenuListener;
import dev.paulobernardes.sistemasocio.comandos.SocioComando;
import dev.paulobernardes.sistemasocio.gerenciadores.ExpiracaoSocios;
import dev.paulobernardes.sistemasocio.comandos.KeyComando;
import dev.paulobernardes.sistemasocio.comandos.GerarKeyComando;
import dev.paulobernardes.sistemasocio.gerenciadores.GerenciadorKeys;
import dev.paulobernardes.sistemasocio.comandos.RemoverSocioComando;
import dev.paulobernardes.sistemasocio.comandos.AjudaComando;
import dev.paulobernardes.sistemasocio.comandos.DefinirSocioComando;
import dev.paulobernardes.sistemasocio.gerenciadores.GerenciadorBanco;
import dev.paulobernardes.sistemasocio.gerenciadores.GerenciadorJogadores;
import dev.paulobernardes.sistemasocio.gerenciadores.GerenciadorSocios;
import net.luckperms.api.LuckPerms;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.java.JavaPlugin;

public final class SistemaSocio extends JavaPlugin {

    private LuckPerms luckPerms;
    private Economy economia;
    private GerenciadorBanco gerenciadorBanco;
    private GerenciadorSocios gerenciadorSocios;
    private GerenciadorJogadores gerenciadorJogadores;
    private GerenciadorKeys gerenciadorKeys;
    private ExpiracaoSocios expiracaoSocios;
    private KeyComando keyComando;
    private SocioComando socioComando;

    @Override
    public void onEnable() {

        saveDefaultConfig();

        gerenciadorBanco = new GerenciadorBanco(this);
        gerenciadorBanco.conectar();

        gerenciadorKeys = new GerenciadorKeys(gerenciadorBanco);

        gerenciadorJogadores = new GerenciadorJogadores(gerenciadorBanco);

        int sociosRetomados = gerenciadorJogadores.retomarSocios();

        if (sociosRetomados > 0) {
            getLogger().info(
                    "SistemaSocio » " + sociosRetomados + " Sócio(s) retomado(s)."
            );
        }

        gerenciadorSocios = new GerenciadorSocios(this);
        gerenciadorSocios.carregar();

        luckPerms = getServer()
                .getServicesManager()
                .load(LuckPerms.class);

        if (luckPerms == null) {
            getLogger().severe("Não foi possível conectar ao LuckPerms!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getLogger().info("SistemaSocio » LuckPerms conectado!");

        expiracaoSocios = new ExpiracaoSocios(
                this,
                gerenciadorJogadores,
                gerenciadorSocios,
                luckPerms
        );

        expiracaoSocios.iniciar();

        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            getLogger().severe("Vault não encontrado!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        economia = getServer()
                .getServicesManager()
                .getRegistration(Economy.class)
                .getProvider();

        if (economia == null) {
            getLogger().severe("Nenhum sistema de economia foi encontrado!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getLogger().info("SistemaSocio » Vault conectado!");
        getLogger().info("SistemaSocio » Economia encontrada!");

        getCommand("setsocio").setExecutor(
                new DefinirSocioComando(
                        gerenciadorJogadores,
                        gerenciadorSocios,
                        luckPerms
                )
        );

        getCommand("sociohelp").setExecutor(new AjudaComando());

        getCommand("removesocio").setExecutor(
                new RemoverSocioComando(
                        gerenciadorJogadores,
                        gerenciadorSocios,
                        luckPerms
                )
        );

        getCommand("gerarkey").setExecutor(
                new GerarKeyComando(
                        gerenciadorKeys,
                        gerenciadorSocios
                )
        );

        socioComando = new SocioComando(
                gerenciadorSocios,
                gerenciadorJogadores
        );

        getCommand("socio").setExecutor(socioComando);

        keyComando = new KeyComando(
                gerenciadorKeys,
                gerenciadorJogadores,
                gerenciadorSocios,
                luckPerms,
                economia
        );

        getCommand("key").setExecutor(keyComando);

        getServer().getPluginManager().registerEvents(
                new SocioMenuListener(
                        this,
                        keyComando,
                        socioComando
                ),
                this
        );

        getLogger().info("SistemaSocio » Plugin ativado com sucesso!");
    }

    @Override
    public void onDisable() {

        if (gerenciadorJogadores != null) {

            int sociosPausados = gerenciadorJogadores.pausarSocios();

            if (sociosPausados > 0) {
                getLogger().info(
                        "SistemaSocio » " + sociosPausados + " Sócio(s) pausado(s)."
                );
            }
        }

        if (gerenciadorBanco != null) {
            gerenciadorBanco.desconectar();
        }

        getLogger().info("SistemaSocio » Plugin desativado.");
    }

    public LuckPerms getLuckPerms() {
        return luckPerms;
    }

    public Economy getEconomia() {
        return economia;
    }

    public GerenciadorBanco getGerenciadorBanco() {
        return gerenciadorBanco;
    }

    public GerenciadorSocios getGerenciadorSocios() {
        return gerenciadorSocios;
    }

    public GerenciadorJogadores getGerenciadorJogadores() {
        return gerenciadorJogadores;
    }
}
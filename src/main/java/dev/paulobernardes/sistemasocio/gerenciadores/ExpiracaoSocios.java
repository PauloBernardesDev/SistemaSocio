package dev.paulobernardes.sistemasocio.gerenciadores;

import org.bukkit.entity.Player;
import dev.paulobernardes.sistemasocio.modelos.Socio;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;

public class ExpiracaoSocios {

    private final JavaPlugin plugin;
    private final GerenciadorJogadores gerenciadorJogadores;
    private final GerenciadorSocios gerenciadorSocios;
    private final LuckPerms luckPerms;

    public ExpiracaoSocios(
            JavaPlugin plugin,
            GerenciadorJogadores gerenciadorJogadores,
            GerenciadorSocios gerenciadorSocios,
            LuckPerms luckPerms
    ) {
        this.plugin = plugin;
        this.gerenciadorJogadores = gerenciadorJogadores;
        this.gerenciadorSocios = gerenciadorSocios;
        this.luckPerms = luckPerms;
    }

    public void iniciar() {

        new BukkitRunnable() {

            @Override
            public void run() {

                verificarExpiracoes();
            }

        }.runTaskTimer(plugin, 20L, 20L);
    }

    private void verificarExpiracoes() {

        Map<UUID, String> expirados =
                gerenciadorJogadores.getSociosExpirados();

        for (Map.Entry<UUID, String> entrada : expirados.entrySet()) {

            UUID uuid = entrada.getKey();
            String tipoSocioExpirado = entrada.getValue();

            String tipoSocioAtual =
                    gerenciadorJogadores.getTipoSocio(uuid);

            if (tipoSocioAtual == null) {
                continue;
            }

            if (!tipoSocioAtual.equalsIgnoreCase(tipoSocioExpirado)) {
                continue;
            }

            Socio socio =
                    gerenciadorSocios.getSocio(tipoSocioExpirado);

            if (socio != null) {

                User usuario =
                        luckPerms.getUserManager().getUser(uuid);

                if (usuario != null) {

                    InheritanceNode grupo =
                            InheritanceNode.builder(
                                    socio.getGrupoLuckPerms()
                            ).build();

                    usuario.data().remove(grupo);

                    luckPerms.getUserManager().saveUser(usuario);
                }
            }

            gerenciadorJogadores.removerSocio(uuid);

            Player jogador = plugin.getServer().getPlayer(uuid);

            if (jogador != null) {
                jogador.sendMessage(
                        "§dSistemaSocio §8» §cSeu Sócio expirou."
                );
            }
        }
    }
}
package dev.paulobernardes.sistemasocio.comandos;

import dev.paulobernardes.sistemasocio.gerenciadores.GerenciadorJogadores;
import dev.paulobernardes.sistemasocio.gerenciadores.GerenciadorSocios;
import dev.paulobernardes.sistemasocio.modelos.Socio;
import dev.paulobernardes.sistemasocio.utilidades.Mensagens;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class RemoverSocioComando implements CommandExecutor {

    private final GerenciadorJogadores gerenciadorJogadores;
    private final GerenciadorSocios gerenciadorSocios;
    private final LuckPerms luckPerms;

    public RemoverSocioComando(
            GerenciadorJogadores gerenciadorJogadores,
            GerenciadorSocios gerenciadorSocios,
            LuckPerms luckPerms
    ) {
        this.gerenciadorJogadores = gerenciadorJogadores;
        this.gerenciadorSocios = gerenciadorSocios;
        this.luckPerms = luckPerms;
    }

    @Override
    public boolean onCommand(
            CommandSender remetente,
            Command comando,
            String rotulo,
            String[] argumentos
    ) {

        if (!remetente.isOp() && !remetente.hasPermission("sistemasocio.admin")) {
            remetente.sendMessage(
                    Mensagens.prefixo("&cVocê não tem permissão para usar este comando.")
            );
            return true;
        }

        if (argumentos.length != 1) {
            remetente.sendMessage(
                    Mensagens.prefixo("&cUso correto: &f/removesocio <jogador>")
            );
            return true;
        }

        Player jogador = Bukkit.getPlayerExact(argumentos[0]);

        if (jogador == null) {
            remetente.sendMessage(
                    Mensagens.prefixo("&cO jogador precisa estar online.")
            );
            return true;
        }

        UUID uuid = jogador.getUniqueId();

        String tipoSocio = gerenciadorJogadores.getTipoSocio(uuid);

        if (tipoSocio == null) {
            remetente.sendMessage(Mensagens.linha());
            remetente.sendMessage(Mensagens.titulo("Erro"));
            remetente.sendMessage("");
            remetente.sendMessage(
                    Mensagens.formatar("&cO jogador &f" + jogador.getName() + " &cnão possui um Sócio registrado.")
            );
            remetente.sendMessage("");
            remetente.sendMessage(Mensagens.linha());
            return true;
        }

        Socio socio = gerenciadorSocios.getSocio(tipoSocio);

        User usuario = luckPerms.getUserManager().getUser(uuid);

        if (usuario != null && socio != null) {

            InheritanceNode grupo = InheritanceNode.builder(
                    socio.getGrupoLuckPerms()
            ).build();

            usuario.data().remove(grupo);

            luckPerms.getUserManager().saveUser(usuario);
        }

        gerenciadorJogadores.removerSocio(uuid);

        remetente.sendMessage(Mensagens.linha());
        remetente.sendMessage(Mensagens.titulo("Sócio removido"));
        remetente.sendMessage("");

        remetente.sendMessage(
                Mensagens.formatar("&7Jogador: &f" + jogador.getName())
        );

        if (socio != null) {
            remetente.sendMessage(
                    Mensagens.formatar("&7Sócio removido: &d" + socio.getNome())
            );
        }

        remetente.sendMessage("");
        remetente.sendMessage(
                Mensagens.formatar("&a✓ Sócio removido com sucesso!")
        );

        remetente.sendMessage("");
        remetente.sendMessage(Mensagens.linha());

        jogador.sendMessage(Mensagens.linha());
        jogador.sendMessage(Mensagens.titulo("Sócio removido"));
        jogador.sendMessage("");
        jogador.sendMessage(
                Mensagens.formatar("&cSeu Sócio foi removido.")
        );
        jogador.sendMessage("");
        jogador.sendMessage(Mensagens.linha());

        return true;
    }
}
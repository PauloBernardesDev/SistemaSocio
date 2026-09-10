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

public class DefinirSocioComando implements CommandExecutor {

    private final GerenciadorJogadores gerenciadorJogadores;
    private final GerenciadorSocios gerenciadorSocios;
    private final LuckPerms luckPerms;

    public DefinirSocioComando(
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

        if (argumentos.length != 3) {
            remetente.sendMessage(
                    Mensagens.prefixo("&cUso correto: &f/setsocio <jogador> <socio> <tempo>")
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

        String tipoSocio = argumentos[1].toLowerCase();
        Socio novoSocio = gerenciadorSocios.getSocio(tipoSocio);

        if (novoSocio == null) {
            remetente.sendMessage(Mensagens.linha());
            remetente.sendMessage(Mensagens.titulo("Erro"));
            remetente.sendMessage("");
            remetente.sendMessage(
                    Mensagens.formatar("&cO tipo de Sócio &f" + tipoSocio + " &cnão existe.")
            );
            remetente.sendMessage("");
            remetente.sendMessage(
                    Mensagens.formatar("&7Confira os tipos disponíveis no &f/sociohelp&7.")
            );
            remetente.sendMessage("");
            remetente.sendMessage(Mensagens.linha());
            return true;
        }

        if (!novoSocio.isAtivado()) {
            remetente.sendMessage(
                    Mensagens.prefixo("&cEsse tipo de Sócio está desativado.")
            );
            return true;
        }

        long duracao = converterTempo(argumentos[2]);

        if (duracao <= 0) {
            remetente.sendMessage(Mensagens.linha());
            remetente.sendMessage(Mensagens.titulo("Erro"));
            remetente.sendMessage("");
            remetente.sendMessage(
                    Mensagens.formatar("&cTempo inválido.")
            );
            remetente.sendMessage(
                    Mensagens.formatar("&7Exemplos: &f30d &7, &f12h &7, &f60m &7ou &f30s")
            );
            remetente.sendMessage("");
            remetente.sendMessage(Mensagens.linha());
            return true;
        }

        UUID uuid = jogador.getUniqueId();

        User usuario = luckPerms.getUserManager().getUser(uuid);

        if (usuario != null) {

            String tipoSocioAtual = gerenciadorJogadores.getTipoSocio(uuid);

            if (tipoSocioAtual != null) {

                Socio socioAtual = gerenciadorSocios.getSocio(tipoSocioAtual);

                if (socioAtual != null) {

                    InheritanceNode grupoAtual = InheritanceNode.builder(
                            socioAtual.getGrupoLuckPerms()
                    ).build();

                    usuario.data().remove(grupoAtual);
                }
            }

            InheritanceNode novoGrupo = InheritanceNode.builder(
                    novoSocio.getGrupoLuckPerms()
            ).build();

            usuario.data().add(novoGrupo);

            luckPerms.getUserManager().saveUser(usuario);
        }

        long agora = System.currentTimeMillis();
        long expiracao = agora + duracao;

        gerenciadorJogadores.ativarSocio(
                uuid,
                tipoSocio,
                agora,
                expiracao
        );

        remetente.sendMessage(Mensagens.linha());
        remetente.sendMessage(Mensagens.titulo("Sócio definido"));
        remetente.sendMessage("");

        remetente.sendMessage(
                Mensagens.formatar("&7Jogador: &f" + jogador.getName())
        );

        remetente.sendMessage(
                Mensagens.formatar("&7Sócio: &d" + novoSocio.getNome())
        );

        remetente.sendMessage(
                Mensagens.formatar("&7Duração: &f" + argumentos[2])
        );

        remetente.sendMessage("");
        remetente.sendMessage(
                Mensagens.formatar("&a✓ Sócio definido com sucesso!")
        );

        remetente.sendMessage("");
        remetente.sendMessage(Mensagens.linha());

        jogador.sendMessage(Mensagens.linha());
        jogador.sendMessage(Mensagens.titulo("Novo Sócio"));
        jogador.sendMessage("");

        jogador.sendMessage(
                Mensagens.formatar("&aVocê recebeu o &d" + novoSocio.getNome() + "&a!")
        );

        jogador.sendMessage(
                Mensagens.formatar("&7Duração: &f" + argumentos[2])
        );

        jogador.sendMessage("");
        jogador.sendMessage(Mensagens.linha());

        return true;
    }

    private long converterTempo(String tempo) {

        try {

            String valor = tempo.substring(0, tempo.length() - 1);
            char unidade = tempo.charAt(tempo.length() - 1);

            long numero = Long.parseLong(valor);

            if (numero <= 0) {
                return -1;
            }

            return switch (unidade) {
                case 's' -> numero * 1000L;
                case 'm' -> numero * 60 * 1000L;
                case 'h' -> numero * 60 * 60 * 1000L;
                case 'd' -> numero * 24 * 60 * 60 * 1000L;
                default -> -1;
            };

        } catch (Exception erro) {
            return -1;
        }
    }
}
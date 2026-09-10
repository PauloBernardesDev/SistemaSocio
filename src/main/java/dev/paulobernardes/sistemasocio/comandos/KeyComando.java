package dev.paulobernardes.sistemasocio.comandos;

import dev.paulobernardes.sistemasocio.gerenciadores.GerenciadorJogadores;
import dev.paulobernardes.sistemasocio.gerenciadores.GerenciadorKeys;
import dev.paulobernardes.sistemasocio.gerenciadores.GerenciadorSocios;
import dev.paulobernardes.sistemasocio.modelos.KeySocio;
import dev.paulobernardes.sistemasocio.modelos.Socio;
import dev.paulobernardes.sistemasocio.utilidades.Mensagens;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.InheritanceNode;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class KeyComando implements CommandExecutor {

    private final GerenciadorKeys gerenciadorKeys;
    private final GerenciadorJogadores gerenciadorJogadores;
    private final GerenciadorSocios gerenciadorSocios;
    private final LuckPerms luckPerms;
    private final Economy economia;

    public KeyComando(
            GerenciadorKeys gerenciadorKeys,
            GerenciadorJogadores gerenciadorJogadores,
            GerenciadorSocios gerenciadorSocios,
            LuckPerms luckPerms,
            Economy economia
    ) {
        this.gerenciadorKeys = gerenciadorKeys;
        this.gerenciadorJogadores = gerenciadorJogadores;
        this.gerenciadorSocios = gerenciadorSocios;
        this.luckPerms = luckPerms;
        this.economia = economia;
    }

    @Override
    public boolean onCommand(
            CommandSender remetente,
            Command comando,
            String rotulo,
            String[] argumentos
    ) {

        if (!(remetente instanceof Player jogador)) {
            remetente.sendMessage(
                    Mensagens.prefixo("&cApenas jogadores podem utilizar Keys.")
            );
            return true;
        }

        if (argumentos.length != 1) {
            jogador.sendMessage(
                    Mensagens.prefixo("&cUso correto: &f/key <chave>")
            );
            return true;
        }

        ativarKey(jogador, argumentos[0]);

        return true;
    }

    public void ativarKey(Player jogador, String chaveInformada) {

        String chave = chaveInformada.toUpperCase();

        KeySocio key = gerenciadorKeys.buscarKey(chave);

        if (key == null) {
            jogador.sendMessage(
                    Mensagens.prefixo("&cEssa Key não existe.")
            );
            return;
        }

        if (key.isUtilizada()) {
            jogador.sendMessage(
                    Mensagens.prefixo("&cEssa Key já foi utilizada.")
            );
            return;
        }

        Socio socioKey = gerenciadorSocios.getSocio(
                key.getTipoSocio()
        );

        if (socioKey == null) {
            jogador.sendMessage(
                    Mensagens.prefixo("&cO Sócio dessa Key não existe mais.")
            );
            return;
        }

        if (!socioKey.isAtivado()) {
            jogador.sendMessage(
                    Mensagens.prefixo("&cEsse tipo de Sócio está desativado.")
            );
            return;
        }

        long duracao = converterTempo(key.getDuracao());

        if (duracao <= 0) {
            jogador.sendMessage(
                    Mensagens.prefixo("&cA duração dessa Key é inválida.")
            );
            return;
        }

        UUID uuid = jogador.getUniqueId();

        String tipoAtual = gerenciadorJogadores.getTipoSocio(uuid);
        Socio socioAtual = null;

        if (tipoAtual != null) {
            socioAtual = gerenciadorSocios.getSocio(tipoAtual);
        }

        String tipoFinal;
        Socio socioFinal;

        if (socioAtual == null || !gerenciadorJogadores.temSocio(uuid)) {

            tipoFinal = socioKey.getId();
            socioFinal = socioKey;

        } else if (socioKey.getNivel() >= socioAtual.getNivel()) {

            tipoFinal = socioKey.getId();
            socioFinal = socioKey;

        } else {

            tipoFinal = socioAtual.getId();
            socioFinal = socioAtual;
        }

        long agora = System.currentTimeMillis();
        long expiracaoAtual = gerenciadorJogadores.getExpiracao(uuid);

        long novaExpiracao;

        if (expiracaoAtual > agora) {
            novaExpiracao = expiracaoAtual + duracao;
        } else {
            novaExpiracao = agora + duracao;
        }

        double dinheiro = socioKey.getDinheiroAtivacao();

        if (dinheiro > 0) {

            var resposta = economia.depositPlayer(
                    jogador,
                    dinheiro
            );

            if (!resposta.transactionSuccess()) {

                jogador.sendMessage(
                        Mensagens.prefixo(
                                "&cNão foi possível adicionar o dinheiro da ativação."
                        )
                );

                return;
            }
        }

        atualizarLuckPerms(
                uuid,
                socioAtual,
                socioFinal
        );

        gerenciadorJogadores.ativarSocio(
                uuid,
                tipoFinal,
                agora,
                novaExpiracao
        );

        if (!gerenciadorKeys.utilizarKey(chave, uuid)) {
            jogador.sendMessage(
                    Mensagens.prefixo("&cNão foi possível utilizar essa Key.")
            );
            return;
        }

        jogador.sendMessage(Mensagens.linha());
        jogador.sendMessage(Mensagens.titulo("Sócio ativado"));
        jogador.sendMessage("");

        jogador.sendMessage(
                Mensagens.formatar(
                        "&aVocê ativou o &d" + socioFinal.getNome() + "&a!"
                )
        );

        jogador.sendMessage(
                Mensagens.formatar(
                        "&7Duração adicionada: &f" + key.getDuracao()
                )
        );

        if (dinheiro > 0) {

            jogador.sendMessage(
                    Mensagens.formatar(
                            "&aDinheiro recebido: &2$" +
                                    String.format("%.2f", dinheiro)
                    )
            );
        }

        if (socioAtual != null
                && socioFinal.getNivel() > socioAtual.getNivel()) {

            jogador.sendMessage(
                    Mensagens.formatar(
                            "&b✦ Seu Sócio foi promovido para &f"
                                    + socioFinal.getNome()
                    )
            );
        }

        jogador.sendMessage("");
        jogador.sendMessage(
                Mensagens.formatar("&a✓ Key utilizada com sucesso!")
        );
        jogador.sendMessage("");
        jogador.sendMessage(Mensagens.linha());
    }

    private void atualizarLuckPerms(
            UUID uuid,
            Socio socioAtual,
            Socio socioFinal
    ) {

        User usuario = luckPerms.getUserManager().getUser(uuid);

        if (usuario == null) {
            return;
        }

        if (socioAtual != null) {

            InheritanceNode grupoAtual = InheritanceNode.builder(
                    socioAtual.getGrupoLuckPerms()
            ).build();

            usuario.data().remove(grupoAtual);
        }

        InheritanceNode novoGrupo = InheritanceNode.builder(
                socioFinal.getGrupoLuckPerms()
        ).build();

        usuario.data().add(novoGrupo);

        luckPerms.getUserManager().saveUser(usuario);
    }

    private long converterTempo(String tempo) {

        try {

            String valor = tempo.substring(
                    0,
                    tempo.length() - 1
            );

            char unidade = tempo.charAt(
                    tempo.length() - 1
            );

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
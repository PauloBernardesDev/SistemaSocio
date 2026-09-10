package dev.paulobernardes.sistemasocio.comandos;

import dev.paulobernardes.sistemasocio.gerenciadores.GerenciadorKeys;
import dev.paulobernardes.sistemasocio.gerenciadores.GerenciadorSocios;
import dev.paulobernardes.sistemasocio.modelos.Socio;
import dev.paulobernardes.sistemasocio.utilidades.Mensagens;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.security.SecureRandom;

public class GerarKeyComando implements CommandExecutor {

    private final GerenciadorKeys gerenciadorKeys;
    private final GerenciadorSocios gerenciadorSocios;

    private static final String CARACTERES =
            "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

    private final SecureRandom aleatorio = new SecureRandom();

    public GerarKeyComando(
            GerenciadorKeys gerenciadorKeys,
            GerenciadorSocios gerenciadorSocios
    ) {
        this.gerenciadorKeys = gerenciadorKeys;
        this.gerenciadorSocios = gerenciadorSocios;
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
                    Mensagens.prefixo(
                            "&cUso correto: &f/gerarkey <socio> <quantidade> <duracao>"
                    )
            );
            return true;
        }

        String tipoSocio = argumentos[0].toLowerCase();

        Socio socio = gerenciadorSocios.getSocio(tipoSocio);

        if (socio == null) {
            remetente.sendMessage(Mensagens.linha());
            remetente.sendMessage(Mensagens.titulo("Erro"));
            remetente.sendMessage("");
            remetente.sendMessage(
                    Mensagens.formatar(
                            "&cO tipo de Sócio &f" + tipoSocio + " &cnão existe."
                    )
            );
            remetente.sendMessage("");
            remetente.sendMessage(Mensagens.linha());
            return true;
        }

        int quantidade;

        try {

            quantidade = Integer.parseInt(argumentos[1]);

        } catch (NumberFormatException erro) {

            remetente.sendMessage(
                    Mensagens.prefixo("&cA quantidade precisa ser um número.")
            );

            return true;
        }

        if (quantidade <= 0) {
            remetente.sendMessage(
                    Mensagens.prefixo("&cA quantidade precisa ser maior que zero.")
            );
            return true;
        }

        if (quantidade > 1000) {
            remetente.sendMessage(
                    Mensagens.prefixo("&cVocê pode gerar no máximo 1000 Keys por vez.")
            );
            return true;
        }

        String duracao = argumentos[2];

        if (!duracaoValida(duracao)) {
            remetente.sendMessage(Mensagens.linha());
            remetente.sendMessage(Mensagens.titulo("Erro"));
            remetente.sendMessage("");
            remetente.sendMessage(
                    Mensagens.formatar("&cDuração inválida.")
            );
            remetente.sendMessage(
                    Mensagens.formatar(
                            "&7Exemplos: &f30d &7, &f7d &7, &f12h &7ou &f60m"
                    )
            );
            remetente.sendMessage("");
            remetente.sendMessage(Mensagens.linha());
            return true;
        }

        remetente.sendMessage(Mensagens.linha());
        remetente.sendMessage(Mensagens.titulo("Keys geradas"));
        remetente.sendMessage("");

        for (int i = 0; i < quantidade; i++) {

            String chave = gerarChave(tipoSocio);

            gerenciadorKeys.criarKey(
                    chave,
                    tipoSocio,
                    duracao
            );

            remetente.sendMessage(
                    Mensagens.formatar("&d" + chave)
            );
        }

        remetente.sendMessage("");
        remetente.sendMessage(
                Mensagens.formatar("&7Tipo: &f" + socio.getNome())
        );
        remetente.sendMessage(
                Mensagens.formatar("&7Quantidade: &f" + quantidade)
        );
        remetente.sendMessage(
                Mensagens.formatar("&7Duração: &f" + duracao)
        );
        remetente.sendMessage("");
        remetente.sendMessage(
                Mensagens.formatar("&a✓ Keys geradas com sucesso!")
        );
        remetente.sendMessage("");
        remetente.sendMessage(Mensagens.linha());

        return true;
    }

    private String gerarChave(String tipoSocio) {

        String prefixo = tipoSocio
                .replace("-", "")
                .toUpperCase();

        String parte1 = gerarParte();
        String parte2 = gerarParte();
        String parte3 = gerarParte();

        return prefixo + "-" + parte1 + "-" + parte2 + "-" + parte3;
    }

    private String gerarParte() {

        StringBuilder resultado = new StringBuilder();

        for (int i = 0; i < 4; i++) {
            resultado.append(
                    CARACTERES.charAt(
                            aleatorio.nextInt(CARACTERES.length())
                    )
            );
        }

        return resultado.toString();
    }

    private boolean duracaoValida(String duracao) {

        if (duracao == null || duracao.length() < 2) {
            return false;
        }

        try {

            long numero = Long.parseLong(
                    duracao.substring(0, duracao.length() - 1)
            );

            char unidade = duracao.charAt(
                    duracao.length() - 1
            );

            if (numero <= 0) {
                return false;
            }

            return unidade == 's'
                    || unidade == 'm'
                    || unidade == 'h'
                    || unidade == 'd';

        } catch (NumberFormatException erro) {
            return false;
        }
    }
}
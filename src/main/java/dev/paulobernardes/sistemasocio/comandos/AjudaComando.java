package dev.paulobernardes.sistemasocio.comandos;

import dev.paulobernardes.sistemasocio.utilidades.Mensagens;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class AjudaComando implements CommandExecutor {

    @Override
    public boolean onCommand(
            CommandSender remetente,
            Command comando,
            String rotulo,
            String[] argumentos
    ) {

        remetente.sendMessage(Mensagens.linha());
        remetente.sendMessage(Mensagens.titulo("Ajuda"));
        remetente.sendMessage("");

        remetente.sendMessage(Mensagens.formatar("&d/socio &8- &fAbre o menu de Sócios."));

        if (remetente.isOp() || remetente.hasPermission("sistemasocio.admin")) {

            remetente.sendMessage("");
            remetente.sendMessage(Mensagens.formatar("&5&lComandos administrativos"));
            remetente.sendMessage("");

            remetente.sendMessage(
                    Mensagens.formatar("&d/setsocio <jogador> <socio> <tempo>")
            );

            remetente.sendMessage(
                    Mensagens.formatar("  &8» &fDefine um Sócio para um jogador.")
            );

            remetente.sendMessage(
                    Mensagens.formatar("  &8» &7Exemplo: &f/setsocio Paulo socio 30d")
            );

            remetente.sendMessage("");

            remetente.sendMessage(
                    Mensagens.formatar("&7Tempo:")
            );

            remetente.sendMessage(
                    Mensagens.formatar("  &8» &fs &7= segundos")
            );

            remetente.sendMessage(
                    Mensagens.formatar("  &8» &fm &7= minutos")
            );

            remetente.sendMessage(
                    Mensagens.formatar("  &8» &fh &7= horas")
            );

            remetente.sendMessage(
                    Mensagens.formatar("  &8» &fd &7= dias")
            );

            remetente.sendMessage(
                    Mensagens.formatar("&d/removesocio <jogador>")
            );

            remetente.sendMessage(
                    Mensagens.formatar("  &8» &fRemove o Sócio de um jogador.")
            );

            remetente.sendMessage("");
        }

        remetente.sendMessage("");
        remetente.sendMessage(Mensagens.linha());

        return true;
    }
}
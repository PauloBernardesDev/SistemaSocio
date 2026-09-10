package dev.paulobernardes.sistemasocio.eventos;

import dev.paulobernardes.sistemasocio.comandos.KeyComando;
import dev.paulobernardes.sistemasocio.comandos.SocioComando;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SocioMenuListener implements Listener {

    private final JavaPlugin plugin;
    private final KeyComando keyComando;
    private final SocioComando socioComando;
    private final Set<UUID> aguardandoKey = new HashSet<>();

    private final String titulo =
            ChatColor.DARK_PURPLE + "Sistema de Sócio";

    private final String tituloBeneficios =
            ChatColor.DARK_PURPLE + "Benefícios";

    public SocioMenuListener(
            JavaPlugin plugin,
            KeyComando keyComando,
            SocioComando socioComando
    ) {
        this.plugin = plugin;
        this.keyComando = keyComando;
        this.socioComando = socioComando;
    }

    @EventHandler
    public void aoClicar(InventoryClickEvent evento) {

        String tituloInventario = evento.getView().getTitle();

        if (!tituloInventario.equals(titulo) &&
                !tituloInventario.equals(tituloBeneficios)) {
            return;
        }

        evento.setCancelled(true);

        if (!(evento.getWhoClicked() instanceof Player jogador)) {
            return;
        }

        if (tituloInventario.equals(tituloBeneficios)) {

            int slot = evento.getRawSlot();
            int tamanho = evento.getView().getTopInventory().getSize();

            if (slot >= tamanho) {
                return;
            }

            int slotVoltar = tamanho - 5;

            if (slot == slotVoltar) {
                socioComando.abrirMenuPrincipal(jogador);
            }

            return;
        }

        if (evento.getRawSlot() == 11) {
            socioComando.abrirMenuBeneficios(jogador);
            return;
        }

        if (evento.getRawSlot() != 10) {
            return;
        }

        UUID uuid = jogador.getUniqueId();

        if (aguardandoKey.contains(uuid)) {
            return;
        }

        aguardandoKey.add(uuid);

        jogador.closeInventory();

        jogador.sendMessage("");
        jogador.sendMessage(
                ChatColor.GREEN + "Digite sua Key no chat:"
        );
        jogador.sendMessage(
                ChatColor.GRAY + "Digite " +
                        ChatColor.RED + "cancelar" +
                        ChatColor.GRAY + " para cancelar."
        );
        jogador.sendMessage("");
    }

    @EventHandler
    public void aoArrastar(InventoryDragEvent evento) {

        String tituloInventario = evento.getView().getTitle();

        if (!tituloInventario.equals(titulo) &&
                !tituloInventario.equals(tituloBeneficios)) {
            return;
        }

        evento.setCancelled(true);
    }

    @EventHandler
    public void aoDigitarKey(AsyncPlayerChatEvent evento) {

        Player jogador = evento.getPlayer();
        UUID uuid = jogador.getUniqueId();

        if (!aguardandoKey.contains(uuid)) {
            return;
        }

        evento.setCancelled(true);

        String mensagem = evento.getMessage().trim();

        aguardandoKey.remove(uuid);

        if (mensagem.equalsIgnoreCase("cancelar")) {

            jogador.sendMessage(
                    ChatColor.YELLOW + "Ativação da Key cancelada."
            );

            return;
        }

        Bukkit.getScheduler().runTask(
                plugin,
                () -> keyComando.ativarKey(jogador, mensagem)
        );
    }
}
package dev.paulobernardes.sistemasocio.comandos;

import dev.paulobernardes.sistemasocio.gerenciadores.GerenciadorJogadores;
import dev.paulobernardes.sistemasocio.gerenciadores.GerenciadorSocios;
import dev.paulobernardes.sistemasocio.modelos.Socio;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SocioComando implements CommandExecutor {

    private final GerenciadorSocios gerenciadorSocios;
    private final GerenciadorJogadores gerenciadorJogadores;

    public SocioComando(
            GerenciadorSocios gerenciadorSocios,
            GerenciadorJogadores gerenciadorJogadores
    ) {
        this.gerenciadorSocios = gerenciadorSocios;
        this.gerenciadorJogadores = gerenciadorJogadores;
    }

    @Override
    public boolean onCommand(
            CommandSender remetente,
            Command comando,
            String rotulo,
            String[] argumentos
    ) {

        if (!(remetente instanceof Player jogador)) {
            remetente.sendMessage("Este comando só pode ser usado por jogadores.");
            return true;
        }

        abrirMenuPrincipal(jogador);

        return true;
    }

    public void abrirMenuPrincipal(Player jogador) {

        Inventory menu = Bukkit.createInventory(
                null,
                27,
                ChatColor.DARK_PURPLE + "Sistema de Sócio"
        );

        ItemStack ativarSocio = new ItemStack(Material.NAME_TAG);
        ItemMeta metaAtivarSocio = ativarSocio.getItemMeta();

        if (metaAtivarSocio != null) {

            metaAtivarSocio.setDisplayName(
                    ChatColor.GREEN + "Ativar Sócio"
            );

            metaAtivarSocio.setLore(List.of(
                    ChatColor.GRAY + "Ative seu Sócio através",
                    ChatColor.GRAY + "de uma Key.",
                    "",
                    ChatColor.YELLOW + "Clique para ativar"
            ));

            ativarSocio.setItemMeta(metaAtivarSocio);
        }

        menu.setItem(10, ativarSocio);

        ItemStack beneficios = new ItemStack(Material.CHEST);
        ItemMeta metaBeneficios = beneficios.getItemMeta();

        if (metaBeneficios != null) {

            metaBeneficios.setDisplayName(
                    ChatColor.GREEN + "Benefícios"
            );

            metaBeneficios.setLore(List.of(
                    ChatColor.GRAY + "Veja os benefícios disponíveis",
                    ChatColor.GRAY + "de cada tipo de Sócio.",
                    "",
                    ChatColor.YELLOW + "Clique para consultar"
            ));

            beneficios.setItemMeta(metaBeneficios);
        }

        menu.setItem(11, beneficios);

        ItemStack upgrade = new ItemStack(Material.DIAMOND);
        ItemMeta metaUpgrade = upgrade.getItemMeta();

        if (metaUpgrade != null) {

            metaUpgrade.setDisplayName(
                    ChatColor.AQUA + "Upgrade para Sócio+"
            );

            metaUpgrade.setLore(List.of(
                    ChatColor.GRAY + "Possui Sócio e quer evoluir",
                    ChatColor.GRAY + "para o Sócio+?",
                    "",
                    ChatColor.GRAY + "Você pode ativar um Sócio+",
                    ChatColor.GRAY + "e aproveitar o tempo que já possui.",
                    "",
                    ChatColor.GRAY + "O tempo será acumulado e seu",
                    ChatColor.GRAY + "Sócio será convertido para Sócio+.",
                    "",
                    ChatColor.AQUA + "Exemplo:",
                    ChatColor.WHITE + "2x Sócio + 1x Sócio+",
                    ChatColor.GRAY + "= 90 dias de Sócio+"
            ));

            upgrade.setItemMeta(metaUpgrade);
        }

        menu.setItem(13, upgrade);

        UUID uuid = jogador.getUniqueId();

        ItemStack meuSocio = new ItemStack(Material.CLOCK);
        ItemMeta metaMeuSocio = meuSocio.getItemMeta();

        if (metaMeuSocio != null) {

            metaMeuSocio.setDisplayName(
                    ChatColor.GOLD + "Meu Sócio"
            );

            List<String> loreMeuSocio = new ArrayList<>();

            if (gerenciadorJogadores.temSocio(uuid)) {

                String tipoSocio = gerenciadorJogadores.getTipoSocio(uuid);
                Socio socio = gerenciadorSocios.getSocio(tipoSocio);

                long expiracao = gerenciadorJogadores.getExpiracao(uuid);

                long restante = Math.max(
                        0,
                        expiracao - System.currentTimeMillis()
                );

                long horasTotais = restante / 3600000;

                long dias = horasTotais / 24;
                long horas = horasTotais % 24;

                String nomeSocio = socio != null
                        ? ChatColor.translateAlternateColorCodes(
                        '&',
                        socio.getNome()
                )
                        : tipoSocio;

                loreMeuSocio.add(
                        ChatColor.GRAY + "Sócio atual: " +
                                ChatColor.WHITE + nomeSocio
                );

                loreMeuSocio.add("");

                loreMeuSocio.add(
                        ChatColor.GRAY + "Tempo restante:"
                );

                if (dias > 0) {

                    loreMeuSocio.add(
                            ChatColor.WHITE + String.valueOf(dias) +
                                    ChatColor.GRAY + " dias e " +
                                    ChatColor.WHITE + String.valueOf(horas) +
                                    ChatColor.GRAY + " horas"
                    );

                } else {

                    loreMeuSocio.add(
                            ChatColor.WHITE + String.valueOf(horas) +
                                    ChatColor.GRAY + " horas"
                    );
                }

                loreMeuSocio.add("");

                loreMeuSocio.add(
                        ChatColor.GRAY + "Status: " +
                                ChatColor.GREEN + "Ativo"
                );

            } else {

                loreMeuSocio.add(
                        ChatColor.GRAY + "Você não possui um"
                );

                loreMeuSocio.add(
                        ChatColor.GRAY + "Sócio ativo."
                );

                loreMeuSocio.add("");

                loreMeuSocio.add(
                        ChatColor.YELLOW + "Ative um Sócio para começar!"
                );
            }

            metaMeuSocio.setLore(loreMeuSocio);
            meuSocio.setItemMeta(metaMeuSocio);
        }

        menu.setItem(16, meuSocio);

        jogador.openInventory(menu);
    }

    public void abrirMenuBeneficios(Player jogador) {

        int quantidadeSocios = gerenciadorSocios.getSocios().size();

        int linhas = Math.max(
                3,
                Math.min(
                        6,
                        (int) Math.ceil(quantidadeSocios / 7.0) + 1
                )
        );

        Inventory menu = Bukkit.createInventory(
                null,
                linhas * 9,
                ChatColor.DARK_PURPLE + "Benefícios"
        );

        int[] slots = {
                10, 11, 12, 13, 14, 15, 16,
                19, 20, 21, 22, 23, 24, 25,
                28, 29, 30, 31, 32, 33, 34,
                37, 38, 39, 40, 41, 42, 43
        };

        int indice = 0;

        for (Socio socio : gerenciadorSocios.getSocios().values()) {

            if (indice >= slots.length) {
                break;
            }

            ItemStack item = new ItemStack(Material.CHEST);
            ItemMeta meta = item.getItemMeta();

            if (meta == null) {
                continue;
            }

            meta.setDisplayName(
                    ChatColor.translateAlternateColorCodes(
                            '&',
                            socio.getNome()
                    )
            );

            List<String> lore = new ArrayList<>();

            lore.add(
                    ChatColor.GRAY + "Benefícios disponíveis:"
            );

            lore.add("");

            for (String beneficio : socio.getBeneficios()) {

                lore.add(
                        ChatColor.GRAY + "• " +
                                ChatColor.translateAlternateColorCodes(
                                        '&',
                                        beneficio
                                )
                );
            }

            meta.setLore(lore);
            item.setItemMeta(meta);

            menu.setItem(slots[indice], item);

            indice++;
        }

        ItemStack voltar = new ItemStack(Material.ARROW);
        ItemMeta metaVoltar = voltar.getItemMeta();

        if (metaVoltar != null) {

            metaVoltar.setDisplayName(
                    ChatColor.YELLOW + "Voltar"
            );

            metaVoltar.setLore(List.of(
                    ChatColor.GRAY + "Voltar para o menu principal."
            ));

            voltar.setItemMeta(metaVoltar);
        }

        menu.setItem((linhas - 1) * 9 + 4, voltar);

        jogador.openInventory(menu);
    }
}
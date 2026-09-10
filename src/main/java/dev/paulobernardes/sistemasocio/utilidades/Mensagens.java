package dev.paulobernardes.sistemasocio.utilidades;

import org.bukkit.ChatColor;

public class Mensagens {

    private static final String PREFIXO = "&5SistemaSocio &8» &r";

    public static String formatar(String mensagem) {
        return ChatColor.translateAlternateColorCodes('&', mensagem);
    }

    public static String prefixo(String mensagem) {
        return formatar(PREFIXO + mensagem);
    }

    public static String linha() {
        return formatar("&8&m----------------------------------------");
    }

    public static String titulo(String titulo) {
        return formatar("&5&lSistemaSocio &8» &d" + titulo);
    }
}
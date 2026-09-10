package dev.paulobernardes.sistemasocio.modelos;

import java.util.List;

public class Socio {

    private final String id;
    private final String nome;
    private final String grupoLuckPerms;
    private final int nivel;
    private final double preco;
    private final double dinheiroAtivacao;
    private final String duracao;
    private final boolean ativado;
    private final List<String> beneficios;

    public Socio(
            String id,
            String nome,
            String grupoLuckPerms,
            int nivel,
            double preco,
            double dinheiroAtivacao,
            String duracao,
            boolean ativado,
            List<String> beneficios
    ) {
        this.id = id;
        this.nome = nome;
        this.grupoLuckPerms = grupoLuckPerms;
        this.nivel = nivel;
        this.preco = preco;
        this.dinheiroAtivacao = dinheiroAtivacao;
        this.duracao = duracao;
        this.ativado = ativado;
        this.beneficios = beneficios;
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getGrupoLuckPerms() {
        return grupoLuckPerms;
    }

    public int getNivel() {
        return nivel;
    }

    public double getPreco() {
        return preco;
    }

    public double getDinheiroAtivacao() {
        return dinheiroAtivacao;
    }

    public String getDuracao() {
        return duracao;
    }

    public boolean isAtivado() {
        return ativado;
    }

    public List<String> getBeneficios() {
        return beneficios;
    }
}
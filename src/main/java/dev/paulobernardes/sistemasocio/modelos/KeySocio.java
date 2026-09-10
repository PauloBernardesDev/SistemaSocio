package dev.paulobernardes.sistemasocio.modelos;

public class KeySocio {

    private final String chave;
    private final String tipoSocio;
    private final String duracao;
    private final boolean utilizada;
    private final String jogadorUuid;
    private final long criadaEm;
    private final long utilizadaEm;

    public KeySocio(
            String chave,
            String tipoSocio,
            String duracao,
            boolean utilizada,
            String jogadorUuid,
            long criadaEm,
            long utilizadaEm
    ) {
        this.chave = chave;
        this.tipoSocio = tipoSocio;
        this.duracao = duracao;
        this.utilizada = utilizada;
        this.jogadorUuid = jogadorUuid;
        this.criadaEm = criadaEm;
        this.utilizadaEm = utilizadaEm;
    }

    public String getChave() {
        return chave;
    }

    public String getTipoSocio() {
        return tipoSocio;
    }

    public String getDuracao() {
        return duracao;
    }

    public boolean isUtilizada() {
        return utilizada;
    }

    public String getJogadorUuid() {
        return jogadorUuid;
    }

    public long getCriadaEm() {
        return criadaEm;
    }

    public long getUtilizadaEm() {
        return utilizadaEm;
    }
}
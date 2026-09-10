package dev.paulobernardes.sistemasocio.gerenciadores;

import java.util.HashMap;
import java.util.Map;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class GerenciadorJogadores {

    private final GerenciadorBanco banco;

    public GerenciadorJogadores(GerenciadorBanco banco) {
        this.banco = banco;
    }

    public void ativarSocio(UUID uuid, String tipoSocio, long ativadoEm, long expiraEm) {

        String sql = """
                INSERT INTO socios_jogadores (uuid, tipo_socio, ativado_em, expira_em)
                VALUES (?, ?, ?, ?)
                ON CONFLICT(uuid) DO UPDATE SET
                    tipo_socio = excluded.tipo_socio,
                    ativado_em = excluded.ativado_em,
                    expira_em = excluded.expira_em
                """;

        try (PreparedStatement statement = banco.getConexao().prepareStatement(sql)) {

            statement.setString(1, uuid.toString());
            statement.setString(2, tipoSocio);
            statement.setLong(3, ativadoEm);
            statement.setLong(4, expiraEm);

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar o Sócio do jogador.", e);
        }
    }

    public void ativarOuRenovarSocio(UUID uuid, String tipoSocio, long duracao) {

        long agora = System.currentTimeMillis();
        long expiracaoAtual = getExpiracao(uuid);

        long novaExpiracao;

        if (expiracaoAtual > agora) {
            novaExpiracao = expiracaoAtual + duracao;
        } else {
            novaExpiracao = agora + duracao;
        }

        ativarSocio(
                uuid,
                tipoSocio,
                agora,
                novaExpiracao
        );
    }

    public boolean temSocio(UUID uuid) {

        String sql = "SELECT uuid FROM socios_jogadores WHERE uuid = ? AND expira_em > ?";

        try (PreparedStatement statement = banco.getConexao().prepareStatement(sql)) {

            statement.setString(1, uuid.toString());
            statement.setLong(2, System.currentTimeMillis());

            try (ResultSet resultado = statement.executeQuery()) {
                return resultado.next();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar o Sócio do jogador.", e);
        }
    }

    public String getTipoSocio(UUID uuid) {

        String sql = "SELECT tipo_socio FROM socios_jogadores WHERE uuid = ?";

        try (PreparedStatement statement = banco.getConexao().prepareStatement(sql)) {

            statement.setString(1, uuid.toString());

            try (ResultSet resultado = statement.executeQuery()) {

                if (resultado.next()) {
                    return resultado.getString("tipo_socio");
                }

                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao consultar o Sócio do jogador.", e);
        }
    }

    public long getExpiracao(UUID uuid) {

        String sql = "SELECT expira_em FROM socios_jogadores WHERE uuid = ?";

        try (PreparedStatement statement = banco.getConexao().prepareStatement(sql)) {

            statement.setString(1, uuid.toString());

            try (ResultSet resultado = statement.executeQuery()) {

                if (resultado.next()) {
                    return resultado.getLong("expira_em");
                }

                return 0;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao consultar a expiração do Sócio.", e);
        }
    }

    public void removerSocio(UUID uuid) {

        String sql = "DELETE FROM socios_jogadores WHERE uuid = ?";

        try (PreparedStatement statement = banco.getConexao().prepareStatement(sql)) {

            statement.setString(1, uuid.toString());
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao remover o Sócio do jogador.", e);
        }
    }

    public Map<UUID, String> getSociosExpirados() {

        Map<UUID, String> expirados = new HashMap<>();

        String sql = """
            SELECT uuid, tipo_socio
            FROM socios_jogadores
            WHERE expira_em <= ?
            """;

        try (PreparedStatement statement = banco.getConexao().prepareStatement(sql)) {

            statement.setLong(1, System.currentTimeMillis());

            try (ResultSet resultado = statement.executeQuery()) {

                while (resultado.next()) {

                    UUID uuid = UUID.fromString(
                            resultado.getString("uuid")
                    );

                    String tipoSocio = resultado.getString("tipo_socio");

                    expirados.put(uuid, tipoSocio);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erro ao consultar Sócios expirados.",
                    e
            );
        }

        return expirados;
    }

    public int pausarSocios() {

        long agora = System.currentTimeMillis();

        String sql = """
            UPDATE socios_jogadores
            SET pausado = 1,
                tempo_restante = expira_em - ?
            WHERE pausado = 0
            """;

        try (PreparedStatement statement = banco.getConexao().prepareStatement(sql)) {

            statement.setLong(1, agora);

            return statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erro ao pausar os Sócios.",
                    e
            );
        }
    }

    public int retomarSocios() {

        long agora = System.currentTimeMillis();

        String sql = """
            UPDATE socios_jogadores
            SET pausado = 0,
                expira_em = ? + tempo_restante,
                tempo_restante = 0
            WHERE pausado = 1
            """;

        try (PreparedStatement statement = banco.getConexao().prepareStatement(sql)) {

            statement.setLong(1, agora);

            return statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erro ao retomar os Sócios.",
                    e
            );
        }
    }
}
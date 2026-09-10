package dev.paulobernardes.sistemasocio.gerenciadores;

import dev.paulobernardes.sistemasocio.modelos.KeySocio;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class GerenciadorKeys {

    private final GerenciadorBanco banco;

    public GerenciadorKeys(GerenciadorBanco banco) {
        this.banco = banco;
    }

    public void criarKey(String chave, String tipoSocio, String duracao) {

        String sql = """
                INSERT INTO chaves
                (chave, tipo_socio, duracao, criada_em)
                VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement statement = banco.getConexao().prepareStatement(sql)) {

            statement.setString(1, chave);
            statement.setString(2, tipoSocio);
            statement.setString(3, duracao);
            statement.setLong(4, System.currentTimeMillis());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar a Key.", e);
        }
    }

    public KeySocio buscarKey(String chave) {

        String sql = """
                SELECT chave, tipo_socio, duracao, utilizada,
                       utilizada_por, criada_em, utilizada_em
                FROM chaves
                WHERE chave = ?
                """;

        try (PreparedStatement statement = banco.getConexao().prepareStatement(sql)) {

            statement.setString(1, chave);

            try (ResultSet resultado = statement.executeQuery()) {

                if (!resultado.next()) {
                    return null;
                }

                String jogadorUuid = resultado.getString("utilizada_por");

                return new KeySocio(
                        resultado.getString("chave"),
                        resultado.getString("tipo_socio"),
                        resultado.getString("duracao"),
                        resultado.getInt("utilizada") == 1,
                        jogadorUuid,
                        resultado.getLong("criada_em"),
                        resultado.getLong("utilizada_em")
                );
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar a Key.", e);
        }
    }

    public boolean utilizarKey(String chave, UUID jogadorUuid) {

        String sql = """
                UPDATE chaves
                SET utilizada = 1,
                    utilizada_por = ?,
                    utilizada_em = ?
                WHERE chave = ?
                AND utilizada = 0
                """;

        try (PreparedStatement statement = banco.getConexao().prepareStatement(sql)) {

            statement.setString(1, jogadorUuid.toString());
            statement.setLong(2, System.currentTimeMillis());
            statement.setString(3, chave);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao utilizar a Key.", e);
        }
    }
}
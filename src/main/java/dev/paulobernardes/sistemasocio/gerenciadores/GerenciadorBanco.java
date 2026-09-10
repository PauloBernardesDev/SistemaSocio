package dev.paulobernardes.sistemasocio.gerenciadores;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class GerenciadorBanco {

    private final JavaPlugin plugin;
    private Connection conexao;

    public GerenciadorBanco(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void conectar() {

        try {
            File pastaDados = plugin.getDataFolder();

            if (!pastaDados.exists()) {
                pastaDados.mkdirs();
            }

            File arquivoBanco = new File(pastaDados, "socios.db");

            conexao = DriverManager.getConnection(
                    "jdbc:sqlite:" + arquivoBanco.getAbsolutePath()
            );

            criarTabelas();

            plugin.getLogger().info("SistemaSocio » Banco de dados conectado.");

        } catch (SQLException e) {
            plugin.getLogger().severe("Não foi possível conectar ao banco de dados.");
            e.printStackTrace();
        }
    }

    private void criarTabelas() {

        String tabelaSocios = """
            CREATE TABLE IF NOT EXISTS socios_jogadores (
                uuid TEXT PRIMARY KEY,
                tipo_socio TEXT NOT NULL,
                ativado_em INTEGER NOT NULL,
                expira_em INTEGER NOT NULL,
                pausado INTEGER NOT NULL DEFAULT 0,
                tempo_restante INTEGER NOT NULL DEFAULT 0
            )
            """;

        String tabelaChaves = """
            CREATE TABLE IF NOT EXISTS chaves (
                chave TEXT PRIMARY KEY,
                tipo_socio TEXT NOT NULL,
                duracao TEXT NOT NULL,
                criada_em INTEGER NOT NULL,
                utilizada INTEGER NOT NULL DEFAULT 0,
                utilizada_por TEXT,
                utilizada_em INTEGER
            )
            """;

        try (Statement statement = conexao.createStatement()) {

            statement.executeUpdate(tabelaSocios);
            statement.executeUpdate(tabelaChaves);

            try {
                statement.executeUpdate(
                        "ALTER TABLE socios_jogadores ADD COLUMN pausado INTEGER NOT NULL DEFAULT 0"
                );
            } catch (SQLException ignored) {
            }

            try {
                statement.executeUpdate(
                        "ALTER TABLE socios_jogadores ADD COLUMN tempo_restante INTEGER NOT NULL DEFAULT 0"
                );
            } catch (SQLException ignored) {
            }

        } catch (SQLException e) {

            plugin.getLogger().severe(
                    "Não foi possível criar as tabelas do banco de dados."
            );

            e.printStackTrace();
        }
    }

    public void desconectar() {

        if (conexao == null) {
            return;
        }

        try {

            if (!conexao.isClosed()) {

                conexao.close();

                plugin.getLogger().info(
                        "SistemaSocio » Banco de dados desconectado."
                );
            }

        } catch (SQLException e) {

            plugin.getLogger().severe(
                    "Não foi possível fechar o banco de dados."
            );

            e.printStackTrace();
        }
    }

    public Connection getConexao() {
        return conexao;
    }
}
/**
 * @file ConexionBD.java
 * @brief Creación de DataSource y ejecución de migraciones.
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.unicauca.iso2.bancopreguntas.access.db;

import org.flywaydb.core.Flyway;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;

/**
 * @brief Fábrica de DataSource (PostgreSQL o H2) y migraciones Flyway.
 *
 * La configuración de PostgreSQL se lee de las variables de entorno
 * DB_HOST, DB_PORT, DB_NAME, DB_USER y DB_PASSWORD.
 */
public final class ConexionBD {

    private ConexionBD() {
    }

    /**
     * @brief DataSource de PostgreSQL con los parámetros dados.
     * @param host      servidor
     * @param puerto    puerto
     * @param baseDatos nombre de la base de datos
     * @param usuario   usuario
     * @param password  contraseña
     * @return DataSource configurado
     */
    public static DataSource crearPostgres(String host, int puerto, String baseDatos,
                                            String usuario, String password) {
        PGSimpleDataSource ds = new PGSimpleDataSource();
        ds.setServerNames(new String[]{host});
        ds.setPortNumbers(new int[]{puerto});
        ds.setDatabaseName(baseDatos);
        ds.setUser(usuario);
        ds.setPassword(password);
        return ds;
    }

    /**
     * @brief DataSource de PostgreSQL leído de variables de entorno.
     * @return DataSource configurado
     */
    public static DataSource crearPostgresDesdeEnv() {
        String host     = valorODefecto(System.getenv("DB_HOST"), "localhost");
        int puerto      = parsePuerto(System.getenv("DB_PORT"), 5432);
        String baseDatos = valorODefecto(System.getenv("DB_NAME"), "banco_preguntas");
        String usuario  = valorODefecto(System.getenv("DB_USER"), "postgres");
        String password = valorODefecto(System.getenv("DB_PASSWORD"), "");
        return crearPostgres(host, puerto, baseDatos, usuario, password);
    }

    /**
     * @brief DataSource H2 en memoria (modo PostgreSQL) para pruebas.
     * @param nombreBaseDatos cada nombre distinto crea una base aislada
     * @return DataSource configurado
     */
    public static DataSource crearH2ParaTest(String nombreBaseDatos) {
        org.h2.jdbcx.JdbcDataSource ds = new org.h2.jdbcx.JdbcDataSource();
        ds.setURL("jdbc:h2:mem:" + nombreBaseDatos
                + ";DB_CLOSE_DELAY=-1;MODE=PostgreSQL");
        ds.setUser("sa");
        ds.setPassword("");
        return ds;
    }

    /**
     * @brief Aplica las migraciones pendientes de db/migration.
     * @param dataSource base de datos destino
     */
    public static void migrar(DataSource dataSource) {
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .load();
        flyway.migrate();
    }

    private static String valorODefecto(String valor, String porDefecto) {
        return (valor != null && !valor.isBlank()) ? valor : porDefecto;
    }

    private static int parsePuerto(String valor, int porDefecto) {
        try {
            return (valor != null && !valor.isBlank())
                    ? Integer.parseInt(valor) : porDefecto;
        } catch (NumberFormatException e) {
            return porDefecto;
        }
    }
}

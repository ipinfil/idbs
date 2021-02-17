package main;

import java.io.*;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Creates database and generates data from sql files.
 * Used to enhance UX, so user can create database and generate data from Main Menu of the app.
 */
public class Initializer {
    private Initializer() {
    }

    public static void createDatabase() throws SQLException, IOException {
        BufferedReader br = new BufferedReader(new FileReader("sql" + File.separator + "createScript.sql"));
        StringBuffer sb = new StringBuffer();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
            sb.append(System.lineSeparator());
        }

        try (PreparedStatement ps = DbContext.getConnection().prepareStatement(sb.toString())) {
            System.out.println("Vytvaram databazu.");
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Nepodarilo sa vytvorit databazu.");
            throw e;
        }
    }

    public static void generateDatabase() throws SQLException, IOException {
        BufferedReader br = new BufferedReader(new FileReader("sql" + File.separator + "generateScript.sql"));
        StringBuffer sb = new StringBuffer();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
            sb.append(System.lineSeparator());
        }

        try (PreparedStatement ps = DbContext.getConnection().prepareStatement(sb.toString())) {
            System.out.println("Generujem data v databaze.");
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Nepodarilo sa vygenerovat data v databaze;");
            throw e;
        }
    }

}



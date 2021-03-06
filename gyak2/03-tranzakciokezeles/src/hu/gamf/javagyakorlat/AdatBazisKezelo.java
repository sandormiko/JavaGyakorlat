/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.gamf.javagyakorlat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 *
 * @author Mikó Sándor
 */
public class AdatBazisKezelo extends AbsztraktAdatbazisKezelo {

    private Connection conn = null;

    public AdatBazisKezelo(Connection connection) {
        this.conn = connection;
    }

    public ArrayList<Szamitas> getSzamitasok() throws SQLException {
        ArrayList<Szamitas> lekerdezettSzamitasok = new ArrayList<>();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            //Class.forName("org.hsqldb.jdbcDriver");

            stmt = conn.createStatement();
            rs = stmt.executeQuery("select szamitas_id,netto_osszeg,afa,brutto_osszeg,ado_ertek, letrehozas_ideje, szemely_id  from szamitas");
            while (rs.next()) {
                Szamitas sz = new Szamitas();
                sz.szamitasId = rs.getLong("szamitas_id");
                sz.nettoOsszeg = rs.getDouble("netto_osszeg");
                sz.afaErteke = AfaErtek.getEnum(rs.getDouble("afa"));
                sz.bruttoErtek = rs.getDouble("brutto_osszeg");
                sz.adoErtek = rs.getDouble("ado_ertek");
                sz.letrehozasIdeje = rs.getDate("letrehozas_ideje");
                sz.szemelyId = rs.getLong("szemely_id");
                lekerdezettSzamitasok.add(sz);
            }
            return lekerdezettSzamitasok;
        } catch (SQLException e) {
            System.err.println("Hiba történt a számítás mentése során");
            throw e;
        } finally {
            close(rs);
            close(stmt);
        }

    }

    public void mentSzamitast(Szamitas sz) throws SQLException {
        PreparedStatement ps = null;
        try {
            //Class.forName("org.hsqldb.jdbcDriver");

            ps = conn.prepareStatement("insert into szamitas("
                    + "netto_osszeg,afa,brutto_osszeg,ado_ertek, letrehozas_ideje, szemely_id) "
                    + "values(?,?,?,?,?,?)");

            ps.setDouble(1, sz.nettoOsszeg);
            ps.setDouble(2, sz.afaErteke.getErtek());
            ps.setDouble(3, sz.bruttoErtek);
            ps.setDouble(4, sz.adoErtek);
            ps.setDate(5, new java.sql.Date(System.currentTimeMillis()));
            ps.setLong(6, sz.szemelyId);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Hiba történt a számítások lekérdezése során");
            throw e;
        } finally {
            close(ps);
        }
    }

    

    public Szemely getSzemelyByAzonosito(String azonosito) throws SQLException {
        Szemely szemely = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            //Class.forName("org.hsqldb.jdbcDriver");

            ps = conn.prepareStatement("select szemely_id,azonosito,keresztnev,vezeteknev,szuletesi_ido   from szemely where azonosito=?");
            ps.setString(1, azonosito);
            rs = ps.executeQuery();
            if (rs.next()) {
                szemely = new Szemely();
                szemely.szemelyId = rs.getLong("szemely_id");
                szemely.azonosito = rs.getString("azonosito");
                szemely.keresztnev = rs.getString("keresztnev");
                szemely.vezeteknev = rs.getString("vezeteknev");
                szemely.szuletesiIdo = rs.getDate("szuletesi_ido");

            }
            return szemely;
        } catch (SQLException e) {
            System.err.println("Hiba történt a személy lekérdezése során");
            throw e;
        } finally {
            close(rs);
            close(ps);
        }

    }

    public void mentSzemelyt(Szemely sz) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            //Class.forName("org.hsqldb.jdbcDriver");

            ps = conn.prepareStatement("insert into szemely("
                    + "azonosito,keresztnev,vezeteknev,szuletesi_ido) "
                    + "values(?,?,?,?)", Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, sz.azonosito);
            ps.setString(2, sz.keresztnev);
            ps.setString(3, sz.vezeteknev);

            ps.setDate(4, new java.sql.Date(sz.szuletesiIdo.getTime()));
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                Long id = rs.getLong(1);
                sz.szemelyId = id;
            }

        } catch (SQLException e) {
            System.err.println("Hiba történt a személy mentése során");
            throw e;

        } finally {
            close(rs);
            close(ps);
        }
    }
}

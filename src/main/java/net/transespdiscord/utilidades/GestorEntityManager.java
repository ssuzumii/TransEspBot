package net.transespdiscord.utilidades;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;

public class GestorEntityManager {
    private static final EntityManagerFactory fabrica = construirFabrica();

    private static EntityManagerFactory construirFabrica(){
        HashMap<String, String> configuracion = new HashMap<>();
        configuracion.put("jakarta.persistence.jdbc.url", "jdbc:mysql://" + System.getenv("BUTTERFREE_DB_HOST") + "/"
                + System.getenv("BUTTERFREE_DB_NAME") + "?enabledTLSProtocols=TLSv1.2&autoReconnect=true&useUnicode=yes");
        configuracion.put("jakarta.persistence.jdbc.user", System.getenv("BUTTERFREE_DB_USER"));
        configuracion.put("jakarta.persistence.jdbc.password", System.getenv("BUTTERFREE_DB_PASS"));

        return Persistence.createEntityManagerFactory("unidad_persistencia", configuracion);
    }

    public static EntityManager getManager(){
        return fabrica.createEntityManager();
    }
}

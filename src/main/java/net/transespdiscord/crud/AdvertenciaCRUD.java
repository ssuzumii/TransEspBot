package net.transespdiscord.crud;

import jakarta.persistence.EntityManager;
import lombok.Cleanup;
import net.transespdiscord.entidades.Advertencia;
import net.transespdiscord.utilidades.GestorEntityManager;

import java.util.ArrayList;
import java.util.Comparator;


public class AdvertenciaCRUD {
    private static ArrayList<Advertencia> advertencias = new ArrayList<>();

    public static void insertar(Advertencia o) {
        @Cleanup EntityManager em = GestorEntityManager.getManager();

        try {
            em.getTransaction().begin();
            em.persist(o);
            em.getTransaction().commit();
            advertencias.add(o);
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        }
    }

    public static ArrayList<Advertencia> listar() {
        @Cleanup EntityManager em = GestorEntityManager.getManager();
        advertencias = new ArrayList<>(em.createQuery("SELECT o FROM Advertencia o", Advertencia.class).getResultList());
        return advertencias;
    }

    public static Advertencia obtenerPorId(int id) {
        Advertencia advertencia = null;

        for (Advertencia a : advertencias) {
            if (a.getId() == id) {
                advertencia = a;
                break;
            }
        }

        if (advertencia == null) {
            @Cleanup EntityManager em = GestorEntityManager.getManager();
            advertencia = em.find(Advertencia.class, id);
            advertencias.add(advertencia);
        }

        return advertencia;
    }

    public static ArrayList<Advertencia> obtenerPorIdUsuarie(String idUsuarie) {
        ArrayList<Advertencia> advertenciasResultado = new ArrayList<>();
        ArrayList<Advertencia> advertenciasBD;

        for (Advertencia a : advertencias) {
            if (a.getIdUsuarie().equals(idUsuarie)) {
                advertenciasResultado.add(a);
            }
        }

        @Cleanup EntityManager em = GestorEntityManager.getManager();
        advertenciasBD = new ArrayList<>(em.createQuery("SELECT o FROM Advertencia o WHERE idUsuarie = '" + idUsuarie + "'", Advertencia.class).getResultList());

        for (Advertencia a : advertenciasBD) {
            boolean encontrado = false;

            for (Advertencia a2 : advertenciasResultado) {
                if (a.getId().intValue() == a2.getId().intValue()) {
                    encontrado = true;
                    break;
                }
            }

            if (!encontrado) {
                advertenciasResultado.add(a);
            }
        }

        ordenarListaAdvertencias(advertenciasResultado);

        return advertenciasResultado;
    }

    public static void eliminar(Advertencia o) {
        @Cleanup EntityManager em = GestorEntityManager.getManager();

        try {
            em.getTransaction().begin();
            em.remove(em.contains(o) ? o : em.merge(o));
            em.getTransaction().commit();
            advertencias.remove(o);
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        }
    }

    private static void ordenarListaAdvertencias(ArrayList<Advertencia> lista) {
        lista.sort(Comparator.comparing(Advertencia::getId));
    }
}

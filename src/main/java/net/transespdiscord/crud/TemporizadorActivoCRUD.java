package net.transespdiscord.crud;

import jakarta.persistence.EntityManager;
import net.transespdiscord.entidades.TemporizadorActivo;
import net.transespdiscord.utilidades.GestorEntityManager;

import java.util.List;

public class TemporizadorActivoCRUD {
    public static void insertar(TemporizadorActivo o){
        EntityManager em = GestorEntityManager.getManager();

        try {
            em.getTransaction().begin();
            em.persist(o);
            em.getTransaction().commit();
        } catch (Exception e){
            em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }

    }

    public static List<TemporizadorActivo> listar(){
        EntityManager em = GestorEntityManager.getManager();
        return em.createQuery("SELECT o FROM TemporizadorActivo o", TemporizadorActivo.class).getResultList();
    }

    public static TemporizadorActivo obtenerPorId(int id){
        EntityManager em = GestorEntityManager.getManager();
        return em.find(TemporizadorActivo.class, id);
    }

    public static List<TemporizadorActivo> obtenerPorIdSolicitante(String idSolicitante){
        EntityManager em = GestorEntityManager.getManager();
        return em.createQuery("SELECT o FROM TemporizadorActivo o WHERE idSolicitante = '" + idSolicitante + "'", TemporizadorActivo.class).getResultList();
    }

    public static void actualizar(TemporizadorActivo o){
        EntityManager em = GestorEntityManager.getManager();

        try {
            em.getTransaction().begin();
            em.merge(o);
            em.getTransaction().commit();
        } catch (Exception e){
            em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public static void eliminar(TemporizadorActivo o){
        EntityManager em = GestorEntityManager.getManager();

        try {
            em.getTransaction().begin();
            em.remove(em.contains(o) ? o : em.merge(o));
            em.getTransaction().commit();
        } catch (Exception e){
            em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}

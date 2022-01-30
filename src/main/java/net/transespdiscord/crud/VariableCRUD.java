package net.transespdiscord.crud;

import jakarta.persistence.EntityManager;
import net.transespdiscord.entidades.Variable;
import net.transespdiscord.utilidades.GestorEntityManager;

import java.util.List;

public class VariableCRUD {
    public static void insertar(Variable o){
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

    public static List<Variable> listar(){
        EntityManager em = GestorEntityManager.getManager();
        return em.createQuery("SELECT o FROM Variable o", Variable.class).getResultList();
    }

    public static Variable obtenerPorClave(String clave){
        EntityManager em = GestorEntityManager.getManager();
        return em.find(Variable.class, clave);
    }

    public static void actualizar(Variable o){
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

    public static void eliminar(Variable o){
        EntityManager em = GestorEntityManager.getManager();

        try {
            em.getTransaction().begin();
            em.remove(o);
            em.getTransaction().commit();
        } catch (Exception e){
            em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}

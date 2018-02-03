/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import DAO.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import modelo.Local;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import modelo.Projeto;

/**
 *
 * @author Emm
 */
public class ProjetoJpaController implements Serializable {

       private EntityManagerFactory emf =  Persistence.createEntityManagerFactory( "ControleFinanceiroPU" );


    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Projeto projeto) {
        if (projeto.getLocais() == null) {
            projeto.setLocais(new ArrayList<Local>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Local> attachedLocais = new ArrayList<Local>();
            for (Local locaisLocalToAttach : projeto.getLocais()) {
                locaisLocalToAttach = em.getReference(locaisLocalToAttach.getClass(), locaisLocalToAttach.getId_local());
                attachedLocais.add(locaisLocalToAttach);
            }
            projeto.setLocais(attachedLocais);
            em.persist(projeto);
            for (Local locaisLocal : projeto.getLocais()) {
                Projeto oldProjetoOfLocaisLocal = locaisLocal.getProjeto();
                locaisLocal.setProjeto(projeto);
                locaisLocal = em.merge(locaisLocal);
                if (oldProjetoOfLocaisLocal != null) {
                    oldProjetoOfLocaisLocal.getLocais().remove(locaisLocal);
                    oldProjetoOfLocaisLocal = em.merge(oldProjetoOfLocaisLocal);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
               em.close();
           
            }
        }
    }

    public void edit(Projeto projeto) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Projeto persistentProjeto = em.find(Projeto.class, projeto.getId_projeto());
            List<Local> locaisOld = persistentProjeto.getLocais();
            List<Local> locaisNew = projeto.getLocais();
            List<Local> attachedLocaisNew = new ArrayList<Local>();
            for (Local locaisNewLocalToAttach : locaisNew) {
                locaisNewLocalToAttach = em.getReference(locaisNewLocalToAttach.getClass(), locaisNewLocalToAttach.getId_local());
                attachedLocaisNew.add(locaisNewLocalToAttach);
            }
            locaisNew = attachedLocaisNew;
            projeto.setLocais(locaisNew);
            projeto = em.merge(projeto);
            for (Local locaisOldLocal : locaisOld) {
                if (!locaisNew.contains(locaisOldLocal)) {
                    locaisOldLocal.setProjeto(null);
                    locaisOldLocal = em.merge(locaisOldLocal);
                }
            }
            for (Local locaisNewLocal : locaisNew) {
                if (!locaisOld.contains(locaisNewLocal)) {
                    Projeto oldProjetoOfLocaisNewLocal = locaisNewLocal.getProjeto();
                    locaisNewLocal.setProjeto(projeto);
                    locaisNewLocal = em.merge(locaisNewLocal);
                    if (oldProjetoOfLocaisNewLocal != null && !oldProjetoOfLocaisNewLocal.equals(projeto)) {
                        oldProjetoOfLocaisNewLocal.getLocais().remove(locaisNewLocal);
                        oldProjetoOfLocaisNewLocal = em.merge(oldProjetoOfLocaisNewLocal);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                int id = projeto.getId_projeto();
                if (findProjeto(id) == null) {
                    throw new NonexistentEntityException("The projeto with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
         
            }
        }
    }

    public void destroy(int id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Projeto projeto;
            try {
                projeto = em.getReference(Projeto.class, id);
                projeto.getId_projeto();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The projeto with id " + id + " no longer exists.", enfe);
            }
            List<Local> locais = projeto.getLocais();
            for (Local locaisLocal : locais) {
                locaisLocal.setProjeto(null);
                locaisLocal = em.merge(locaisLocal);
            }
            em.remove(projeto);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
           
            }
        }
    }

    public List<Projeto> findProjetoEntities() {
        return findProjetoEntities(true, -1, -1);
    }

    public List<Projeto> findProjetoEntities(int maxResults, int firstResult) {
        return findProjetoEntities(false, maxResults, firstResult);
    }

    private List<Projeto> findProjetoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Projeto.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
          
        }
    }

    public Projeto findProjeto(int id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Projeto.class, id);
        } finally {
            em.close();
           
        }
    }

    public int getProjetoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Projeto> rt = cq.from(Projeto.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
           
        }
    }
    
}

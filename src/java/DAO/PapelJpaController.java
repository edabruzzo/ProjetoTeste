/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import DAO.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import modelo.Usuario;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import modelo.Papel;

/**
 *
 * @author Emm
 */
public class PapelJpaController implements Serializable {

   
    private EntityManagerFactory emf =  Persistence.createEntityManagerFactory( "ControleFinanceiroPU" );


    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Papel papel) {
        if (papel.getUsuario() == null) {
            papel.setUsuario(new ArrayList<Usuario>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Usuario> attachedUsuario = new ArrayList<Usuario>();
            for (Usuario usuarioUsuarioToAttach : papel.getUsuario()) {
                usuarioUsuarioToAttach = em.getReference(usuarioUsuarioToAttach.getClass(), usuarioUsuarioToAttach.getIdUsuario());
                attachedUsuario.add(usuarioUsuarioToAttach);
            }
            papel.setUsuario(attachedUsuario);
            em.persist(papel);
            for (Usuario usuarioUsuario : papel.getUsuario()) {
                Papel oldPapelOfUsuarioUsuario = usuarioUsuario.getPapel();
                usuarioUsuario.setPapel(papel);
                usuarioUsuario = em.merge(usuarioUsuario);
                if (oldPapelOfUsuarioUsuario != null) {
                    oldPapelOfUsuarioUsuario.getUsuario().remove(usuarioUsuario);
                    oldPapelOfUsuarioUsuario = em.merge(oldPapelOfUsuarioUsuario);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
           
            }
        }
    }

    public void edit(Papel papel) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Papel persistentPapel = em.find(Papel.class, papel.getIdPapel());
            List<Usuario> usuarioOld = persistentPapel.getUsuario();
            List<Usuario> usuarioNew = papel.getUsuario();
            List<Usuario> attachedUsuarioNew = new ArrayList<Usuario>();
            for (Usuario usuarioNewUsuarioToAttach : usuarioNew) {
                usuarioNewUsuarioToAttach = em.getReference(usuarioNewUsuarioToAttach.getClass(), usuarioNewUsuarioToAttach.getIdUsuario());
                attachedUsuarioNew.add(usuarioNewUsuarioToAttach);
            }
            usuarioNew = attachedUsuarioNew;
            papel.setUsuario(usuarioNew);
            papel = em.merge(papel);
            for (Usuario usuarioOldUsuario : usuarioOld) {
                if (!usuarioNew.contains(usuarioOldUsuario)) {
                    usuarioOldUsuario.setPapel(null);
                    usuarioOldUsuario = em.merge(usuarioOldUsuario);
                }
            }
            for (Usuario usuarioNewUsuario : usuarioNew) {
                if (!usuarioOld.contains(usuarioNewUsuario)) {
                    Papel oldPapelOfUsuarioNewUsuario = usuarioNewUsuario.getPapel();
                    usuarioNewUsuario.setPapel(papel);
                    usuarioNewUsuario = em.merge(usuarioNewUsuario);
                    if (oldPapelOfUsuarioNewUsuario != null && !oldPapelOfUsuarioNewUsuario.equals(papel)) {
                        oldPapelOfUsuarioNewUsuario.getUsuario().remove(usuarioNewUsuario);
                        oldPapelOfUsuarioNewUsuario = em.merge(oldPapelOfUsuarioNewUsuario);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                int id = papel.getIdPapel();
                if (findPapel(id) == null) {
                    throw new NonexistentEntityException("The papel with id " + id + " no longer exists.");
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
            Papel papel;
            try {
                papel = em.getReference(Papel.class, id);
                papel.getIdPapel();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The papel with id " + id + " no longer exists.", enfe);
            }
            List<Usuario> usuario = papel.getUsuario();
            for (Usuario usuarioUsuario : usuario) {
                usuarioUsuario.setPapel(null);
                usuarioUsuario = em.merge(usuarioUsuario);
            }
            em.remove(papel);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
               em.close();
           
            }
        }
    }

    public List<Papel> findPapelEntities() {
        return findPapelEntities(true, -1, -1);
    }

    public List<Papel> findPapelEntities(int maxResults, int firstResult) {
        return findPapelEntities(false, maxResults, firstResult);
    }

    private List<Papel> findPapelEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        List<Papel> listaPapeis = new ArrayList();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Papel.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            
            return (List<Papel>) q.getResultList();
            
        } finally{
            em.close();
        }
    }
    
   
      public List<Papel> findPapelMenosSuper(){
          EntityManager em = getEntityManager();
          String sqlString = "SELECT * FROM tb_papel WHERE IDPAPEL <> 1;";
          Query q = em.createNativeQuery(sqlString, Papel.class);
          
          return (List<Papel>)q.getResultList();
          
          
          
      }
   
   
    public Papel findPapel(int id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Papel.class, id);
        } finally {
            em.close();
           
        }
    }

    public int getPapelCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Papel> rt = cq.from(Papel.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
      
        }
    }
    
}

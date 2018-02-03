/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import DAO.exceptions.NonexistentEntityException;
import TransactionsAnnotations.Transacional;
import factory.JPAFactory;
import modelo.Papel;
import modelo.Usuario;

/**
 *
 * @author Emm
 */
public class PapelJpaController implements Serializable {


	private static final long serialVersionUID = -8089341738409196078L;

	@Inject
    private JPAFactory jpa;
    
	
	@Transacional
    public void create(Papel papel) {
        
    	if (papel.getUsuario() == null) {
            papel.setUsuario(new ArrayList<Usuario>());
        }
        EntityManager em = null;
             em = jpa.getEntityManager();
             // em.getTransaction().begin();
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
            // em.getTransaction().commit();
         
    }

	
	@Transacional
    public void edit(Papel papel) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = jpa.getEntityManager();
            //em.getTransaction().begin();
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
            //em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                int id = papel.getIdPapel();
                if (findPapel(id) == null) {
                    throw new NonexistentEntityException("The papel with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } 
    }

	@Transacional
    public void destroy(int id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = jpa.getEntityManager();
            //em.getTransaction().begin();
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
            // em.getTransaction().commit();
        } finally {
                 }
    }

    public List<Papel> findPapelEntities() {
        return findPapelEntities(true, -1, -1);
    }

    public List<Papel> findPapelEntities(int maxResults, int firstResult) {
        return findPapelEntities(false, maxResults, firstResult);
    }

    private List<Papel> findPapelEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = jpa.getEntityManager();
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
         
        }
    }
    
   
      public List<Papel> findPapelMenosSuper(){
          EntityManager em = jpa.getEntityManager();
          String sqlString = "SELECT * FROM tb_papel WHERE IDPAPEL <> 1;";
          Query q = em.createNativeQuery(sqlString, Papel.class);
          
          return (List<Papel>)q.getResultList();
          
          
      }
   
   
    public Papel findPapel(int id) {
        EntityManager em = jpa.getEntityManager();
        try {
            return em.find(Papel.class, id);
        } finally {
           
        }
    }

    public int getPapelCount() {
        EntityManager em = jpa.getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Papel> rt = cq.from(Papel.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
         
        }
    }
    
}

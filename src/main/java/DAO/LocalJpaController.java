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
import modelo.Gasto;
import modelo.Local;
import modelo.Projeto;

/**
 *
 * @author Emm
 */
public class LocalJpaController implements Serializable {
	private static final long serialVersionUID = 5495412493613862564L;

	
	@Inject
	private JPAFactory jpa;

	
	
	@Transacional
    public void create(Local local) {
        if (local.getGastos() == null) {
            local.setGastos(new ArrayList<Gasto>());
        }
        EntityManager em = null;
        try {
            em = jpa.getEntityManager();
//            em.getTransaction().begin();
            Projeto projeto = local.getProjeto();
            if (projeto != null) {
                projeto = em.getReference(projeto.getClass(), projeto.getId_projeto());
                local.setProjeto(projeto);
            }
            List<Gasto> attachedGastos = new ArrayList<Gasto>();
            for (Gasto gastosGastoToAttach : local.getGastos()) {
                gastosGastoToAttach = em.getReference(gastosGastoToAttach.getClass(), gastosGastoToAttach.getId_gasto());
                attachedGastos.add(gastosGastoToAttach);
            }
            local.setGastos(attachedGastos);
            em.persist(local);
            if (projeto != null) {
                projeto.getLocais().add(local);
                projeto = em.merge(projeto);
            }
            for (Gasto gastosGasto : local.getGastos()) {
                Local oldLocalOfGastosGasto = gastosGasto.getLocal();
                gastosGasto.setLocal(local);
                gastosGasto = em.merge(gastosGasto);
                if (oldLocalOfGastosGasto != null) {
                    oldLocalOfGastosGasto.getGastos().remove(gastosGasto);
                    oldLocalOfGastosGasto = em.merge(oldLocalOfGastosGasto);
                }
            }
            //            em.getTransaction().commit();
        } finally {
        }
    }

	
	@Transacional
    public void edit(Local local) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = jpa.getEntityManager();
            //em.getTransaction().begin();
            Local persistentLocal = em.find(Local.class, local.getId_local());
            Projeto projetoOld = persistentLocal.getProjeto();
            Projeto projetoNew = local.getProjeto();
            List<Gasto> gastosOld = persistentLocal.getGastos();
            List<Gasto> gastosNew = local.getGastos();
            if (projetoNew != null) {
                projetoNew = em.getReference(projetoNew.getClass(), projetoNew.getId_projeto());
                local.setProjeto(projetoNew);
            }
            List<Gasto> attachedGastosNew = new ArrayList<Gasto>();
            for (Gasto gastosNewGastoToAttach : gastosNew) {
                gastosNewGastoToAttach = em.getReference(gastosNewGastoToAttach.getClass(), gastosNewGastoToAttach.getId_gasto());
                attachedGastosNew.add(gastosNewGastoToAttach);
            }
            gastosNew = attachedGastosNew;
            local.setGastos(gastosNew);
            local = em.merge(local);
            if (projetoOld != null && !projetoOld.equals(projetoNew)) {
                projetoOld.getLocais().remove(local);
                projetoOld = em.merge(projetoOld);
            }
            if (projetoNew != null && !projetoNew.equals(projetoOld)) {
                projetoNew.getLocais().add(local);
                projetoNew = em.merge(projetoNew);
            }
            for (Gasto gastosOldGasto : gastosOld) {
                if (!gastosNew.contains(gastosOldGasto)) {
                    gastosOldGasto.setLocal(null);
                    gastosOldGasto = em.merge(gastosOldGasto);
                }
            }
            for (Gasto gastosNewGasto : gastosNew) {
                if (!gastosOld.contains(gastosNewGasto)) {
                    Local oldLocalOfGastosNewGasto = gastosNewGasto.getLocal();
                    gastosNewGasto.setLocal(local);
                    gastosNewGasto = em.merge(gastosNewGasto);
                    if (oldLocalOfGastosNewGasto != null && !oldLocalOfGastosNewGasto.equals(local)) {
                        oldLocalOfGastosNewGasto.getGastos().remove(gastosNewGasto);
                        oldLocalOfGastosNewGasto = em.merge(oldLocalOfGastosNewGasto);
                    }
                }
            }
            //em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                int id = local.getId_local();
                if (findLocal(id) == null) {
                    throw new NonexistentEntityException("The local with id " + id + " no longer exists.");
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
            Local local;
            try {
                local = em.getReference(Local.class, id);
                local.getId_local();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The local with id " + id + " no longer exists.", enfe);
            }
            Projeto projeto = local.getProjeto();
            if (projeto != null) {
                projeto.getLocais().remove(local);
                projeto = em.merge(projeto);
            }
            List<Gasto> gastos = local.getGastos();
            for (Gasto gastosGasto : gastos) {
                gastosGasto.setLocal(null);
                gastosGasto = em.merge(gastosGasto);
            }
            em.remove(local);
            //em.getTransaction().commit();
        } catch (EntityNotFoundException enfe) {
            throw new NonexistentEntityException("The local with id " + id + " no longer exists.", enfe);
        }
    }

    public List<Local> findLocalEntities() {
        return findLocalEntities(true, -1, -1);
    }

    public List<Local> findLocalEntities(int maxResults, int firstResult) {
        return findLocalEntities(false, maxResults, firstResult);
    }

    private List<Local> findLocalEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = jpa.getEntityManager();
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Local.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
    }

    public Local findLocal(int id) {
        EntityManager em = jpa.getEntityManager();
        
            return em.find(Local.class, id);
        
    }

    public int getLocalCount() {
        EntityManager em = jpa.getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Local> rt = cq.from(Local.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
          
        }
    }
    
}

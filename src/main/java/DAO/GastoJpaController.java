/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import DAO.exceptions.NonexistentEntityException;
import Default.JPAUtil;
import bean.GastoBean;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import modelo.Gasto;
import modelo.Usuario;
import modelo.Local;

/**
 *
 * @author Emm
 */
public class GastoJpaController implements Serializable {

	JPAUtil jpa = new JPAUtil();

	
      
    public boolean create(Gasto gasto) {
        EntityManager em = null;
        try {
            em = this.jpa.getEntityManager();
            em.getTransaction().begin();
            Usuario usuario = gasto.getUsuario();
            if (usuario != null) {
                usuario = em.getReference(usuario.getClass(), usuario.getIdUsuario());
                gasto.setUsuario(usuario);
            }
            Local local = gasto.getLocal();
            if (local != null) {
                local = em.getReference(local.getClass(), local.getId_local());
                gasto.setLocal(local);
            }
            em.persist(gasto);
            if (usuario != null) {
                usuario.getGastos().add(gasto);
                usuario = em.merge(usuario);
            }
            if (local != null) {
                local.getGastos().add(gasto);
                local = em.merge(local);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
               em.close();
        
            }
        }
        return true;
    }

    public void edit(Gasto gasto) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
        	 em = this.jpa.getEntityManager();
        	 em.getTransaction().begin();
            Gasto persistentGasto = em.find(Gasto.class, gasto.getId_gasto());
            Usuario usuarioOld = persistentGasto.getUsuario();
            Usuario usuarioNew = gasto.getUsuario();
            Local localOld = persistentGasto.getLocal();
            Local localNew = gasto.getLocal();
            if (usuarioNew != null) {
                usuarioNew = em.getReference(usuarioNew.getClass(), usuarioNew.getIdUsuario());
                gasto.setUsuario(usuarioNew);
            }
            if (localNew != null) {
                localNew = em.getReference(localNew.getClass(), localNew.getId_local());
                gasto.setLocal(localNew);
            }
            gasto = em.merge(gasto);
            if (usuarioOld != null && !usuarioOld.equals(usuarioNew)) {
                usuarioOld.getGastos().remove(gasto);
                usuarioOld = em.merge(usuarioOld);
            }
            if (usuarioNew != null && !usuarioNew.equals(usuarioOld)) {
                usuarioNew.getGastos().add(gasto);
                usuarioNew = em.merge(usuarioNew);
            }
            if (localOld != null && !localOld.equals(localNew)) {
                localOld.getGastos().remove(gasto);
                localOld = em.merge(localOld);
            }
            if (localNew != null && !localNew.equals(localOld)) {
                localNew.getGastos().add(gasto);
                localNew = em.merge(localNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                int id = gasto.getId_gasto();
                if (findGasto(id) == null) {
                    throw new NonexistentEntityException("The gasto with id " + id + " no longer exists.");
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
        	 em = this.jpa.getEntityManager();
            em.getTransaction().begin();
            Gasto gasto;
            try {
                gasto = em.getReference(Gasto.class, id);
                gasto.getId_gasto();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The gasto with id " + id + " no longer exists.", enfe);
            }
            Usuario usuario = gasto.getUsuario();
            if (usuario != null) {
                usuario.getGastos().remove(gasto);
                usuario = em.merge(usuario);
            }
            Local local = gasto.getLocal();
            if (local != null) {
                local.getGastos().remove(gasto);
                local = em.merge(local);
            }
            em.remove(gasto);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
               em.close();
      
            }
        }
    }

    public List<Gasto> findGastoEntities() {
        return findGastoEntities(true, -1, -1);
    }

    public List<Gasto> findGastoEntities(int maxResults, int firstResult) {
        return findGastoEntities(false, maxResults, firstResult);
    }

    private List<Gasto> findGastoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em =  this.jpa.getEntityManager();
        try {
            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
            CriteriaQuery<Object> criteriaQuery = criteriaBuilder.createQuery();
            Root<Gasto> from = criteriaQuery.from(Gasto.class);
            CriteriaQuery<Object> select1 = criteriaQuery.select(from);
            select1.orderBy(criteriaBuilder.desc(from.get("dataGasto")));
            Query q = em.createQuery(select1);
            
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return (List<Gasto>) q.getResultList();
        } finally {
            em.close();
         
        }
    }
    
    
    public Gasto findGasto(int id) {
        EntityManager em = null;
        em = this.jpa.getEntityManager();
        try{
            return em.find(Gasto.class, id);
        }finally {
           em.close();
        
        }
    }

    public int getGastoCount() {
        EntityManager em = null;
        em = this.jpa.getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Gasto> rt = cq.from(Gasto.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
         
        }
    }
    
              
       public List<Gasto> listaGastosByConsultaSQL(){
        EntityManager em = jpa.getEntityManager();
           String sqlString = "SELECT * FROM tb_gasto ORDER BY ID_GASTO DESC;";
          Query q = em.createNativeQuery(sqlString, Gasto.class);
           List<Gasto> listaGasto = new ArrayList();
          try{
         
          listaGasto = q.getResultList();

          }catch (PersistenceException pe) {
              apresentaMensagem(pe.getMessage());
         // }catch(NullPointerException npex){
           //   apresentaMensagem();
         // }
           
          }
           em.close();
             
           return listaGasto;
       }
       
       
         
        public List<Gasto> listaGastosByUsuarioLogado(Integer idUsuario){
        EntityManager em = jpa.getEntityManager();
           String sqlString = "SELECT g.* FROM tb_gasto g "
              + " WHERE g.USUARIO_IDUSUARIO = "+idUsuario+" ORDER BY DATAGASTO DESC;";
          Query q = em.createNativeQuery(sqlString, Gasto.class);
          try{
           return (List<Gasto>) q.getResultList();
         // }catch(NullPointerException npex){
           //   apresentaMensagem();
         // }
           
          }finally{
           em.close();
              
          } 
           
       }
        
        
        
        public List<Gasto> listaGastosByProjeto(Integer projetoID){
            EntityManager em = this.jpa.getEntityManager();
         String sqlString = "SELECT g.* FROM tb_gasto g "
         + "INNER JOIN tb_projeto_tb_local pl ON g.LOCAL_ID_LOCAL = pl.locais_ID_LOCAL "
         + "INNER JOIN tb_projeto p ON pl.Projeto_ID_PROJETO = p.ID_PROJETO "        
            + " WHERE p.ID_PROJETO = "+projetoID+";";                   
        Query q = em.createNativeQuery(sqlString, Gasto.class);
        try{
         return (List<Gasto>) q.getResultList();
          }finally{
         em.close();
        }
  
         
      }        	
        	
        	
           
       
        public double calculaGastosTotais() {
        
        EntityManager em = jpa.getEntityManager();
        double resultado = 0;
                  
        String sqlString = "SELECT sum(g.VALORGASTO) FROM tb_gasto g ;";
         
          Query q = em.createNativeQuery(sqlString);
          try{
               resultado = (double) q.getSingleResult();
          }catch(NullPointerException npex){
              
        
          apresentaMensagem();
        }
          
        em.close();
           
        return resultado;
    }
        
        
       

        
        public void apresentaMensagem(String mensagemErro){
              GastoBean gb = new GastoBean();
              String mensagem = "NÃO FOI POSSÍVEL ENCONTRAR GASTOS "
              		+ "POR CONTA DO ERRO A SEGUIR"+ mensagemErro ;
              gb.mostraMensagemErro(mensagem);
          }
          

        public void apresentaMensagem(){
            GastoBean gb = new GastoBean();
            String mensagem = "NÃO FOI POSSÍVEL ENCONTRAR GASTOS " ;
            gb.mostraMensagemErro(mensagem);
        }

        
        
}

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
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import DAO.exceptions.NonexistentEntityException;
import TransactionsAnnotations.Transacional;
import bean.GastoBean;
import factory.JPAFactory;
import jpaAnnotations.Query_;
import modelo.Gasto;
import modelo.Local;
import modelo.Usuario;

/**
 *
 * @author Emm
 */
public class GastoJpaController implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1343218160966481708L;
	
	
	
	@Inject
	private JPAFactory jpa;

	private Usuario usuarioNew = new Usuario();

	
	@Inject
    private GastoBean gb;
	
	@Inject
	@Query_("SELECT * FROM tb_gasto ORDER BY ID_GASTO DESC;")
    private Query listaGastosByConsultaSQL; 	
	
	
	
	@Transacional
    public boolean create(Gasto gasto) {
        EntityManager em = null;
            em = this.jpa.getEntityManager();
            
          //  em.getTransaction().begin();

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
            //em.getTransaction().commit();
                 return true;
    }
	
	
	
	@Transacional
    public void edit(Gasto gasto) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
        	 em = this.jpa.getEntityManager();
        	
        	 
        	 //em.getTransaction().begin();
           
        	 
        	Gasto persistentGasto = em.find(Gasto.class, gasto.getId_gasto());
            Usuario usuarioOld = persistentGasto.getUsuario();
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
        
            //em.getTransaction().commit();
        
        
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                int id = gasto.getId_gasto();
                if (findGasto(id) == null) {
                    throw new NonexistentEntityException("The gasto with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } 
    }

    
	@Transacional
	public void destroy(int id) throws NonexistentEntityException {
        
    	    EntityManager em = null;

            em = this.jpa.getEntityManager();
        
            //em.getTransaction().begin();
            
            Gasto gasto;
            
                gasto = em.getReference(Gasto.class, id);
                gasto.getId_gasto();
            
            
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

            //em.getTransaction().commit();
            
    }

    public List<Gasto> findGastoEntities() {
        return findGastoEntities(true, -1, -1);
    }

    public List<Gasto> findGastoEntities(int maxResults, int firstResult) {
        return findGastoEntities(false, maxResults, firstResult);
    }

    private List<Gasto> findGastoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em =  this.jpa.getEntityManager();
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
    }
    
    
    public Gasto findGasto(int id) {
        EntityManager em = null;
        em = this.jpa.getEntityManager();
            return em.find(Gasto.class, id);
        
    }

    public int getGastoCount() {
        EntityManager em = null;
        em = this.jpa.getEntityManager();
        
        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Gasto> rt = cq.from(Gasto.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        
    }
    
              	
       public List<Gasto> listaGastosByConsultaSQL(){
        
        
           List<Gasto> listaGasto = new ArrayList();
          try{
         
          listaGasto = listaGastosByConsultaSQL.getResultList();

          }catch (PersistenceException pe) {
              apresentaMensagem(pe.getMessage());
           
          }
             
           return listaGasto;
       }
       
       
         
        public List<Gasto> listaGastosByUsuarioLogado(Integer idUsuario){
        
        EntityManager em = jpa.getEntityManager();
         String sqlString = "SELECT g.* FROM tb_gasto g "
              + " WHERE g.USUARIO_IDUSUARIO = "+idUsuario+" ORDER BY DATAGASTO DESC;";
          Query q = em.createNativeQuery(sqlString, Gasto.class);
        
          return (List<Gasto>) q.getResultList(); 
       }
        
        
        
        public List<Gasto> listaGastosByProjeto(Integer projetoID){
            EntityManager em = this.jpa.getEntityManager();
         String sqlString = "SELECT g.* FROM tb_gasto g "
         + "INNER JOIN tb_projeto_tb_local pl ON g.LOCAL_ID_LOCAL = pl.locais_ID_LOCAL "
         + "INNER JOIN tb_projeto p ON pl.Projeto_ID_PROJETO = p.ID_PROJETO "        
            + " WHERE p.ID_PROJETO = "+projetoID+";";                   
        Query q = em.createNativeQuery(sqlString, Gasto.class);
         return (List<Gasto>) q.getResultList();
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
          
           
        return resultado;
    }
        
        
       

        
        public void apresentaMensagem(String mensagemErro){

              String mensagem = "NÃO FOI POSSÍVEL ENCONTRAR GASTOS "
              		+ "POR CONTA DO ERRO A SEGUIR"+ mensagemErro ;
              gb.mostraMensagemErro(mensagem);
          }
          

        public void apresentaMensagem(){
            String mensagem = "NÃO FOI POSSÍVEL ENCONTRAR GASTOS " ;
            gb.mostraMensagemErro(mensagem);
        }

        
        
}

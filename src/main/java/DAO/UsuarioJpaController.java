/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import DAO.exceptions.NonexistentEntityException;
import Default.CriptografiaSenha;
import bean.LoginFilter;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import modelo.Papel;
import modelo.Gasto;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import modelo.Usuario;

/**
 *
 * @author Emm
 */
public class UsuarioJpaController implements Serializable {

  
    private EntityManagerFactory emf =  Persistence.createEntityManagerFactory( "ControleFinanceiroPU" );

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Usuario usuario) {
        if (usuario.getGastos() == null) {
            usuario.setGastos(new ArrayList<Gasto>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Papel papel = usuario.getPapel();
            if (papel != null) {
                papel = em.getReference(papel.getClass(), papel.getIdPapel());
                usuario.setPapel(papel);
            }
            List<Gasto> attachedGastos = new ArrayList<Gasto>();
            for (Gasto gastosGastoToAttach : usuario.getGastos()) {
                gastosGastoToAttach = em.getReference(gastosGastoToAttach.getClass(), gastosGastoToAttach.getId_gasto());
                attachedGastos.add(gastosGastoToAttach);
            }
            usuario.setGastos(attachedGastos);
            em.persist(usuario);
            if (papel != null) {
                papel.getUsuario().add(usuario);
                papel = em.merge(papel);
            }
            for (Gasto gastosGasto : usuario.getGastos()) {
                Usuario oldUsuarioOfGastosGasto = gastosGasto.getUsuario();
                gastosGasto.setUsuario(usuario);
                gastosGasto = em.merge(gastosGasto);
                if (oldUsuarioOfGastosGasto != null) {
                    oldUsuarioOfGastosGasto.getGastos().remove(gastosGasto);
                    oldUsuarioOfGastosGasto = em.merge(oldUsuarioOfGastosGasto);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
           
            }
        }
    }

    public void edit(Usuario usuario) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Usuario persistentUsuario = em.find(Usuario.class, usuario.getIdUsuario());
            Papel papelOld = persistentUsuario.getPapel();
            Papel papelNew = usuario.getPapel();
            List<Gasto> gastosOld = persistentUsuario.getGastos();
            List<Gasto> gastosNew = usuario.getGastos();
            if (papelNew != null) {
                papelNew = em.getReference(papelNew.getClass(), papelNew.getIdPapel());
                usuario.setPapel(papelNew);
            }
            List<Gasto> attachedGastosNew = new ArrayList<Gasto>();
            for (Gasto gastosNewGastoToAttach : gastosNew) {
                gastosNewGastoToAttach = em.getReference(gastosNewGastoToAttach.getClass(), gastosNewGastoToAttach.getId_gasto());
                attachedGastosNew.add(gastosNewGastoToAttach);
            }
            gastosNew = attachedGastosNew;
            usuario.setGastos(gastosNew);
            usuario = em.merge(usuario);
            if (papelOld != null && !papelOld.equals(papelNew)) {
                papelOld.getUsuario().remove(usuario);
                papelOld = em.merge(papelOld);
            }
            if (papelNew != null && !papelNew.equals(papelOld)) {
                papelNew.getUsuario().add(usuario);
                papelNew = em.merge(papelNew);
            }
            for (Gasto gastosOldGasto : gastosOld) {
                if (!gastosNew.contains(gastosOldGasto)) {
                    gastosOldGasto.setUsuario(null);
                    gastosOldGasto = em.merge(gastosOldGasto);
                }
            }
            for (Gasto gastosNewGasto : gastosNew) {
                if (!gastosOld.contains(gastosNewGasto)) {
                    Usuario oldUsuarioOfGastosNewGasto = gastosNewGasto.getUsuario();
                    gastosNewGasto.setUsuario(usuario);
                    gastosNewGasto = em.merge(gastosNewGasto);
                    if (oldUsuarioOfGastosNewGasto != null && !oldUsuarioOfGastosNewGasto.equals(usuario)) {
                        oldUsuarioOfGastosNewGasto.getGastos().remove(gastosNewGasto);
                        oldUsuarioOfGastosNewGasto = em.merge(oldUsuarioOfGastosNewGasto);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                int id = usuario.getIdUsuario();
                if (findUsuario(id) == null) {
                    throw new NonexistentEntityException("The usuario with id " + id + " no longer exists.");
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
            Usuario usuario;
            try {
                usuario = em.getReference(Usuario.class, id);
                usuario.getIdUsuario();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The usuario with id " + id + " no longer exists.", enfe);
            }
            Papel papel = usuario.getPapel();
            if (papel != null) {
                papel.getUsuario().remove(usuario);
                papel = em.merge(papel);
            }
            List<Gasto> gastos = usuario.getGastos();
            for (Gasto gastosGasto : gastos) {
                gastosGasto.setUsuario(null);
                gastosGasto = em.merge(gastosGasto);
            }
            em.remove(usuario);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
           
            }
        }
    }

    public List<Usuario> findUsuarioEntities() {
        return findUsuarioEntities(true, -1, -1);
    }

    public List<Usuario> findUsuarioEntities(int maxResults, int firstResult) {
        return findUsuarioEntities(false, maxResults, firstResult);
    }

    private List<Usuario> findUsuarioEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        List<Usuario> listaUsuario = new ArrayList();
       
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Usuario.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            listaUsuario =  q.getResultList();
        
        if (listaUsuario.isEmpty()){
        
        PapelJpaController papelDAO = new PapelJpaController();
        Papel papel1 = new Papel();
        papel1.setAtivo(true);
        papel1.setDescPapel("SUPER ADMINISTRADOR");
        papel1.setPrivAdmin(true);
        papel1.setPrivSuperAdmin(true);
        papelDAO.create(papel1);
        
        Papel papel2 = new Papel();
        papel2.setAtivo(true);
        papel2.setDescPapel("ADMINISTRADOR");
        papel2.setPrivAdmin(true);
        papelDAO.create(papel2);
        
        
        Papel papel3 = new Papel();
        papel3.setAtivo(true);
        papel3.setDescPapel("USUÁRIO");
        papelDAO.create(papel3);
        
        
            
        Usuario usuario = new Usuario();
        usuario.setLogin("SUPERADMIN");
        
        CriptografiaSenha criptografar = new CriptografiaSenha();
        String senhaCriptografada = criptografar.convertStringToMd5("SUPERADMIN.123");
        
        usuario.setPassword(senhaCriptografada);
        usuario.setNome("EDITE SEU NOME, LOGIN E SENHA");
        usuario.setEmail("ALTERE SEU E-MAIL");
        usuario.setPapel(papel1);
        create(usuario);
            
       }
            
        em.close();
        
        return listaUsuario;
        
        
    }

    public Usuario findUsuario(int id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Usuario.class, id);
        } finally {
            em.close();
           
        }
    }

    public int getUsuarioCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Usuario> rt = cq.from(Usuario.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
           
        }
    }
    
    
    public Usuario findByLoginSenha(String login, String senha){
        
        Usuario usuario = new Usuario();
        EntityManager em = getEntityManager();
        List<Usuario> listaUsuarios = new ArrayList();
        listaUsuarios = findUsuarioEntities();
        if (listaUsuarios != null || !listaUsuarios.isEmpty()){
         
        String sql = "SELECT * FROM tb_usuario WHERE LOGIN LIKE '"+
        login+"' AND PASSWORD LIKE '"+senha+"'";
        
        Query q = em.createNativeQuery(sql, Usuario.class);
        
     
        try{
        Object o = q.getSingleResult();
        usuario = (Usuario) o;
        }catch(NoResultException e) {
           
            apresentaMensagemErro("validaAcesso", "USUÁRIO NÃO ENCONTRADO");
            e.printStackTrace();
            return null;
            
        }finally{
        
        em.close();
         
        }
     }
    return usuario;
    
    }
    
    
    
    public Usuario findByLogin(String login){
        Usuario usuario = new Usuario();
        EntityManager em = getEntityManager();
        
        String sql = "SELECT * FROM tb_usuario WHERE LOGIN LIKE '"+
                login+"'";
        
        Query q = em.createNativeQuery(sql, Usuario.class);
        
        try{
        Object o = q.getSingleResult();
        usuario = (Usuario) o;
        }catch(NoResultException e) {
           
          String   mensagem = "USUÁRIO NÃO ENCONTRADO"+e.getMessage();
          
          apresentaMensagemErro("solicitaSenha", mensagem);
          return null;
            
        }finally{
        
       em.close();
          
        }
    return usuario;
    
    }
    
    public void apresentaMensagemErro(String idElemento, String mensagemErro){
        LoginFilter lf = new LoginFilter();
        lf.apresentaMensagemErro(idElemento, mensagemErro);
           
    }
    
    
}

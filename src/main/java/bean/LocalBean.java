/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import DAO.LocalJpaController;
import DAO.ProjetoJpaController;
import jsfAnnotation.SessionModel;
import modelo.Local;
import modelo.Projeto;

/**
 *
 * @author Emm
 */
@SessionModel
public class LocalBean implements Serializable{


	private static final long serialVersionUID = -716678353134143011L;

	/**
     * Creates a new instance of LocalBean
     */
    public LocalBean() {
    }
    
    private int projetoID;
    
    private Projeto projeto = new Projeto();
    
    private Local local = new Local();

    @Inject
    private ProjetoJpaController projetoDAO;
    
    @Inject
    private LocalJpaController localDAO;    
    
    
    public Local getLocal() {
        return local;
    }

    public void setLocal(Local local) {
        this.local = local;
    }

    public Projeto getProjeto() {
        return projeto;
    }

    public void setProjeto(Projeto projeto) {
        this.projeto = projeto;
    }
    

    public int getProjetoID() {
        return projetoID;
    }

    public void setProjetoID(int projetoID) {
        this.projetoID = projetoID;
    }
    
    
    public List<Projeto> listaProjetos(){
        
    	//ProjetoJpaController projetoDAO = new ProjetoJpaController();
         List<Projeto> listaProjetos = new ArrayList();
         listaProjetos = projetoDAO.findProjetoEntities();
         return listaProjetos;
        
    }
    
    public void gravaProjetoNoLocal(){
    	//ProjetoJpaController projetoDAO = new ProjetoJpaController();
         this.projeto = projetoDAO.findProjeto(projetoID);
         this.local.setProjeto(this.projeto);
    }
    
    
    public void criaNovoLocal(){
        
//LocalJpaController localDAO = new LocalJpaController();
        localDAO.create(this.local);
        
    }
    
    
    public List<Local> listaLocais(){
    	
//        LocalJpaController localDAO = new LocalJpaController();
        List<Local> listaLocais = new ArrayList();  
        listaLocais = localDAO.findLocalEntities();
        return listaLocais;
    }
   
    
    
}

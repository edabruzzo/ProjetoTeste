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

import DAO.ProjetoJpaController;
import jsfAnnotation.SessionModel;
import modelo.Projeto;

/**
 *
 * @author Emm
 */
@SessionModel
public class ProjetoBean implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 8173294256756299119L;

	/**
     * Creates a new instance of ProjetoBean
     */
    public ProjetoBean() {
    }
    
    private Projeto projeto = new Projeto();

    @Inject
    ProjetoJpaController projetoDAO;
    
    public Projeto getProjeto() {
        return projeto;
    }

    public void setProjeto(Projeto projeto) {
        this.projeto = projeto;
    }
    
    
    public void criarNovoProjeto(){
        
        this.projeto.setAtivo(true);
        projetoDAO.create(this.projeto);
        
    }
    
    
    public List<Projeto> listaProjetos(){
        
    List<Projeto> listaProjetos = new ArrayList();
    
    listaProjetos = projetoDAO.findProjetoEntities();
    return listaProjetos;
        
    }
    
    
    
    
    
    
    
}

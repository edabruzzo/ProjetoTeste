/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean;

import DAO.GastoJpaController;
import DAO.LocalJpaController;
import DAO.ProjetoJpaController;
import DAO.UsuarioJpaController;
import DAO.exceptions.NonexistentEntityException;
import java.util.List;
import java.util.Locale;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.inject.Named;
import javax.faces.context.FacesContext;
import modelo.Gasto;
import modelo.Local;
import modelo.Projeto;
import modelo.Usuario;
import org.primefaces.event.data.FilterEvent;
import javax.enterprise.context.SessionScoped;


/**
 *
 * @author Emm
 */
@Named
@SessionScoped
public class GastoBean {
    
    private Gasto gasto = new Gasto();
    private Gasto gastoEditado = new Gasto();
    private Local local = new Local();
    private Usuario usuario = new Usuario();
    private int projetoID;
    private int localID;
    private int IDUsuarioPesquisado;
    private List<Gasto> listaGastosFiltrados;
    private boolean houveErro = false;
    private List<Gasto> listaGastosTotais;
    private double gastosFiltrados;
    private double gastosTotais;
   
    
      public List<Gasto> getListaGastosFiltrados() {
        return listaGastosFiltrados;
    }

    public void setListaGastosFiltrados(List<Gasto> listaGastosFiltrados) {
        this.listaGastosFiltrados = listaGastosFiltrados;
    }


    public List<Gasto> getListaGastosTotais() {
          GastoJpaController gastoDAO = new GastoJpaController();
          LoginFilter lf = new LoginFilter();
          
          if (lf.verificaPrivilegio()) {

        	  this.listaGastosTotais = gastoDAO.listaGastosByConsultaSQL();
        	  
        	  
          }else {
        	  
        	  
        	  this.listaGastosTotais = gastoDAO.listaGastosByUsuarioLogado(lf.getUsuario().getIdUsuario());
          }
       
        return listaGastosTotais;
        
    }

    public void setListaGastosTotais(List<Gasto> listaGastosTotais) {
        this.listaGastosTotais = listaGastosTotais;
    }

    public double getGastosFiltrados() {
     
        return this.gastosFiltrados;
    }

    public void setGastosFiltrados(double gastosFiltrados) {
        this.gastosFiltrados = gastosFiltrados;
    }
    

    

    public int getProjetoID() {
        return projetoID;
    }

    public void setProjetoID(int projetoID) {
        this.projetoID = projetoID;
    }



    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

   
    
  
    public double getGastosTotais() {
        
        return this.gastosTotais;
    }

    public void setGastosTotais(double gastosTotais) {
        this.gastosTotais = gastosTotais;
    }


    public int getLocalID() {
        return localID;
    }

    public void setLocalID(int localID) {
        this.localID = localID;
    }
    
    



    public Local getLocal() {
        return local;
    }

    public void setLocal(Local local) {
        this.local = local;
    }

    public Gasto getGasto() {
        return gasto;
    }

    public void setGasto(Gasto gasto) {
        this.gasto = gasto;
    }
    
    /**
     * Creates a new instance of GastoBean
     */
    public GastoBean() {
    }
    
    
    public void mostraMensagemErro(String message){
        
    this.setHouveErro(true);
    FacesMessage fm = new FacesMessage( message);
    FacesContext.getCurrentInstance().addMessage("tabelaGastos", fm);
    
    
    }
    
    
    
  
    
    public void adicionarGasto() throws NonexistentEntityException, Exception{
    
        GastoJpaController gastoDAO = new GastoJpaController();
        boolean gravado = false;
        
        if (gastoDAO.findGasto(gasto.getId_gasto()) == null) {
      	
        	if(this.gasto.getLocal()==null & gasto.getUsuario()==null){
          	
          	String mensagem1 = null;
               mensagem1 = "HOUVE UM PROBLEMA E O GASTO NÃO FOI GRAVADO POIS "
                       + "O LOCAL E/OU USUÁRIO ESTÃO NULOS";
              FacesMessage fm = new FacesMessage(mensagem1);
               FacesContext.getCurrentInstance().addMessage("gravaGasto", fm);
          
        	gravado =  gastoDAO.create(this.gasto);
      	  
        }else if(gastoDAO.findGasto(this.gasto.getId_gasto()) != null) {
        	
        	gastoDAO.edit(this.gasto);
        	
        }

        
        String mensagem = null;
           
               
        if  (!gravado){
            mensagem = "HOUVE UM PROBLEMA E O GASTO NÃO FOI GRAVADO";
            FacesMessage fm = new FacesMessage(mensagem);
             FacesContext.getCurrentInstance().addMessage("gravaGasto", fm);
        }else {
            
            mensagem = "O GASTO FOI GRAVADO COM SUCESSO";
            FacesMessage fm = new FacesMessage(mensagem);
            FacesContext.getCurrentInstance().addMessage("gravaGasto", fm);
              }
        this.listaGastosTotais = getListaGastosTotais();            
        this.gasto = new Gasto();
        }
    }
        
    
        public void gravaLocal(){
            
       LocalJpaController localDAO = new LocalJpaController();
        this.local = localDAO.findLocal(localID);
        this.gasto.setLocal(local);
                    
        }
    
        public List<Local> selecionaLocais(){
            
            LocalJpaController locaisDAO = new LocalJpaController();
           
            List<Local> listaLocais =  locaisDAO.findLocalEntities();
            
            return listaLocais;
        }
        
        
        public List<Projeto> selecionaProjetos(){
            
            ProjetoJpaController projetoDAO = new ProjetoJpaController();
            List<Projeto> listaProjetos = projetoDAO.findProjetoEntities();
            return listaProjetos;
        }
        
        
           public List<Usuario> selecionaUsuarios(){
            
            UsuarioJpaController usuarioDAO = new UsuarioJpaController();
           
            List<Usuario> listaUsuarios =  usuarioDAO.findUsuarioEntities();
            
            return listaUsuarios;
        }
           
             public void gravaUsuario(){
            
            UsuarioJpaController usuarioDAO = new UsuarioJpaController();
            this.usuario = usuarioDAO.findUsuario(getIDUsuarioPesquisado());
            gasto.setUsuario(usuario);
        }
             

             
             
             public void editaGasto() throws Exception{
                 
            	 LoginFilter lf = new LoginFilter();
                 boolean possuiPrivilegio = lf.verificaPrivilegio();
                 
                 if(possuiPrivilegio){
 
                 }else {
            
            FacesMessage fm = new FacesMessage("DESCULPE, MAS VOCÊ NÃO TEM PRIVILÉGIO DE ADMINISTRADOR PARA EXECUTAR ESTA AÇÃO!");
            FacesContext.getCurrentInstance().addMessage("editaGasto", fm);
               }
                 
             }
             
             
             /*
             Estou recebendo neste método um gasto como parâmetro que está 
             vindo do dataTable. Ou seja, este gasto que vem como parâmetro é 
             a variável item lá do dataTable.
             
             Uma outra foma de editar seria carregar os datos modificados no 
             objeto gastoEditado desta classe aqui, através de um elemento 
             
                <h:commandButton value="SALVAR GASTO EDITADO" 
                             action="#{gastoBean.salvarGastoEditado()}"
                             rendered="#{gastoBean.canEdit}">
                             
                <f:setPropertyActionListener target="#{gastoBean.gastoEditado}"
             value="#{item}"/> 
          
            </h:commandButton>
             
             Neste caso eu não passaria o item como parâmetro do método salvarGastoEditado().
             
             */
            public void salvarGastoEditado() throws Exception{
                
                /*Só é necessário se eu não utilizar o 
                <f:setPropertyActionListener /> no dataTable
                Só que neste caso tenho que passar a variável do dataTable (que 
                é um Gasto, ou seja, um item da lista de gastos carregada) 
                como parâmetro do método.
                
                this.gastoEditado = gasto; 
                 */
                
                LoginFilter lf = new LoginFilter();
                 boolean possuiPrivilegio = lf.verificaPrivilegio();
                 
                 if(possuiPrivilegio & this.getGastoEditado() != null){
                this.gasto = this.gastoEditado;
                adicionarGasto();
                this.listaGastosTotais = getListaGastosTotais();            
               }
                
                
            }
        
        
             public void deletaGasto() throws NonexistentEntityException{
                 
                 LoginFilter lf = new LoginFilter();
                 boolean possuiPrivilegio = lf.verificaPrivilegio();
                 
                 if(possuiPrivilegio & this.gasto != null){
                 
                 GastoJpaController gastoDAO = new GastoJpaController();
                 gastoDAO.destroy(this.gasto.getId_gasto());
                 this.gastosTotais = 0;
    
                 }
                 
             }
        
        
             
             
             
             
             
             
         // http://respostas.guj.com.br/9399-primefaces-datatable-listener-para-calculo-apos-filtro
        //        https://groups.google.com/forum/#!topic/javasf/24reJNQo-eQ  

 /*   public void filterListener(){
        
             for (Gasto gasto : this.listaGastosFiltrados){
                
                this.gastosFiltrados += gasto.getValorGasto();
                
            }
       }

/*
        public void calcularTotaisFiltrados(List<Gasto> listaGastosFiltrada){
            
            for (Gasto gasto : this.listaGastosFiltrados){
                
                this.gastosFiltrados += gasto.getValorGasto();
                
            }
            
        }
     */  
        
        
    public boolean valorEhMenor(Object valorColuna, Object filtroDigitado, Locale locale) { //java.util.Locale
   
    //tirando espaços do filtro
        String textoDigitado = (filtroDigitado == null) ? null : filtroDigitado.toString().trim();

        System.out.println("Filtrando pelo " + textoDigitado + ", Valor do elemento: " + valorColuna);

        // o filtro é nulo ou vazio?
        if (textoDigitado == null || textoDigitado.equals("")) {
            return true;
        }

        // elemento da tabela é nulo?
        if (valorColuna == null) {
            return false;
        }

        try {
            // fazendo o parsing do filtro para converter para Double
            Double valorDigitado = Double.valueOf(textoDigitado);
            Double valorXColuna = (Double) valorColuna;

            // comparando os valores, compareTo devolve um valor negativo se o value é menor do que o filtro
            return valorXColuna.compareTo(valorDigitado) < 0;

        } catch (NumberFormatException e) {

            // usuario nao digitou um numero
            return false;
        }
    
    
    }

    public void calculaGastos(){
         
         
        if (this.listaGastosFiltrados != null){
           
            this.gastosFiltrados = 0;
            for (Gasto gasto : this.listaGastosFiltrados){
             this.gastosFiltrados += gasto.getValorGasto();
              }
        }else {
            this.gastosFiltrados = 0;
            for (Gasto gasto : this.listaGastosTotais){
            this.gastosFiltrados += gasto.getValorGasto();
                }
            }
        

       }

	public boolean isHouveErro() {
		return houveErro;
	}

	public void setHouveErro(boolean houveErro) {
		this.houveErro = houveErro;
	}

	public Gasto getGastoEditado() {
		return gastoEditado;
	}

	public void setGastoEditado(Gasto gastoEditado) {
		this.gastoEditado = gastoEditado;
	}

	public int getIDUsuarioPesquisado() {
		return IDUsuarioPesquisado;
	}

	public void setIDUsuarioPesquisado(int iDUsuarioPesquisado) {
		IDUsuarioPesquisado = iDUsuarioPesquisado;
	}
       
  
}

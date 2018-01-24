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
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import modelo.Gasto;
import modelo.Local;
import modelo.Projeto;
import modelo.Usuario;
import org.primefaces.event.data.FilterEvent;



/**
 *
 * @author Emm
 */
@ManagedBean
@SessionScoped
public class GastoBean {
    
    private Gasto gasto = new Gasto();
    private Local local = new Local();
    private Usuario usuario = new Usuario();
    private int projetoID;
    private int localID;
    //este é o usuário logado
   
    private boolean canEdit = false;
    private int pesquisaByMes;
    private int pesquisaByAno;
    private List<Gasto> listaGastosFiltrados;

  
    private boolean mostrarTabelaPesquisas = false;
    private boolean houveErro = false;
    private Integer IDUsuarioPesquisado;
    private Gasto gastoEditado = new Gasto();
    private String consultaSQL;
    private boolean permiteConsultaSQL = false;
    private List<Gasto> listaGastosTotais;
    private double gastosFiltrados;
    private double gastosTotais;
   private List<Gasto> listaGastosPesquisa;
   private boolean mostraTotal = false;

    public boolean isMostraTotal() {
        return mostraTotal;
    }

    public void setMostraTotal(boolean mostraTotal) {
        this.mostraTotal = mostraTotal;
    }

    
    
      public List<Gasto> getListaGastosFiltrados() {
        return listaGastosFiltrados;
    }

    public void setListaGastosFiltrados(List<Gasto> listaGastosFiltrados) {
        this.listaGastosFiltrados = listaGastosFiltrados;
    }


    public List<Gasto> getListaGastosTotais() {
          GastoJpaController gastoDAO = new GastoJpaController();
        this.listaGastosTotais = gastoDAO.listaGastosByConsultaSQL();
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
    

    public boolean isPermiteConsultaSQL() {
        return permiteConsultaSQL;
    }

    public void setPermiteConsultaSQL(boolean permiteConsultaSQL) {
        this.permiteConsultaSQL = permiteConsultaSQL;
    }

    public String getConsultaSQL() {
        return consultaSQL;
    }

    public void setConsultaSQL(String consultaSQL) {
        this.consultaSQL = consultaSQL;
    }

    public Integer getIDUsuarioPesquisado() {
        return IDUsuarioPesquisado;
    }

    public void setIDUsuarioPesquisado(Integer IDUsuarioPesquisado) {
        this.IDUsuarioPesquisado = IDUsuarioPesquisado;
    }

   

    public boolean isHouveErro() {
        return houveErro;
    }

    public void setHouveErro(boolean houveErro) {
        this.houveErro = houveErro;
    }

    public boolean isMostrarTabelaPesquisas() {
        return mostrarTabelaPesquisas;
    }

    public void setMostrarTabelaPesquisas(boolean mostrarTabelaPesquisas) {
        this.mostrarTabelaPesquisas = mostrarTabelaPesquisas;
    }

    public List<Gasto> getListaGastosPesquisa() {
        return listaGastosPesquisa;
    }

    public void setListaGastosPesquisa(List<Gasto> listaGastosPesquisa) {
        this.listaGastosPesquisa = listaGastosPesquisa;
    }
    
    

    public int getProjetoID() {
        return projetoID;
    }

    public void setProjetoID(int projetoID) {
        this.projetoID = projetoID;
    }

    public int getPesquisaByMes() {
        return pesquisaByMes;
    }

    public void setPesquisaByMes(int pesquisaByMes) {
        this.pesquisaByMes = pesquisaByMes;
    }

    public int getPesquisaByAno() {
        return pesquisaByAno;
    }

    public void setPesquisaByAno(int pesquisaByAno) {
        this.pesquisaByAno = pesquisaByAno;
    }
  
    

    public boolean isCanEdit() {
        return canEdit;
    }

    public void setCanEdit(boolean canEdit) {
        this.canEdit = canEdit;
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
    
      public Gasto getGastoEditado() {
        return gastoEditado;
    }

    public void setGastoEditado(Gasto gastoEditado) {
        this.gastoEditado = gastoEditado;
    }

    /**
     * Creates a new instance of GastoBean
     */
    public GastoBean() {
    }
    
    public void limparTabelaView(){
        this.mostrarTabelaPesquisas = false;
        this.listaGastosPesquisa = null;
        this.gastosTotais = 0;
       
    }
    
    public void mostraMensagemErro(String message){
        
    this.houveErro = true;
    FacesMessage fm = new FacesMessage( message);
    FacesContext.getCurrentInstance().addMessage("tabelaGastosPesquisados", fm);
    
    
    }
    
    
    public List<Gasto> listaGastosPesquisados(){
        
        if(this.listaGastosPesquisa == null){
            this.setMostrarTabelaPesquisas(false);
        }
        
        return this.listaGastosPesquisa;
        
    }
    


       public void verificaPermissaoPesquissaGastosSQL(){
           LoginFilter lf = new LoginFilter();

          if(lf.verificaPrivilegio()){

              this.permiteConsultaSQL = true;

          }
       }
       
       
       public void verificaGastosSQL(){
           
           GastoJpaController gastoDAO = new GastoJpaController();
           LoginFilter lf = new LoginFilter();
           if(lf.verificaPrivilegioSuperAdmin()){
           this.listaGastosPesquisa = gastoDAO.listaGastosByConsultaSQL();
           this.gastosTotais = gastoDAO.calculaGastosBySQL(this.consultaSQL);
           this.mostrarTabelaPesquisas = true;
           this.permiteConsultaSQL = false;
           }
           
       }

    
    public void verificaGastosMes() {
        
       LoginFilter lf = new LoginFilter();
       Usuario usuarioLogado = new Usuario();
       usuarioLogado = lf.getUsuario();
       GastoJpaController gastoDAO = new GastoJpaController();
        
       if(usuarioLogado.getPapel().isPrivAdmin()){
            
        this.gastosTotais = gastoDAO.calculaGastosMensais(this.pesquisaByMes, this.pesquisaByAno);
        this.listaGastosPesquisa = gastoDAO.listaGastosByMes(this.pesquisaByMes, this.pesquisaByAno);
        
       }else{
           
           this.gastosTotais = gastoDAO.calculaGastosMensaisUsuarioLogado(this.pesquisaByMes, this.pesquisaByAno, usuarioLogado.getIdUsuario());
           this.listaGastosPesquisa = gastoDAO.listaGastosByMesUsuarioLogado(this.pesquisaByMes, this.pesquisaByAno, usuarioLogado.getIdUsuario());
       }
       
       if(!houveErro){
     
           this.mostrarTabelaPesquisas = true;

       }
       
             
    }
    
    
      public void verificaGastosLocal(){
        
       LoginFilter lf = new LoginFilter();
       Usuario usuarioLogado = new Usuario();
       usuarioLogado = lf.getUsuario();
        GastoJpaController gastoDAO = new GastoJpaController();
        
        if(usuarioLogado.getPapel().isPrivAdmin()){
            
        this.gastosTotais = gastoDAO.calculaGastosByLocal(this.localID);        
        this.listaGastosPesquisa = gastoDAO.listaGastosByLocal(this.localID);
        
        }else {
            this.gastosTotais = gastoDAO.calculaGastosByLocalUsuarioLogado(localID, usuarioLogado.getIdUsuario());
            this.listaGastosPesquisa = gastoDAO.listaGastosByLocalUsuarioLogado(this.localID, usuarioLogado.getIdUsuario());
        }
        
        if (!houveErro){
             this.mostrarTabelaPesquisas = true;
        }
       
       
    }
      
      
       public void verificaGastosUsuario(){
           
       LoginFilter lf = new LoginFilter();
       Usuario usuarioLogado = new Usuario();
       usuarioLogado = lf.getUsuario();
       GastoJpaController gastoDAO = new GastoJpaController();
        
        if(usuarioLogado.getPapel().isPrivAdmin()){
            
           this.gastosTotais = gastoDAO.calculaGastosByUsuario(this.IDUsuarioPesquisado);
           this.listaGastosPesquisa = gastoDAO.listaGastosByUsuario(this.IDUsuarioPesquisado);
          
       }else {
            this.gastosTotais = gastoDAO.calculaGastosByUsuario(usuarioLogado.getIdUsuario());
            this.listaGastosPesquisa = gastoDAO.listaGastosByUsuarioLogado(usuarioLogado.getIdUsuario());
        }
        
        this.mostrarTabelaPesquisas = true;
        
        
       }
      
      
        public void verificaGastosProjeto(){
        
        LoginFilter lf = new LoginFilter();
        Usuario usuarioLogado = new Usuario();
        usuarioLogado = lf.getUsuario();
        GastoJpaController gastoDAO = new GastoJpaController();
        
        if(usuarioLogado.getPapel().isPrivAdmin()){
            
           this.gastosTotais = gastoDAO.calculaGastosByProjeto(this.projetoID);
           this.listaGastosPesquisa = gastoDAO.listaGastosByProjeto(this.projetoID);
            
        }else {
        this.gastosTotais = gastoDAO.calculaGastosByProjetoUsuarioLogado(this.projetoID, usuarioLogado.getIdUsuario());
        this.listaGastosPesquisa = gastoDAO.listaGastosByProjetoUsuarioLogado(this.projetoID, usuarioLogado.getIdUsuario());
        }
        
        if(!houveErro){
            this.mostrarTabelaPesquisas = true;
        }
       
    }
    
    
    
    public void adicionarGasto(){
    
        GastoJpaController gastoDAO = new GastoJpaController();
        boolean gravado = false;
          
         
          gasto.setLocal(local);
         
     
        if(gasto.getLocal()==null & gasto.getUsuario()==null){
            String mensagem1 = null;
             mensagem1 = "HOUVE UM PROBLEMA E O GASTO NÃO FOI GRAVADO POIS "
                     + "O LOCAL E/OU USUÁRIO ESTÃO NULOS";
            FacesMessage fm = new FacesMessage(mensagem1);
             FacesContext.getCurrentInstance().addMessage("gravaGasto", fm);
        }else{
         gravado =  gastoDAO.create(gasto);
         this.listaGastosTotais = gastoDAO.listaGastosByConsultaSQL();
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
        
       //this.gasto = new Gasto();
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
            
            this.usuario = usuarioDAO.findUsuario(IDUsuarioPesquisado);
            gasto.setUsuario(usuario);
        }
             
             public void editaGasto() throws Exception{
                 LoginFilter lf = new LoginFilter();
                 boolean possuiPrivilegio = lf.verificaPrivilegio();
                 
                 if(possuiPrivilegio){
                 this.canEdit = true;
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
                 
                 if(possuiPrivilegio & this.gastoEditado != null){
                 GastoJpaController gastoDAO = new GastoJpaController();
                 gastoDAO.edit(this.gastoEditado);
                 this.canEdit = false;
                 this.listaGastosTotais = gastoDAO.listaGastosByConsultaSQL();
            
               }
                
                
            }
        
        
             public void deletaGasto() throws NonexistentEntityException{
                 
                 LoginFilter lf = new LoginFilter();
                 boolean possuiPrivilegio = lf.verificaPrivilegio();
                 
                 if(possuiPrivilegio & this.gasto != null){
                 
                 GastoJpaController gastoDAO = new GastoJpaController();
                 gastoDAO.destroy(this.gasto.getId_gasto());
                 this.mostrarTabelaPesquisas = false;
                 this.listaGastosPesquisa = null;
                 this.gastosTotais = 0;
    
                 }
                 
             }
        
        public String verificaGastosTotais(){
            
            GastoJpaController gastoDAO = new GastoJpaController();
            LoginFilter lf = new LoginFilter();
            boolean isAdministrador = lf.verificaPrivilegio();
            Usuario usuarioLogado = lf.getUsuario();
           
            if(isAdministrador){
            this.listaGastosPesquisa = gastoDAO.findGastoEntities(); 
            this.gastosTotais = gastoDAO.calculaGastosTotais();
            }else{
                this.listaGastosPesquisa = gastoDAO.listaGastosByUsuarioLogado(usuarioLogado.getIdUsuario()); 
                this.gastosTotais  = gastoDAO.calculaGastosTotaisUsuarioLogado(usuarioLogado.getIdUsuario());
            }
                if(!houveErro){
                    this.mostrarTabelaPesquisas = true;
                }
            return "/restricted/gastos?faces-redirect=true" ;
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
         
        this.mostraTotal = true;
         
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
       
    

}

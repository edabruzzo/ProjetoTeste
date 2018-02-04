/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean;



import java.io.IOException;
import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import DAO.UsuarioJpaController;
import Default.CriptografiaSenha;
import helper.MessageHelper;
import modelo.Usuario;



/**
 *
 * @author Emm
 */
@Named
@ApplicationScoped
public class LoginFilter implements Filter, Serializable{
    

private static final long serialVersionUID = -8369585486567279810L;



@Inject
private MessageHelper helper;

@Inject
private FacesContext context;



private  boolean permiteAcesso = false;
private boolean mostra = false;

//  private static Usuario usuario = new Usuario();
  
       private static Usuario usuario = new Usuario();

       @Inject  
       private UsuarioJpaController usuarioDAO;
       
       private Usuario novoUsuario = new Usuario();
       
       @Inject
       private  CriptografiaSenha criptoSenha;         
         
    public boolean isPermiteAcesso() {
        return permiteAcesso;
    }

    public void setPermiteAcesso(boolean permiteAcesso) {
        this.permiteAcesso = permiteAcesso;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    
    
    
    
        public String validaAcesso(){
            
           String redireciona = "/security/login?faces-redirect=true";
           
         
            //faz a criptografia da senha entrada pelo usuário antes de 
           //gravar no banco
            
            String senhaCriptografada = criptoSenha.convertStringToMd5(this.usuario.getPassword());
           
            this.usuario.setPassword(senhaCriptografada);
            
            novoUsuario = usuarioDAO.findByLoginSenha(this.usuario.getLogin(), this.usuario.getPassword());
 
          
            try {
            
            if(novoUsuario == null){
                    apresentaMensagemErro("formLogin", "O PROCESSO DE LOGIN FALHOU ! USUÁRIO INEXISTENTE OU SENHA INCORRETA !");
            }else{
           
              /*LANÇA UMA EXCEÇÃO java.lang.UnsupportedOperationException
               params.put("usuarioLogado", novoUsuario.getNome());
               params.putIfAbsent("usuarioLogado", novoUsuario.getNome()); 
            	ESTOU OPTANDO POR RECEBER O USUÁRIO LOGADO NÃO PELO MAP DO CONTEXTO
            	DA SEÇÃO, MAS ATRAVÉS DE UM ATRIBUTO PRIVADO ESTÁTICO, QUE DEVE DURAR 
            	DURANTE TODA A EXECUÇÃO DA SESSÃO.*/
            
            	
              this.usuario = novoUsuario;
              
              //estou colocando o usuarioLogado no mapa da sessão
              this.context.getExternalContext().getSessionMap().put("usuarioLogado", this.usuario);

              this.permiteAcesso = true;
                redireciona = "/restricted/gastos?faces-redirect=true";
            }
            }catch(NullPointerException npex) {
            	
                apresentaMensagemErro("formLogin", "O PROCESSO DE LOGIN FALHOU ! USUÁRIO INEXISTENTE OU SENHA INCORRETA !");
            }
                
             return redireciona;
            
        }
        
        
        public void apresentaMensagemErro(String idElemento, String mensagemErro){
            
            helper.onFlash().addMessage(idElemento, mensagemErro);
            
        }
                
        
        public void apresentaMensagemErro(String mensagemErro){
            
            helper.onFlash().addMessage(mensagemErro); 
           
        } 
        
        
         public boolean verificaPapel(){
            
             boolean permitido = false;
             if (this.usuario != null & this.usuario.getPapel().isPrivAdmin()){
             permitido = true;
             }else {
           	 apresentaMensagemErro("Voce não possui privilégio de Administrador");
             }
            
             return permitido;
         }
         
         
         public boolean verificaPrivilegioSuperAdmin(){
             
             boolean possuiPrivilegioSuperAdmin = false;
             if (this.usuario != null & this.usuario.getPapel().isPrivSuperAdmin()){
             possuiPrivilegioSuperAdmin = true;
             }
            
             return possuiPrivilegioSuperAdmin;
         }
      
         
         
         public String redirecionaUsuarios(){
             String redireciona = null;
             boolean permitidoRedirecionamento = verificaPapel();
             if(permitidoRedirecionamento){
                 redireciona = "/restricted/usuarios?faces-redirect=true";
             }
             return redireciona;
         }
         
         
         
             public String redirecionaGraficos(){
             String redireciona = null;
             boolean permitidoRedirecionamento = verificaPapel();
             if(permitidoRedirecionamento){
                 redireciona = "/restricted/graficos?faces-redirect=true";
             }
             return redireciona;
         }
         
         
         
                  
         public String redirecionaPesquisas(){
             String redireciona = null;
             boolean permitidoRedirecionamento = verificaPapel();
             if(permitidoRedirecionamento){
                 redireciona = "/restricted/pesquisas?faces-redirect=true";
             }
             return redireciona;
         }
         
         
           public String redirecionaLocais(){
             String redireciona = null;
             boolean permitidoRedirecionamento = verificaPapel();
             if(permitidoRedirecionamento){
                 redireciona = "/restricted/locais?faces-redirect=true";
             }
             return redireciona;
         }
           
            public String redirecionaProjetos(){
             String redireciona = null;
             boolean permitidoRedirecionamento = verificaPapel();
             if(permitidoRedirecionamento){
                 redireciona = "/restricted/projetos?faces-redirect=true";
             }
             return redireciona;
         }
         
            
            
            
         
          public String logout(){
            
             this.permiteAcesso = false;
             String redireciona = "/security/login?faces-redirect=true";
             this.usuario = null;
             
             //estou removendo o usuarioLogado do mapa da sessão
             this.context.getExternalContext().getSessionMap().remove("usuarioLogado");

             this.usuario = new Usuario();
             return redireciona;
         }
          
          
          public boolean verificaPrivilegio(){
              
           boolean possuiPrivilegio = false;
                     
              if(this.usuario.getPapel().isPrivAdmin()){
                  possuiPrivilegio = true;
              }else {
            	  apresentaMensagemErro("Voce não possui privilégio de Administrador");
              }
              return possuiPrivilegio;
         }
          
          
            public boolean verificaUsuarioLogado(){

          /*  
           *   
           NÃO HÁ NECESSIDADE DE PEGAR O USUÁRIO PELO CONTEXTO, 
           POIS TENHO O LOGIN E A SENHA EM THIS.USUARIO.GETLOGIN()
           O atributo usuário é estático e, portanto, tem escopo de aplicação
          FacesContext fc = FacesContext.getCurrentInstance();
          Map<String, String> params = fc.getExternalContext().getRequestParameterMap();
         String loginUsuarioLogado = params.get("j_idt6:login");

             */
          
             if (this.usuario.getNome() != null){
                   this.setMostra(true);
             }else {
            	 this.setMostra(false);
             }
            return isMostra();
            }
            
            
            public void solicitarNovaSenha() throws Exception {
             
             //UsuarioJpaController usuarioDAO = new UsuarioJpaController();
             Usuario usuarioSemSenha = usuarioDAO.findByLogin(this.usuario.getLogin());
             if(usuarioSemSenha != null){
                 
             criptoSenha.gerarNovaSenha(usuarioSemSenha);
             
            String mensagem = "                                            "
                    + "                                                         "
                    + "                                       ************************"
                    + "************************************************************"
                    + "************************************************************"
                    + "                                                              "
                    + "                                                             "
                    + "NOVA SENHA ENVIADA PARA O EMAIL :"+usuarioSemSenha.getEmail()+"            "
                            + "                                                       "
                            + "                                                          "
                            + "                                                          "
                            + "                                                          "
                            + "***********************************************************"
                            + "***********************************************************"
                            + "***********************************************************";    
            
            apresentaMensagemErro("novaSenha", mensagem);

             }
            
             
         }
      
    
    
           @Override
           public void destroy() {
                     // TODO Auto-generated method stub
   
           }
           
           
           
           /*
           
           https://stackoverflow.com/questions/1026846/how-to-redirect-to-login-page-when-session-is-expired-in-java-web-application
           PARA MELHORAR O PROCESSO DE LOGIN
           
           
           */
           
           
   
           @Override
           public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
               
               HttpSession sess = ((HttpServletRequest) request).getSession(false);
               
              if(sess != null)  { 
                     if (permiteAcesso == false) {
                    
                    String contextPath = ((HttpServletRequest) request).getContextPath();
                              
                    ((HttpServletResponse) response).sendRedirect(contextPath + "/security/login.xhtml?faces-redirect=true");
                     }
                     
                    else {
                              chain.doFilter(request, response);
                     }
                }
           }
   
           @Override
           public void init(FilterConfig arg0) throws ServletException {
                     // TODO Auto-generated method stub
   
           }

		public boolean isMostra() {
			
			return mostraMenu();
		
		}

		public void setMostra(boolean mostra) {
			this.mostra = mostra;
		}
            

		public boolean mostraMenu() {
			
			
			try {
				
				if (LoginFilter.usuario.getNome() != null){
		             this.setMostra(true);
		       }			
				
				
			}catch(NullPointerException npe) {
				
				this.mostra = false;
				
			}

			return mostra;
			
		}
		
		
   
  }

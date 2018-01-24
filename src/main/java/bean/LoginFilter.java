/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean;



import DAO.UsuarioJpaController;
import Default.CriptografiaSenha;
import java.io.IOException;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import modelo.Usuario;



/**
 *
 * @author Emm
 */
@ManagedBean
@SessionScoped
public class LoginFilter implements Filter {
    
private static final long serialVersionUID = 1L;

private  boolean permiteAcesso = false;

  private static Usuario usuario = new Usuario();
  


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
           
            
            UsuarioJpaController usuarioDAO = new UsuarioJpaController();
            Usuario novoUsuario = new Usuario();
         
            //faz a criptografia da senha entrada pelo usuÃ¡rio antes de 
           //gravar no banco
            CriptografiaSenha criptoSenha = new CriptografiaSenha();
            String senhaCriptografada = criptoSenha.convertStringToMd5(this.usuario.getPassword());
            this.usuario.setPassword(senhaCriptografada);
            novoUsuario = usuarioDAO.findByLoginSenha(this.usuario.getLogin(), this.usuario.getPassword());
 
            if(novoUsuario == null){
                    apresentaMensagemErro("validaAcesso", "O PROCESSO DE LOGIN FALHOU ! USUÃ�RIO INEXISTENTE OU SENHA INCORRETA !");
            }else{
           
              //LANÃ‡A UMA EXCEÃ‡ÃƒO java.lang.UnsupportedOperationException
               //params.put("usuarioLogado", novoUsuario.getNome());
               // params.putIfAbsent("usuarioLogado", novoUsuario.getNome()); 
              this.usuario = novoUsuario;
              this.permiteAcesso = true;
                redireciona = "/restricted/gastos?faces-redirect=true";
            }   
                
             return redireciona;
            
        }
        
        
        public void apresentaMensagemErro(String idElemento, String mensagemErro){
            
            FacesMessage mensagem = new FacesMessage(mensagemErro);    
            FacesContext.getCurrentInstance().addMessage(idElemento, mensagem); 
            
            
        }
                
              public void apresentaMensagemErro(String mensagemErro){
            
            FacesMessage mensagem = new FacesMessage(mensagemErro);    
            FacesContext.getCurrentInstance().addMessage(null, mensagem); 
            
            
        } 
        
        
         public boolean verificaPapel(){
            
             boolean permitido = false;
             if (this.usuario != null & this.usuario.getPapel().isPrivAdmin()){
             permitido = true;
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
             this.usuario = new Usuario();
             return redireciona;
         }
          
          
          public boolean verificaPrivilegio(){
              
           boolean possuiPrivilegio = false;
         
                     
              if(this.usuario.getPapel().isPrivAdmin()){
                  possuiPrivilegio = true;
              }
              return possuiPrivilegio;
         }
          
          
            public boolean verificaUsuarioLogado(){

          /*    
//                NÃƒO HÃ� NECESSIDADE DE PEGAR PELO CONTEXTO, POIS TENHO O LOGIN E A SENHA 
  //              EM THIS.USUARIO.GETLOGIN()
                
                FacesContext fc = FacesContext.getCurrentInstance();
              Map<String, String> params = fc.getExternalContext().getRequestParameterMap();
              String loginUsuarioLogado = params.get("j_idt6:login");
             */
          
             if (this.usuario.getNome() != null){
                    return true;
             }
             else return false;
            
            }
            
               public void solicitarNovaSenha() throws Exception {
       
             
             UsuarioJpaController usuarioDAO = new UsuarioJpaController();
             Usuario usuarioSemSenha = usuarioDAO.findByLogin(this.usuario.getLogin());
             if(usuarioSemSenha != null){
                 
             CriptografiaSenha criptoSenha = new CriptografiaSenha();
             criptoSenha.gerarNovaSenha(usuarioSemSenha);
             
            FacesMessage mensagem = new FacesMessage("                                            "
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
                            + "***********************************************************");    
            FacesContext.getCurrentInstance().addMessage("novaSenha", mensagem);

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
           
           
          
   
  }

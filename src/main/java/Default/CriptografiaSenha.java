/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Default;

import DAO.UsuarioJpaController;
import DAO.exceptions.NonexistentEntityException;
import bean.LoginFilter;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import modelo.Usuario;
import util.CriptografaSenha;

/**
 *
 * @author Emm
 */
@Named
@RequestScoped
public class CriptografiaSenha implements Serializable{

	private static final long serialVersionUID = 4347824241169545934L;

	@Inject
	private  UsuarioJpaController usuarioDAO;
	
	@Inject
	private CriptografaSenha criptografaSenha;
	
	@Inject
	private LoginFilter lf;
             
            
            
         public void criptografaSenhaUsuario(Usuario usuario) throws Exception{
               
            String senhaCriptografada = criptografaSenha.convertStringToMd5(usuario.getPassword());
            usuario.setPassword(senhaCriptografada);
            usuarioDAO.edit(usuario);
                 
             }
            
        
             
         public void gravarNovaSenhaUsuario(Usuario usuario, String novaSenha){
        	 
        	   
        	   String novaSenhaCriptografada = criptografaSenha.convertStringToMd5(novaSenha);

        	   usuario.setPassword(novaSenhaCriptografada);
               
               try {
				usuarioDAO.edit(usuario);
			} catch (NonexistentEntityException e) {
				// TODO Auto-generated catch block
				lf.apresentaMensagemErro(e.getMessage());
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				lf.apresentaMensagemErro(e.getMessage());
			}
               
               enviarNovaSenhaEmailUsuario(novaSenha);
         }
             
             
           public void enviarNovaSenhaEmailUsuario(String novaSenha){
               
               //IMPLANTAR ENVIO DE E-MAIL
               System.out.println("A NOVA SENHA É : "+novaSenha);
               
           }
            
             
    }
    
    

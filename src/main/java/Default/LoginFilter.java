package Default;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.LoginBean;


public class LoginFilter implements Filter {
	
	@Inject
	private LoginBean lb;

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
              if (lb.isPermiteAcesso() == false) {
             
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

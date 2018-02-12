package Default;

import java.io.Serializable;
import java.util.Map;

import javax.enterprise.event.Observes;
import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.inject.Inject;

import jsfAnnotation.ScopeMap;
import jsfAnnotation.ScopeMap.Scope;
import modelo.Usuario;
import phaseListenerAnnotations.After;
import phaseListenerAnnotations.Phase;
import phaseListenerAnnotations.Phase.Phases;

public class Autorizador implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8661409548839265686L;

    @Inject
	private FacesContext context;
	
    @Inject @ScopeMap(Scope.SESSION)
    private Map<String, Object> sessionMap;
    
    @Inject
    private NavigationHandler handler;
	
	
	public void afterPhase(@Observes @After @Phase(Phases.RESTORE_VIEW)PhaseEvent evento) {
		
		String nomePagina =  context.getViewRoot().getViewId();
		
		System.out.println(nomePagina);
	
        if("login.xhtml".equals(nomePagina)) {
            return;
        }

        Usuario usuarioLogado = (Usuario) sessionMap.get("usuarioLogado");

        if(usuarioLogado != null) {
            return;
        }

        handler.handleNavigation(context, null, "/security/login?faces-redirect=true");
        context.renderResponse();
    }
		
		
	}
	
	
	
	


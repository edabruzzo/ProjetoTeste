
package bean;


 
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Model;
import javax.inject.Inject;

import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.DateAxis;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;

import DAO.GastoJpaController;
import modelo.Gasto;


@Model
public class ChartView implements Serializable {
 
    /**
	 * 
	 */
	private static final long serialVersionUID = 4776381042414260456L;

	private LineChartModel animatedModel1;
    private boolean mostraGrafico = false;

    //https://github.com/algaworks/curso-javaee-primefaces/blob/master/ResolvendoErroGraficoPrimeFaces/src/main/java/com/algaworks/pedidovenda/controller/GraficoPedidosCriadosBean.java
    private static DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    
    @Inject
    private GastoJpaController gastoDAO;        

/*    @Inject
    public ChartView(GastoJpaController gastoDAO) {
		super();
		this.gastoDAO = gastoDAO;
	}*/

    
    
    public boolean isMostraGrafico() {
        return mostraGrafico;
    }

    public void setMostraGrafico(boolean mostraGrafico) {
        this.mostraGrafico = mostraGrafico;
    }
  
    @PostConstruct
    public void init() {
        createAnimatedModels();
    }
 
    public LineChartModel getAnimatedModel1() {
       // animatedModel1 = (LineChartModel) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("model");
        return animatedModel1;
    }
 
  
    private void createAnimatedModels() {
        animatedModel1 = initLinearModel();
        animatedModel1.setTitle("GASTOS POR PROJETO");
        animatedModel1.setAnimate(true);
        animatedModel1.setLegendPosition("se");
        Axis yAxis = animatedModel1.getAxis(AxisType.Y);
        yAxis.setLabel("VALOR DO GASTO");
        yAxis.setMin(0);
        yAxis.setMax(5000);
        
       DateAxis axis = new DateAxis("DATA DO GASTO");
       axis.setTickAngle(-50);
       axis.setMax("2018-01-20");
       this.animatedModel1.getAxes().put(AxisType.X, axis);
       
       
        
    }
     
    private LineChartModel initLinearModel() {
        LineChartModel model = new LineChartModel();
 
        LineChartSeries series1 = new LineChartSeries();
        series1.setLabel("CIDADANIA");
        series1.setFill(true);
        
        //ao invés de instanciar aqui o objeto, passar a responsabilidade para o CDI
        //através de um atributo de classe e um construtor
        //GastoJpaController gastoDAO = new GastoJpaController();        
        List<Gasto> listaGastos = gastoDAO.listaGastosByProjeto(1);

       
        
        for (Gasto gasto : listaGastos){
            
        series1.set(DATE_FORMAT.format(gasto.getDataGasto()), gasto.getValorGasto());
                
        }
 
        model.addSeries(series1);
     
     //   FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("model", model);
        return model;
    }
}
    
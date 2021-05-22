package org.insa.graphs.algorithm.shortestpath;

import org.insa.graphs.algorithm.AbstractInputData.Mode;
import org.insa.graphs.model.*;
//import org.insa.graphs.model.Label;

public class AStarAlgorithm extends DijkstraAlgorithm {

    public AStarAlgorithm(ShortestPathData data) {
        super(data);
    }

    protected void Initialisation(Label[] Labels, Graph graph, ShortestPathData data) {
    	//cas du plus court chemin en temps
    	if (data.getMode() == Mode.TIME) {
    		double vitesse = (double) data.getGraph().getGraphInformation().getMaximumSpeed()/3.6 ; //car on a des km/h et la distance est en m
    		for (int i = 0 ; i < graph.size(); i++) {
        		//distance à vol d'oiseau = distance entre deux points
        		double distance = Point.distance(graph.get(i).getPoint(), data.getDestination().getPoint());
            	Labels[i] = new LabelStar(i, false, Double.POSITIVE_INFINITY, null, distance/vitesse) ;
            }     
    	}
    	//cas du plus court chemin en distance
    	else {
	    	for (int i = 0 ; i < graph.size(); i++) {
	    		//distance à vol d'oiseau = distance entre deux points
	    		double distance = Point.distance(graph.get(i).getPoint(), data.getDestination().getPoint());
	        	Labels[i] = new LabelStar(i, false, Double.POSITIVE_INFINITY, null, distance) ;
	        } 
    	}
    	
   }
}

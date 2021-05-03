package org.insa.graphs.algorithm.shortestpath;

import java.util.ArrayList;

import org.insa.graphs.algorithm.utils.*;

import org.insa.graphs.model.*;

public class DijkstraAlgorithm extends ShortestPathAlgorithm {

	private ArrayList<Label> Labels;
	
	private BinaryHeap<Label> tas = new BinaryHeap<Label>();
	
    public DijkstraAlgorithm(ShortestPathData data) {
        super(data);
    }

    @Override
    protected ShortestPathSolution doRun() {
        final ShortestPathData data = getInputData();
        ShortestPathSolution solution = null;
        Graph graph = data.getGraph();
        
        //initialisation du tableau de labels 
        //on ajoute un label pour chaque node
        for (int i = 0 ; i < graph.size(); i++) {
        	this.Labels.add(new Label(i, false, Integer.MAX_VALUE, null));
        }
        
        //on met le cout du sommet d'origine à 0
        Node s = data.getOrigin();
        Label new_s = new Label(s.getId(), false, 0, null);
        Labels.set(s.getId(), new_s);
        
        //on ajoute ce sommet au tas 
        tas.insert(new_s);
        
        //tant qu'il y a des sommets marqués à faux
        boolean done = true;
        for (Label l : Labels) {
        	if (!l.getMarque()) {
        		done = false ;
        		break;
        	}
        }
        
        while (!done) {
        	Label label_x = tas.findMin();
        	label_x.setMarque(true);
        	
        	int id_x = label_x.getSommetCourant();
        	Node node_x = graph.get(id_x);
        	//on parcourt les successeurs de x
        	for (Arc a : node_x.getSuccessors()) {
        		Node node_y = a.getDestination();
        		Label label_y = Labels.get(node_y.getId());
        		if (!label_y.getMarque()) {
        			//si ils ne sont pas déjà marqués, on calcule le nouveau cout
        			double new_cost = Double.min(label_y.getCost(), label_x.getCost()+data.getCost(a));
                	//si le nouveau cout est différent de l'ancien on màj
        			if (new_cost < label_y.getCost()) {
        				
        				//modifier le cout et le pere dans Labels
        				//Label new_label_y = new Label(l'id, false, nouveau cout, nouveau pere)
        				//Labels.set(l'id, new_label_y)
        				//peut-être remplacer la ArrayList Labels par just un Array
        				tas.insert(label_y);
        			}
        		}
        	}
        	
        }
        
        return solution;
    }

}

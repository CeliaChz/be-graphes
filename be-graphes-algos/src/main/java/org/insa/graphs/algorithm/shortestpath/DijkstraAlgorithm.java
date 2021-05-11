package org.insa.graphs.algorithm.shortestpath;

import java.util.ArrayList;
import java.util.Collections;
import org.insa.graphs.algorithm.AbstractSolution.Status;
import org.insa.graphs.algorithm.utils.*;
import org.insa.graphs.model.*;

public class DijkstraAlgorithm extends ShortestPathAlgorithm {
	
	private BinaryHeap<Label> tas = new BinaryHeap<Label>();
	
    public DijkstraAlgorithm(ShortestPathData data) {
        super(data);
    }

    @Override
    protected ShortestPathSolution doRun() {
        ShortestPathData data = getInputData();
        ShortestPathSolution solution = null;
        Graph graph = data.getGraph();
        
        //initialisation du tableau de labels 
        //on ajoute un label pour chaque node
        Label[] Labels = new Label[graph.size()];
        for (int i = 0 ; i < graph.size(); i++) {
        	Labels[i] = new Label(i, false, Double.POSITIVE_INFINITY, null) ;
        }
        
        //on met le cout du sommet d'origine à 0
        Labels[data.getOrigin().getId()].setCost(0);
        //on ajoute ce sommet au tas 
        tas.insert(Labels[data.getOrigin().getId()]);
        this.notifyOriginProcessed(data.getOrigin());
               
        //tant qu'il existe des sommets non marqués 
        while (!Labels[data.getDestination().getId()].getMarque()) {
        	Label label_x ;
        	try {
        		label_x = tas.deleteMin();
        	}
        	catch (EmptyPriorityQueueException e) {
        		break ;
        	}
        	
        	int id_x = label_x.getSommetCourant();
        	Node node_x = graph.get(id_x);
        	
        	Labels[id_x].setMarque(true);
        	this.notifyNodeMarked(node_x);
        	
        	if (node_x==data.getDestination()) {
        		this.notifyDestinationReached(data.getDestination());
        	}
        	
        	else {
        		//on parcourt les successeurs de x
            	for (Arc a : node_x.getSuccessors()) {
            		if (data.isAllowed(a)) {
            		Node node_y = a.getDestination();
            		int node_y_id = node_y.getId();
            		Label label_y = Labels[node_y_id];
            		if (!label_y.getMarque()) {
            			//si ils ne sont pas déjà marqués, on calcule le nouveau cout
            			double new_cost = Double.min(label_y.getCost(), label_x.getCost()+data.getCost(a));
            			if (new_cost < label_y.getCost()) {
            				//si le nouveau cout est différent de l'ancien on le màj
            				if (Labels[node_y_id].getCost()!=Double.POSITIVE_INFINITY) {
            					//y était déjà dans le tas donc on l'enlève pour le màj
            					Labels[node_y_id].setCost(new_cost);
            					tas.remove(Labels[node_y_id]);
            					tas.insert(label_y);
            					//on màj son père
                				Labels[node_y_id].setPere(a);
            				}
            				else {
            					Labels[node_y_id].setCost(new_cost);
                				tas.insert(label_y);
                				Labels[node_y_id].setPere(a);
                				this.notifyNodeReached(node_y);
            				}
            				
            			}
            		}
            	}
            }
        		
        	}
        	
        	
        }
        
        ArrayList<Arc> arcs = new ArrayList<>();
        Node destination = data.getDestination();
        Label Labels_dest = Labels[destination.getId()];
        Arc arc = Labels_dest.getPere();
        
        if (arc==null) {
        	solution = new ShortestPathSolution(data, Status.INFEASIBLE);
        }
        else {
        	while (arc != null) {
                arcs.add(arc);
                arc = Labels[arc.getOrigin().getId()].getPere();
            }

            // Reverse the path...
            Collections.reverse(arcs);

            // Create the final solution.
            solution = new ShortestPathSolution(data, Status.OPTIMAL, new Path(graph, arcs));
        }
        
        return solution;
    }
}


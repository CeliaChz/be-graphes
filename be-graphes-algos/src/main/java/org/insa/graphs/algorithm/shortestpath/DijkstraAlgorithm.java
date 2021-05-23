package org.insa.graphs.algorithm.shortestpath;

import java.util.ArrayList;
import java.util.Collections;
import org.insa.graphs.algorithm.AbstractSolution.Status;
import org.insa.graphs.algorithm.utils.*;
import org.insa.graphs.model.*;

public class DijkstraAlgorithm extends ShortestPathAlgorithm {
	
    public DijkstraAlgorithm(ShortestPathData data) {
        super(data);
    }

    //nécessaire pour implémenter A* ensuite 
    protected void Initialisation(Label[] Labels, Graph graph, ShortestPathData data) {
    	 for (int i = 0 ; i < graph.size(); i++) {
         	Labels[i] = new Label(i, false, Double.POSITIVE_INFINITY, null) ;
         }
    }
    
    @Override
    protected ShortestPathSolution doRun() {
    	final ShortestPathData data = getInputData();
        ShortestPathSolution solution = null;
        Graph graph = data.getGraph();
        
        //Variables pour la partie vérification
        double ancien_cout = 0;
        int nb_explores = 0 ;
        
        //initialisation du tableau de labels 
        //on ajoute un label pour chaque node
        Label[] Labels = new Label[graph.size()];
        this.Initialisation(Labels, graph, data);
        
        //on met le cout du sommet d'origine à 0
        Labels[data.getOrigin().getId()].setCost(0);
        Labels[data.getOrigin().getId()].setMarque(true);
        //on ajoute ce sommet au tas 
        BinaryHeap<Label> tas = new BinaryHeap<Label>();
        tas.insert(Labels[data.getOrigin().getId()]);
        this.notifyOriginProcessed(data.getOrigin());
               
        //tant qu'il existe des sommets non marqués 
        while (!Labels[data.getDestination().getId()].getMarque()&&(!tas.isEmpty())) {
        	Label label_x ;
        	try {
        		label_x = tas.findMin();
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
        		//Vérification que les coûts des labels marqués sont croissants
        		/*if(ancien_cout > label_x.getTotalCost()) { 
            		System.out.println("Les coûts des Labels marqués ne sont pas croissants.");
            	}*/
            	ancien_cout = label_x.getTotalCost();
            	tas.remove(label_x);
            	
        		//on parcourt les successeurs de x
            	nb_explores ++ ;
            	for (Arc a : node_x.getSuccessors()) {
            		if (data.isAllowed(a)) {
            		Node node_y = a.getDestination();
            		int node_y_id = node_y.getId();
            		Label label_y = Labels[node_y_id];
            		if (!label_y.getMarque()) {
            			//si ils ne sont pas déjà marqués, on calcule le nouveau cout
            			double new_cost = Double.min(label_y.getCost(), label_x.getCost()+ data.getCost(a));
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
         //System.out.println("Le tas est encore valide : " + tas.isValid());
        }
        }
               
        ArrayList<Arc> arcs = new ArrayList<>();
        Node destination = data.getDestination();
        Label Labels_dest = Labels[destination.getId()];
        Arc arc = Labels_dest.getPere();
        
        double longueurDijkstra = -1 ;
        if (arc==null) {
        	solution = new ShortestPathSolution(data, Status.INFEASIBLE);
        }
        else {
        	longueurDijkstra =  Labels[data.getDestination().getId()].getTotalCost();
        	while (arc != null) {
                arcs.add(arc);
                arc = Labels[arc.getOrigin().getId()].getPere();
            }

            // Reverse the path...
            Collections.reverse(arcs);

            // Create the final solution.
            solution = new ShortestPathSolution(data, Status.OPTIMAL, new Path(graph, arcs));
            
            Path path = new Path(graph, arcs);
            
            //on vérifie que le chemin est valide avec la classe path
            System.out.println("Le chemin est valide : " + solution.getPath().isValid());
            //comparaison de la longueur de la solution trouvée par l'algorithme avec celle de path
            //on convertit les deux longueurs en entiers car elles utilisent des arrondis différents
            if ((int)longueurDijkstra==(int)path.getLength()) {
            	System.out.println("La longueur avec la classe Path est la même que celle de l'algorithme" );
            }
        }
         
        //on vérifie que le tas est encore valide
        //(seulement à la fin car perte de performance sinon)
        System.out.println("Le tas est encore valide : " + tas.isValid());
        
        //affichage du nombre de nodes explorés
        System.out.println("Le nombre de node explorés est : " + nb_explores + " pour " + data.getGraph().size() + " node(s).");	
        System.out.println();
        return solution;
    }
}


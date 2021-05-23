package org.insa.graphs.algorithm.utils;

import static org.junit.Assert.assertEquals;

import java.io.*;
import java.util.*;
import org.insa.graphs.algorithm.*;
import org.insa.graphs.algorithm.AbstractSolution.Status;
import org.insa.graphs.algorithm.shortestpath.*;
import org.insa.graphs.model.*;
import org.insa.graphs.model.io.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;


@RunWith(Parameterized.class)
public class DijkstraTest {
	//Chemin 1 : INSA Bikini
	private static Graph graphe1;
	//Chemin 2 : Guyane - chemin entre Cayenne et une île (inaccessible)
	private static Graph graphe2;
	
	@Parameters
    public static Collection<ShortestPathData> data() {
        final String map1 = "C:/Users/chauz/Documents_non_drive/INSA/3A/S6/BE_Graphes/be-graphes/maps/europe/france/haute-garonne.mapgr";
        final String map2 = "C:/Users/chauz/Documents_non_drive/INSA/3A/S6/BE_Graphes/be-graphes/maps/europe/france/guyane.mapgr";
        
        try {
        	final GraphReader reader1 = new BinaryGraphReader(new DataInputStream(new BufferedInputStream(new FileInputStream(map1))));
        	graphe1 = reader1.read();
        	final GraphReader reader2 = new BinaryGraphReader(new DataInputStream(new BufferedInputStream(new FileInputStream(map2))));
        	graphe2 = reader2.read();
        }
        catch(Exception e){
        	System.out.println("Erreur de lecture de la carte.");
        }

        //La liste Filtres contient toutes les possibilités de reccherche de PCC
        //ex : le premier est "shortest path all roads allowed"
        //le deuxième est "shortest path only roads open for cars" etc
        List<ArcInspector> Filtres = ArcInspectorFactory.getAllFilters() ;
    	
        Collection<ShortestPathData> objects = new ArrayList<>();
		//Shortest path all roads allowed
        //Départ=Arrivée
        objects.add(new ShortestPathData(graphe1, graphe1.get(10991), graphe1.get(10991), Filtres.get(0)));
        //Chemin INSA Bikini
        objects.add(new ShortestPathData(graphe1, graphe1.get(10991), graphe1.get(63104), Filtres.get(0)));
        
        //Fastest path all roads allowed
        //Départ=Arrivée
        objects.add(new ShortestPathData(graphe1, graphe1.get(10991), graphe1.get(10991), Filtres.get(2)));
        //Chemin INSA Bikini
        objects.add(new ShortestPathData(graphe1, graphe1.get(10991), graphe1.get(63104), Filtres.get(2)));
        
		//Chemin inaccessible (entre Cayenne et une île) - shortest et fastest paths
        objects.add(new ShortestPathData(graphe2, graphe2.get(9999), graphe2.get(16), Filtres.get(0)));
        objects.add(new ShortestPathData(graphe2, graphe2.get(9999), graphe2.get(16), Filtres.get(2)));
        return objects;
	}
	
	 @Parameter
	    public ShortestPathData parameters;
	
	//Test 1 : Dijkstra et A* sur un chemin sans solution
	@Test
	public void NoSolution() {
	    Assume.assumeTrue(parameters.getGraph().getMapName() == graphe2.getMapName());
	    ShortestPathAlgorithm Dijkstra = new DijkstraAlgorithm(parameters);
		AStarAlgorithm AStar = new AStarAlgorithm(parameters);
		assertEquals(Dijkstra.run().getStatus(), Status.INFEASIBLE);
		assertEquals(AStar.run().getStatus(), Status.INFEASIBLE);	
	}
	
	//Test 2 : Dijkstra et A* sur un chemin avec un seul node
	@Test
	public void OneNodePath() {
		Assume.assumeTrue(parameters.getOrigin() == parameters.getDestination() && parameters.getGraph().getMapName() != graphe2.getMapName());
		ShortestPathAlgorithm Dijkstra = new DijkstraAlgorithm(parameters);
		AStarAlgorithm AStar = new AStarAlgorithm(parameters);
		assertEquals(Dijkstra.run().getStatus(), Status.INFEASIBLE);
		assertEquals(AStar.run().getStatus(), Status.INFEASIBLE);
	 }
	
	//Test 3 : Dijkstra et A* sur le chemin INSA Bikini all roads allowed
	//Comparaison en temps et en distance avec Bellman Ford
	@Test
	public void BikiniAllRoadsAllowed() {
		Assume.assumeFalse(parameters.getOrigin() == parameters.getDestination() || parameters.getGraph().getMapName() == graphe2.getMapName());
		ShortestPathAlgorithm Bellman = new BellmanFordAlgorithm(parameters);
		ShortestPathAlgorithm Dijkstra = new DijkstraAlgorithm(parameters);
	 	AStarAlgorithm AStar = new AStarAlgorithm(parameters);
	 	ShortestPathSolution BellmanSolution = Bellman.run();
	 	
		//on fixe un delta de 0.01 pour vérifier que les solutions solutions trouvées sont les mêmes à cause des arrondis
		//on compare la longueur des paths trouvés 
		assertEquals(Dijkstra.run().getPath().getLength(), BellmanSolution.getPath().getLength(), 0.01 );	
		assertEquals(AStar.run().getPath().getLength(), BellmanSolution.getPath().getLength(), 0.01);	
		//on compare le temps de trajet des deux paths trouvés 
		assertEquals( Dijkstra.run().getPath().getMinimumTravelTime(), BellmanSolution.getPath().getMinimumTravelTime(), 0.01);	
		assertEquals(AStar.run().getPath().getMinimumTravelTime(), BellmanSolution.getPath().getMinimumTravelTime(), 0.01);
	}
	 	
	//Test 4 : Dijkstra et A* sur le chemin INSA Bikini, seulement les routes pour les voitures
	//Comparaison distance avec Bellman Ford
	@Test
    public void BikiniOnlyRoadOpenForCarsDistance() {
    	ShortestPathData data = new ShortestPathData(graphe1, graphe1.get(10991), graphe1.get(63104), ArcInspectorFactory.getAllFilters().get(1));
    	ShortestPathAlgorithm Bellman = new BellmanFordAlgorithm(data);
    	ShortestPathAlgorithm Dijkstra = new DijkstraAlgorithm(data);
    	ShortestPathAlgorithm AStar = new AStarAlgorithm(data);
    	ShortestPathSolution BellmanSolution = Bellman.run();
        assertEquals(BellmanSolution.getPath().getLength(),Dijkstra.run().getPath().getLength(), 0.01);
        assertEquals(BellmanSolution.getPath().getLength(),AStar.run().getPath().getLength(),0.01);
    }
	
	//Test 5 : Dijkstra et A* sur le chemin INSA Bikini, seulement les routes pour les voitures
	//Comparaison temps avec Bellman Ford
    @Test
    public void BikiniOnlyRoadOpenForCarsTime() {
    	ShortestPathData data = new ShortestPathData(graphe1, graphe1.get(10991), graphe1.get(63104), ArcInspectorFactory.getAllFilters().get(3));
    	ShortestPathAlgorithm Bellman = new BellmanFordAlgorithm(data);
    	ShortestPathAlgorithm Dijkstra = new DijkstraAlgorithm(data);
    	ShortestPathAlgorithm AStar = new AStarAlgorithm(data);
    	ShortestPathSolution BellmanSolution = Bellman.run();
        assertEquals(BellmanSolution.getPath().getLength(),Dijkstra.run().getPath().getLength(), 0.01);
        assertEquals(BellmanSolution.getPath().getLength(),AStar.run().getPath().getLength(),0.01);
    }
    
	
	//Note sur les tests d'optimalité sans oracle 
	
	//Possibilité 1 : vérifier que les sous-chemins de la solution sont bien des PCC
	//car les sous-chemin d'un PCC sont des PCC
	//si cette condition n'est pas respectée alors la solution n'est pas optimale
	//mais si cette condition est respectée, nous ne pouvons pas en conclure que la solution est optimale
    
    //Possibilté 2 : comparer le coût de la solution trouvée avec le coût estimé
    //Cependant le coût estimé peut être éloigné du coût minimal réel
    //on pourrait ajouter un delta et vérifier que : coût solution trouvée <= coût estimé + delta
    //Mais il y a toujours un problème : on n'a pas d'informations pour fixer delta
    //Dans certains cas plusieurs solutions vérifieront l'inégalité, et pas seulement la solution optimale
    //dans d'autres cas, si delta est trop petit, même la solution optimale ne vérifiera pas l'inégalité
    
    //Possibilité 3 : comparer le coût du chemin "Départ-Destination" et celui du chemin "Destination-Départ"
    //c'est-à-dire comparer l'aller et le retour
    //Ainsi, on pourrait se dire que si le coût d'un des deux chemins est inférieur à l'autre, 
    //alors sa solution n'est pas optimale.
    //Mais en réalité le coût des chemins aller et retour peuvent être différents 
    //en cas de routes à sens unique par exemple. On s'en rend compte sur le trajet INSA Bikini
	
}
	
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
	
	//TEST DE DIJKSTRA
	//Test 1 : Dijkstra sur un chemin sans solution
	@Test
	public void DijkstraNoSolution() {
	    Assume.assumeTrue(parameters.getGraph().getMapName() == graphe2.getMapName());
	    ShortestPathAlgorithm Dijkstra = new DijkstraAlgorithm(parameters);
		assertEquals(Dijkstra.run().getStatus(), Status.INFEASIBLE);	
	}
	
	//Test 2 : Dijkstra sur un chemin avec un seul node
	@Test
	public void DijkstraOneNodePath() {
		Assume.assumeTrue(parameters.getOrigin() == parameters.getDestination() && parameters.getGraph().getMapName() != graphe2.getMapName());
		ShortestPathAlgorithm Dijkstra = new DijkstraAlgorithm(parameters);
		assertEquals(Dijkstra.run().getStatus(), Status.INFEASIBLE);	
	 }
	
	//Test 3 : Dijkstra sur le chemin INSA Bikini
	//Comparaison en temps et en distance avec Bellman Ford
	@Test
	public void DijkstraBikini() {
		Assume.assumeFalse(parameters.getOrigin() == parameters.getDestination() || parameters.getGraph().getMapName() == graphe2.getMapName());
		ShortestPathAlgorithm Dijkstra = new DijkstraAlgorithm(parameters);
		ShortestPathAlgorithm Bellman = new BellmanFordAlgorithm(parameters);
		assertEquals(Dijkstra.run().getPath().getLength(), Bellman.run().getPath().getLength(), 0.01 );	
		assertEquals( Dijkstra.run().getPath().getMinimumTravelTime(), Dijkstra.run().getPath().getMinimumTravelTime(), 0.01);	
	}
	 
	
	//TEST DE A*
	//Test 4 : A* sur un chemin sans solution
	@Test
	public void AStarNoSolution() {
	    Assume.assumeTrue(parameters.getGraph().getMapName() == graphe2.getMapName() );
	 	AStarAlgorithm AStar = new AStarAlgorithm(parameters);
		assertEquals(AStar.run().getStatus(), Status.INFEASIBLE);	
	}
	
	
	//Test 5 : A* sur un chemin avec un seul node
	@Test
	public void AStarOneNodePath() {
		Assume.assumeTrue(parameters.getOrigin() == parameters.getDestination() && parameters.getGraph().getMapName() != graphe2.getMapName());
		AStarAlgorithm AStar = new AStarAlgorithm(parameters);
		assertEquals(AStar.run().getStatus(), Status.INFEASIBLE);
	}
	
	//Test 6 : A* sur le chemin INSA Bikini
	//Comparaison en temps et en distance avec Bellman Ford
	@Test
	public void AStarBikini() {
		Assume.assumeFalse(parameters.getOrigin() == parameters.getDestination() || parameters.getGraph().getMapName() == graphe2.getMapName());
	 	AStarAlgorithm AStar = new AStarAlgorithm(parameters);
		ShortestPathAlgorithm Bellman = new BellmanFordAlgorithm(parameters);
		//on compare la longueur des deux paths trouvés 
		assertEquals(AStar.run().getPath().getLength(), Bellman.run().getPath().getLength(), 0.01 );	
		//on compare le temps de trajet des deux paths trouvés 
		assertEquals(AStar.run().getPath().getMinimumTravelTime(), Bellman.run().getPath().getMinimumTravelTime(), 0.01);
	}
	
}
	
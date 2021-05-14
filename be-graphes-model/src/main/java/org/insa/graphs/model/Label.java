package org.insa.graphs.model;

public class Label implements Comparable<Label>{
	//numéro du sommet associé au label
	private int sommet_courant ;
	//bool vrai lorsque l'algo connait le cout min du sommet
	private boolean marque ;
	//valeur courante du plus court chemin depuis l'origine vers ce sommet
	private double cout ;
	//Arc menant au sommet précédent correspondant au + court chemin courant 
	private Arc pere;

	//constructeur 
	public Label(int S, boolean M, double C, Arc P) {
		this.sommet_courant = S;
		this.marque = M;
		this.cout = C ;
		this.pere = P ;
	}
	
	//retourne le cout du label
	public double getCost() {
		return this.cout;
	}
	
	public void setCost(double new_cost) {
		this.cout = new_cost;
	}
	
	public boolean getMarque() {
		return this.marque ;
	}
	
	public void setMarque(boolean new_marque) {
		this.marque = new_marque ;
	}
	
	public Arc getPere() {
		return this.pere ;
	}
	
	public void setPere(Arc new_pere) {
		this.pere = new_pere;
	}
	
	public int getSommetCourant() {
		return this.sommet_courant;
	}
	
	public double getTotalCost() {
		return this.cout ;
	}
	
	public double getEstimatedCost() {
		return 0;
	}
	
	
	//On ordonne les noeuds selon l'ordre (coût depuis l'origine + coût estimé à la destination) croissant
	//càs coût total croissant
	//En cas d'égalité, le sommet ayant le plus petit coût estimé à la destination sera le premier
	public int compareTo(Label other) {
		//le cout total de l'autre noeud est plus grand => return -1
		if (this.getTotalCost() < other.getTotalCost()) {
			return -1 ;
		}
		//le cout total du noeud et de l'autre noeud sont égaux
		else if (this.getTotalCost() == other.getTotalCost()) {
			//l'autre noeud a un cout estimé plus grand => -1
			if (this.getEstimatedCost() < other.getEstimatedCost()) {
				return -1;
			}
			//ils ont le même cout estimé => 0
			else if (this.getEstimatedCost() == other.getEstimatedCost()) {
				return 0 ;
			}
			//l'autre noeud a un cout estimé plus petit => 1
			else {
				return 1;
			}
		}
		//le cout total de l'autre noeud est plus petit => return 1
		else {
			return 1 ;
		}
	}
	
}


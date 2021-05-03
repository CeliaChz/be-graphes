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
	
	public int compareTo(Label other) {
		return Double.compare(getCost(), other.getCost());
	}
	
}


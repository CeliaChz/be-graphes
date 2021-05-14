package org.insa.graphs.model;


public class LabelStar extends Label {
	
	private double estimatedCost;

	public LabelStar(int S, boolean M, double C, Arc P, double EC) {
		super(S, M, C, P);
		this.estimatedCost = EC;
	}
	
	public double getEstimatedCost() {
		return estimatedCost;
	}
	
	public void setEstimatedCost(double EC) {
		this.estimatedCost = EC ;
	}
	
	public double getTotalCost() {
		return this.estimatedCost + super.getCost();
	}
}
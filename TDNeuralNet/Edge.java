/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package TDNeuralNet;

/**
 *
 * @author NinjaKL
 */
public class Edge {
    Neuron from,to;
    float weight;
    
    public Edge(Neuron inFrom,Neuron inTo,float inWeight){
        from = inFrom;
        to = inTo;
        weight = inWeight;
    }
}
// PROG2 VT2025, Inlämningsuppgift, del 1
// Grupp 067
// Oskar Persson ospe4502
// Felix Warkmark fewa5233
// Anton Sahlén 0433

package se.su.inlupp;

import java.util.Objects;

public class Edge<T> {

  private final T destination;
  private final String name;
  private int weight;

public Edge (T destination, String name, int weight ) {
  if (weight < 0) {
    throw new IllegalArgumentException(" Vikten får inte vara negativ ");
  }
  this.destination = Objects.requireNonNull(destination);
  this.name = Objects.requireNonNull(name);
  this.weight = weight;
}

  public int getWeight(){
  return weight;
  }

 public void setWeight(int weight){
  if (weight < 0) {
    throw new IllegalArgumentException("Vikten får inte vara negativ");
  }
  this.weight = weight;
 }

 public T getDestination() {
  return destination;
 }

 public String getName(){
  return name;
  }

  @Override
  public String toString() {
      return "till " + destination + " med " + name + " tar " + weight;
  }



                                                    // Se över!!!


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Edge<?>) {
            Edge<?> edge = (Edge<?>) obj;
            return Objects.equals(destination, edge.destination) &&
                    Objects.equals(name, edge.name);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(destination, name);
    }

}

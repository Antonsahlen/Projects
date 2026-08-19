import java.util.*;
import java.util.ArrayList;
public class DogTailComparatorTestProgram {
	
	    public static void main(String[] args) {
	    	   ArrayList<Dog> dogs = new ArrayList<>();
	           dogs.add(new Dog("Karo", "Labrador", 7, 25));
	           dogs.add(new Dog("Rex", "Bulldog", 8, 30));
	           dogs.add(new Dog("Fido", "Poodle", 10, 28));
	           dogs.add(new Dog("Kisen", "Husky", 7, 25));

	           // Använd DogTailComparator för att sortera
	           Collections.sort(dogs, new DogTailComparator());

	           // Skriv ut den sorterade listan
	           System.out.println("Dogs sorted by tail length and name:");
	           for (Dog dog : dogs) {
	               System.out.println(dog);
	           }
	       }
	   }
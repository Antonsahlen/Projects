
public class DogCollectionTestProgram {
	
	
	public static void main(String[] args) {
	DogCollection dogCollection = new DogCollection();

	
		
	Dog dog1 = new Dog("Buddy", "Boston Terrier", 10, 15);
    Dog dog2 = new Dog("Charlie", "Terrier", 15 , 5);
    Dog dog3 = new Dog("Max", "Tax", 5, 4);
    Dog dog4 = new Dog("Anton", "Schäfer", 3, 2);
	

	System.out.println("Adding Buddy: " + dogCollection.addDog(dog1)); // Expected: true
    System.out.println("Adding Charlie: " + dogCollection.addDog(dog2)); // Expected: true
    System.out.println("Adding Buddy again: " + dogCollection.addDog(dog1)); // Expected: true (already exists)
    System.out.println("Adding Anton: " + dogCollection.addDog(dog4));
    //System.out.println("Ta bort Anton: " + dogCollection.removeDog(dog4));
    
    System.out.println("Contains Buddy: " + dogCollection.containsDog(dog1)); // Expected: true
    System.out.println("Contains Charlie: " + dogCollection.containsDog(dog2)); // Expected: true
    System.out.println("Contains Max: " + dogCollection.containsDog(dog3)); // Expected: false
    System.out.println("Contains Anton: " + dogCollection.containsDog(dog4));
    
    
    System.out.println("visa hela listan : " + dogCollection.getDogs());
    
}
}

// Anton Sahlén ansa0433

import java.util.ArrayList;

public class DogCollection {

    private final ArrayList<Dog> dogs = new ArrayList<>();        //final för att listan alltid finns(fast del av objektets struktur) men att innehållet kan ändras

    public boolean addDog(Dog d) {
        if (!containsDog(d)) {
            dogs.add(d);
            return true;
        }
        return false;
    }

    public boolean removeDog(Dog dog) {
        if (dog == null || dog.getOwner() != null) {
            return false;
        }
        return dogs.remove(dog);
    }

    public boolean removeDog(String name) {
        Dog dogToRemove = getDog(name);
        if (dogToRemove == null || dogToRemove.getOwner() != null) {
            return false;
        }
        return dogs.remove(dogToRemove);
    }

    public boolean containsDog(String name) {
        return getDog(name) != null;
    }

    public boolean containsDog(Dog dog) {
        if (dog == null) {
            return false;
        }
        return containsDog(dog.getName());
    }

    public Dog getDog(String name) {
        for (Dog dog : dogs) {
            if (dog.getName().equalsIgnoreCase(name)) {
                return dog;
            }
        }
        return null;
    }

    public ArrayList<Dog> getDogs() {
        ArrayList<Dog> sortedDogs = new ArrayList<>(dogs);        //hela listan ska sorteras så alla hundar(dogs, alltså) skickas med här.
        DogNameComparator comparator = new DogNameComparator();
        DogSorter.sortDogs(comparator, sortedDogs);
        return sortedDogs;
    }

    public ArrayList<Dog> getDogsMinTailLength(double minLength) {
        ArrayList<Dog> qualifiedDogs = new ArrayList<>();                    //dogs kan inte skickas med här pga att hundar med för kort svans ska filtreras bort.
        for (Dog dog : dogs) {                                                    //om dogs skulle skickas med skulle vi ta bort vissa hundar ist.
            if (dog.getTailLength() >= minLength) {
                qualifiedDogs.add(dog);
            }
        }
        DogTailNameComparator comparator = new DogTailNameComparator();
        DogSorter.sortDogs(comparator, qualifiedDogs);
        return qualifiedDogs;
    }
}
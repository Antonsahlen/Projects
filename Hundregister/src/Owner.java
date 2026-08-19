// Anton Sahlén ansa0433

import java.util.ArrayList;

public class Owner implements Comparable<Owner> {
    private final String name;
    private final ArrayList<Dog> ownedDogs = new ArrayList<>(); //ändra till mer passande namn på lista

    public Owner(String name) {
        this.name = normalizeName(name);
    }

    public String getName() {
        return name;
    }

    private String normalizeName(String inputName) {
        if (inputName == null || inputName.isBlank()) {
            return "Okänt Namn";
        }
        inputName = inputName.trim().toLowerCase();
        return inputName.substring(0, 1).toUpperCase() + inputName.substring(1);
    }

    public int compareTo(Owner other) {
        return this.name.compareTo(other.name);
    }

    public boolean addDog(Dog dog) {
        if (dog == null || !dog.getOwner().equals(this) || ownedDogs.contains(dog)) {
            return false;
        }
        ownedDogs.add(dog);
        return true;
    }

    public boolean removeDog(Dog dog) {
        return ownedDogs.remove(dog);
    }

    public ArrayList<Dog> getDogs() {
        ArrayList<Dog> sortedOwnedDogs = new ArrayList<>(ownedDogs);
        sortedOwnedDogs.sort(new DogNameComparator());
        return new ArrayList<>(sortedOwnedDogs);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Ägare: ").append(name).append("\n");

        if (ownedDogs.isEmpty()) {
            sb.append("  Hundar: (Inga hundar)");
        } else {
            sb.append("  Hundar:");
            for (Dog dog : ownedDogs) {
                sb.append(" ").append(dog.getName());
            }
        }
        return sb.toString();
    }
}

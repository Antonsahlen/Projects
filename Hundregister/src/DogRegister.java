// Anton Sahlén ansa0433

import java.util.ArrayList;
import java.util.List;

public class DogRegister {
    private static final String REGISTER_NEW_OWNER = "register new owner";
    private static final String REMOVE_OWNER = "remove owner";
    private static final String REGISTER_NEW_DOG = "register new dog";
    private static final String REMOVE_DOG = "remove dog";
    private static final String LIST_DOGS = "list dogs";
    private static final String LIST_OWNERS = "list owners";
    private static final String INCREASE_AGE = "increase age";
    private static final String GIVE_DOG_TO_OWNER = "give dog to owner";
    private static final String REMOVE_DOG_FROM_OWNER = "remove dog from owner";
    private static final String EXIT_COMMAND = "exit";

    private final InputReader inputReader = new InputReader();
    private final DogCollection dogRegister = new DogCollection();
    private final OwnerCollection ownerRegister = new OwnerCollection();


    private void start() {
        initialize();
        runCommandoLoop();
        exitCommand();
    }

    private void initialize() {
        System.out.println("Initializing program");
    }

    private void runCommandoLoop() {
        String command;
        do {
            command = readCommand();
            handleCommand(command);

        } while (!command.equals("0") && !command.equalsIgnoreCase("exit"));
    }

    private void exitCommand() {
        System.out.println("Exiting program");
    }

    private String readCommand() {
        System.out.println();
        return inputReader.readLine("Ange Kommando").trim().toLowerCase();
    }

    private void handleCommand(String command) {

        switch (command) {
            case "1":
            case REGISTER_NEW_OWNER:
                registerNewOwner();
                break;
            case "2":
            case REMOVE_OWNER:
                removeOwner();
                break;
            case "3":
            case REGISTER_NEW_DOG:
                registerNewDog();
                break;
            case "4":
            case REMOVE_DOG:
                removeDog();
                break;
            case "5":
            case LIST_DOGS:
                listDogs();
                break;
            case "6":
            case LIST_OWNERS:
                listOwners();
                break;
            case "7":
            case INCREASE_AGE:
                increaseAge();
                break;
            case "8":
            case GIVE_DOG_TO_OWNER:
                giveDogToOwner();
                break;
            case "9":
            case REMOVE_DOG_FROM_OWNER:
                removeDogFromOwner();
                break;
            case "0":
            case EXIT_COMMAND:
                break;
            default:
                System.out.println("Fel kommando, försök igen");
        }
    }

    private void registerNewOwner() {
        while (true) {
            String name = inputReader.readLine("Ange ägarens namn").trim();

            if (name.isBlank()) {
                System.out.println("Fel. Inget giltigt namn angavs, försök igen");

            } else if (ownerRegister.containsOwner(name)) {
                System.out.println("Fel, ägaren finns redan");
                break;

            } else {
                Owner newOwner = new Owner(name);
                ownerRegister.addOwner(newOwner);
                System.out.println("Ny ägare registrerad: " + newOwner.getName());
                break;
            }
        }
    }

    private void removeOwner() {

        if (!isOwnerRegistered()) {
            return;
        }

        String nameToRemove = inputReader.readLine("Ange namnet på ägaren som ska tas bort");
        Owner owner = getOwnerIfExists(nameToRemove);

        if (owner == null) {
            return;
        }

        List<Dog> dogsToRemove = new ArrayList<>(owner.getDogs());
        for (Dog dog : dogsToRemove) {
            dog.setOwner(null);
            owner.removeDog(dog);
            dogRegister.removeDog(dog);
        }

        boolean removed = ownerRegister.removeOwner(owner);
        if (removed) {
            System.out.println(owner.getName() + " har tagits bort");
        } else {
            System.out.println("Fel, det gick inte att ta bort ägaren");
        }
    }

    private void registerNewDog() {
        String name = getValidDogName();
        if (name == null) {
            return;
        }
        String breed = getValidBreed();
        int age = inputReader.readInt("Hundens ålder");
        int weight = inputReader.readInt("Hundens vikt");

        Dog dog = new Dog(name, breed, age, weight);
        dogRegister.addDog(dog);
        System.out.println("Ny hund registrerad: " + dog.getName());
    }

    private void removeDog() {
        if (!isDogRegistered()) {
            return;
        }

        String name = inputReader.readLine("Ange namnet på hunden som ska tas bort");
        Dog dog = getDogIfExists(name);
        if (dog == null) {
            return;
        }

        Owner owner = dog.getOwner();
        if (owner != null) {
            owner.removeDog(dog);
            dog.setOwner(null);
        }

        boolean removed = dogRegister.removeDog(dog);
        if (removed) {
            System.out.println(dog.getName() + " har tagits bort");
        } else {
            System.out.println("Fel, det gick inte att ta bort hunden");
        }
    }

    private void listDogs() {
        if (!isDogRegistered()) {
            return;
        }
        double minTailLength = inputReader.readDouble("Ange minsta svanslängd");
        ArrayList<Dog> validTailLength = dogRegister.getDogsMinTailLength(minTailLength);

        if (validTailLength.isEmpty()) {
            System.out.println("Fel, det finns inga hundar med tillräcklig svanslängd");
            return;
        }
        for (Dog dog : validTailLength) {
            System.out.println(dog);
        }
    }

    private void listOwners() {
        if (!isOwnerRegistered()) {
            return;
        }
        ArrayList<Owner> owners = ownerRegister.getOwners();

        System.out.println("Registrerade ägare: ");
        for (Owner owner : owners) {
            System.out.println(owner);
        }
    }

    private void increaseAge() {
        if (!isDogRegistered()) {
            return;
        }
        String name = inputReader.readLine("Hundens namn").trim();
        if (!isNameValid(name)) {
            return;
        }
        Dog dog = getDogIfExists(name);
        if (dog == null) {
            return;
        }
        dog.increaseAge();
        System.out.println(dog.getName() + " är nu " + dog.getAge() + " år gammal");
    }

    private void giveDogToOwner() {
        if (!isDogRegistered() || !isOwnerRegistered()) {
            return;
        }

        Dog dog = dogValidation();
        if (dog == null) {
            return;
        }
        Owner owner = ownerValidation();
        if (owner == null) {
            return;
        }

        dog.setOwner(owner);
        System.out.println("Hunden " + dog.getName() + " har en ny ägare registrerad: " + owner.getName());
    }

    private void removeDogFromOwner() {
        if (!isDogRegistered() || !isOwnerRegistered()) {
            return;
        }

        String dogName = inputReader.readLine("Vilken hund ska tas bort från sin ägare").trim();
        if (!isNameValid(dogName)) {
            return;
        }

        Dog dog = getDogIfExists(dogName);
        if (dog == null) {
            return;
        }

        if (dog.getOwner() == null) {
            System.out.println("Fel, hunden har ingen ägare");
            return;
        }

        dog.setOwner(null);
        System.out.println(dog.getName() + " har tagits bort från sin ägare");
    }

    private Dog dogValidation() {
        String dogName = inputReader.readLine("Vilken hund ska ges bort").trim();

        if (!isNameValid(dogName)) {
            return null;
        }

        Dog dog = getDogIfExists(dogName);
        if (dog == null) {
            return null;
        }

        if (dog.getOwner() != null) {
            System.out.println("Fel, hunden har redan en ägare");
            return null;
        }
        return dog;
    }

    private Owner ownerValidation() {
        String ownerName = inputReader.readLine("Vem ska bli ägare").trim();

        if (!isNameValid(ownerName)) {
            return null;
        }
        return getOwnerIfExists(ownerName);
    }


    private boolean isNameValid(String name) {
        if (name == null || name.isEmpty()) {
            System.out.println("Fel, inget giltigt namn angavs");
            return false;
        }
        return true;
    }

    private String getValidDogName() {
        while (true) {
            String name = inputReader.readLine("Hundens namn").trim();
            if (!isNameValid(name)) {
                continue;
            }
            if (dogRegister.getDog(name) != null) {
                System.out.println("Fel, hunden finns redan");
                return null;
            }
            return name;
        }
    }

    private String getValidBreed() {
        while (true) {
            String breed = inputReader.readLine("Hundens ras").trim();
            if (isNameValid(breed)) {
                return breed;
            }
        }
    }

    private boolean isDogRegistered() {
        if (dogRegister.getDogs().isEmpty()) {
            System.out.println("Fel, det finns inga hundar registrerade");
            return false;
        }
        return true;
    }

    private boolean isOwnerRegistered() {
        if (ownerRegister.getOwners().isEmpty()) {
            System.out.println("Fel, det finns inga ägare registrerade");
            return false;
        }
        return true;
    }

    private Dog getDogIfExists(String dogName) {
        Dog dog = dogRegister.getDog(dogName);
        if (dog == null) {
            System.out.println("Fel, hunden finns inte");
            return null;
        }
        return dog;
    }

    private Owner getOwnerIfExists(String ownerName) {
        Owner owner = ownerRegister.getOwner(ownerName);
        if (owner == null) {
            System.out.println("Fel, ägaren finns inte");
            return null;
        }
        return owner;
    }

    public static void main(String[] args) {
        new DogRegister().start();
    }
}

// Anton Sahlén ansa0433

public class Dog {

    private static final double DACHSHUND_TAIL_LENGTH = 3.7;
    private static final double CALCULATE_TAIL_LENGTH_FACTOR = 10.0;
    private static final double ROUNDING_FACTOR = 10.0;
    private final String name;
    private final String breed;
    private int age;
    private final int weight;
    private Owner owner;

    public Dog(String name, String breed, int age, int weight) {

        this.name = normalizeName(name);
        this.breed = normalizeName(breed);
        this.age = age;
        this.weight = weight;
    }

    private String normalizeName(String inputName) {
        if (inputName == null || inputName.isBlank()) {
            return "Okänt Namn";
        }
        inputName = inputName.trim().toLowerCase();
        return inputName.substring(0, 1).toUpperCase() + inputName.substring(1);

    }

    public String getName() {
        return name;
    }

    public String getBreed() {
        return breed;
    }

    public int getAge() {
        return age;
    }

    public int getWeight() {
        return weight;
    }

    public double getTailLength() {
        double tailLength;
        if (breed.equalsIgnoreCase("Tax") || breed.equalsIgnoreCase("Dachshund")) {
            tailLength = DACHSHUND_TAIL_LENGTH;
        } else {
            tailLength = age * (weight / CALCULATE_TAIL_LENGTH_FACTOR);
        }
        return Math.round(tailLength * ROUNDING_FACTOR) / ROUNDING_FACTOR; // Här avrundas svanslängde till närmaste heltal och sedan divideras med 10 till ett decimaltal.
    }

    public void increaseAge() {
        if (age < Integer.MAX_VALUE) {
            age++;
        }
    }

    public boolean setOwner(Owner newOwner) {
        if (newOwner == null) {
            return doRemoveOwner();
        } else {
            return allocateOwner(newOwner);
        }
    }

    private boolean allocateOwner(Owner newOwner) {
        if (newOwner == null) {
            return false;
        }
        if (this.owner != null) {
            return false;
        }
        this.owner = newOwner;
        newOwner.addDog(this);
        return true;
    }

    private boolean doRemoveOwner() {
        if (this.owner == null) {
            return false;
        }
        this.owner.removeDog(this);
        this.owner = null;
        return true;
    }

    public Owner getOwner() {
        return owner;
    }

    @Override
    public String toString() {
        return "Namn = " + name + "\nHundras = " + breed + "\nÅlder = " + age + " år " + "\nVikt = " + weight + "kg " + "\nSvanslängd = " + getTailLength() + "\nOwner = " + owner;
    }
}

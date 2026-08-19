// Anton Sahlén ansa0433

import java.util.Comparator;

public class DogNameComparator implements Comparator<Dog> {

    public int compare(Dog dogFirst, Dog dogSecond) {
        return dogFirst.getName().compareTo(dogSecond.getName());
    }

}
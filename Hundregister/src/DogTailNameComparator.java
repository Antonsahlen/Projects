// Anton Sahlén ansa0433

import java.util.Comparator;

public class DogTailNameComparator implements Comparator<Dog> {

    public int compare(Dog dogFirst, Dog dogSecond) {
        if (dogFirst.getTailLength() < dogSecond.getTailLength()) {
            return -1;
        }
        if (dogFirst.getTailLength() > dogSecond.getTailLength()) {
            return 1;
        }
        return dogFirst.getName().compareTo(dogSecond.getName());
    }
}
		

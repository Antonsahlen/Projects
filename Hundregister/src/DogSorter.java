// Anton Sahlén ansa0433

import java.util.ArrayList;
import java.util.Comparator;

public class DogSorter {        //statiska metoder eftersom denna klass endast är en sorteringsklass
    //har ex inga instansvariabler, använder metoderna som hjälpmetoder, men som inte behöver ett obj för att användas.
    private static void swapDogs(ArrayList<Dog> dogs, int indexFirst, int indexSecond) {
        Dog temporary = dogs.get(indexFirst);
        dogs.set(indexFirst, dogs.get(indexSecond));
        dogs.set(indexSecond, temporary);
    }

    private static int nextDog(Comparator<Dog> comparator, ArrayList<Dog> dogs, int startIndex) {
        int minIndex = startIndex;                                                        //inre loop som :
        for (int i = startIndex + 1; i < dogs.size(); i++) {                            //utgår från startindex, antar att den är minst och kollar om nästa element i listan är mindre.
            if (comparator.compare(dogs.get(i), dogs.get(minIndex)) < 0) {
                minIndex = i;                                            //om den är det sätts den till i, och returneras, så görs detta tills hela listan har kollats.
            }
        }
        return minIndex;
    }

    public static int sortDogs(Comparator<Dog> comparator, ArrayList<Dog> dogs) {
        int swapCounter = 0;
        for (int i = 0; i < dogs.size() - 1; i++) {                            //yttre loop
            int minIndex = nextDog(comparator, dogs, i);                    //i varje iteration returneras en minIndex(kortaste namn ex),
            if (minIndex != i) {                                            //om inte det minsta elementet är på plats byter vi plats.
                swapDogs(dogs, i, minIndex);
                swapCounter++;
            }
        }
        return swapCounter;
    }

}  




	

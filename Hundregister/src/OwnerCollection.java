// Anton Sahlén ansa0433

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


public class OwnerCollection {
    private Owner[] owners;         //inte final pga att det i addowner skapas en ny array(kopia) och tilldelar den till owners.(är förbjudet om den hade varit final)
    private int ownerCounter;       //håller reda på hur många ägare som faktiskt finns i arrayen.

    public OwnerCollection() {
        owners = new Owner[10];
        ownerCounter = 0;
    }

    public boolean addOwner(Owner owner) {
        if (owner == null || containsOwner(owner)) {
            return false;
        }// om obj som ska läggas till är null eller redan finns
        if (ownerCounter == owners.length) {
            owners = Arrays.copyOf(owners, owners.length * 2);      //om listan är full så skapas en ny kopia av listan som är dubbelt så stor
        }
        owners[ownerCounter++] = owner; //lägger till owner på "indexvärdet" som ownerCounter har och ökar sedan ownerCounter.
        return true;
    }

    public boolean removeOwner(String name) {                                           //Eventuellt ha en typ findOwnerIndex ?
        for (int i = 0; i < ownerCounter; i++) {
            if (owners[i].getName().equalsIgnoreCase(name)) {           //letar upp ägaren i arrayen
                if (!owners[i].getDogs().isEmpty()) {           //kontroll på att ägaren inte har några hundar
                    return false;                               //om villkoren uppgylls ->
                }

                for (int j = i; j < ownerCounter - 1; j++) {    //loopar från borttaggningspunkten till näst sista
                    owners[j] = owners[j + 1];                  //flyttar varje element ett steg bakåt
                }
                owners[ownerCounter - 1] = null;            //tömmer sista platsen
                ownerCounter--;             //minskar antalet ägare
                return true;
            }
        }
        return false;
    }

    public boolean removeOwner(Owner owner) {
        if (owner == null || !owner.getDogs().isEmpty()) {
            return false;
        }
        return removeOwner(owner.getName());
    }

    public boolean containsOwner(String name) {
        return getOwner(name) != null;
    }

    public boolean containsOwner(Owner owner) {
        if (owner == null) {
            return false;
        }
        return containsOwner(owner.getName());
    }

    public Owner getOwner(String name) {
        for (int i = 0; i < ownerCounter; i++) {
            if (owners[i] != null && owners[i].getName().equalsIgnoreCase(name)) {
                return owners[i];
            }
        }
        return null;
    }

    public ArrayList<Owner> getOwners() {
        ArrayList<Owner> list = new ArrayList<>();
        for (Owner owner : owners) {
            if (owner != null) {
                list.add(owner);
            }
        }
        Collections.sort(list);
        return list;
    }
}

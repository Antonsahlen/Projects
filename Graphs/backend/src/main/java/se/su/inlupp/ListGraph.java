// PROG2 VT2025, Inlämningsuppgift, del 1
// Grupp 067
// Oskar Persson ospe4502
// Felix Warkmark fewa5233
// Anton Sahlén 0433

package se.su.inlupp;


import java.util.*;

public class ListGraph<T> implements Graph<T> {

    //noderna är nyckelvärde, Varje nod har en mängd kanter kopplat till sig.
    private final Map<T, Set<Edge<T>>> places = new HashMap<>();

    @Override
    public void add(T node) {
        //kontrollerar om node redan finns
        //om den inte finns läggs den till och skapar ett tomt Set<Edge<T>>
        //om den redan finns görs ingenting
        places.putIfAbsent(node, new HashSet<>());

    }

    @Override
    public void connect(T node1, T node2, String name, int weight) {


        // kollar om noderna finns, annars kastas ett undantag
        if (!places.containsKey(node1) || !places.containsKey(node2)) {
            throw new NoSuchElementException("En av noderna saknas i grafen");
        }
        // kontrollera vikten
        if (weight < 0) {
            throw new IllegalArgumentException("Vikten får inte vara negativ");
        }
        // Hämtar mängd av kanter över kanter för båda noderna
        Set<Edge<T>> fromPlaces = places.get(node1);
        Set<Edge<T>> toPlaces = places.get(node2);

        // söker upp node1 i places-mappen och hämtar dess mängd av kopplingar. dessa kopplingar representerar alla vägar som går från node1 till andra platser.
        for (Edge<T> edge : fromPlaces) {
            if (edge.getDestination().equals(node2)) {
                throw new IllegalStateException("En kant finns redan mellan dessa två noder");
            }
        } //samma görs med toPlaces
        for (Edge<T> edge : toPlaces) {
            if (edge.getDestination().equals(node1)) {
                throw new IllegalStateException("En kant finns redan mellan dessa två noder");
            }
        }
        // om ingen kant finns mellan noderna skapas 2 nya kanter till, en från nod 1 till nod 2 och en från nod2 till nod1. och sparas i respektive
        // fromPlaces(nod1 kopplingar), samt toPlaces(nod2 kopplingar)
        fromPlaces.add(new Edge<>(node2, name, weight));
        toPlaces.add(new Edge<>(node1, name, weight));
    }

    @Override
    public void setConnectionWeight(T node1, T node2, int weight) {
        //kolla om noderna finns i grafen, (i våran map)
        if (!places.containsKey(node1) || !places.containsKey(node2)) {
            throw new NoSuchElementException("En av noderna saknas i grafen");
        }

        //om noderna finns i grafen, hämtar vi kanterna emellan, i båda riktningarna, om någon av de är null kastas ett exception.
        Edge<T> edge1 = getEdgeBetween(node1, node2);
        Edge<T> edge2 = getEdgeBetween(node2, node1);
        if (edge1 == null || edge2 == null) {
            throw new NoSuchElementException("Det finns ingen kant mellan dessa noder");
        }

        //viktkontroll
        if (weight < 0) {
            throw new IllegalArgumentException("Vikten får inte vara negativ");
        }
        //uppdaterar vikterna för båda kanterna.
        edge1.setWeight(weight);
        edge2.setWeight(weight);
    }


    @Override
    //Nycklarna i places är noderna, keySet hämtar samtliga nycklar(dvs noderna) och skapar en
    // ny HashSet med värdena, vi returnerar en kopia!Detta är viktigt eftersom vi inte vill ha några dubletter i vårt program.
    public Set<T> getNodes() {
        return new HashSet<>(places.keySet());
    }

    @Override
    public Collection<Edge<T>> getEdgesFrom(T node) {
        if (!places.containsKey(node)) {
            throw new NoSuchElementException("Noden " + node + "finns inte med i grafen");
        }

        // returnerar en kopia av Set<Edge<T>> så att man inte kan modifiera den. ibland är ArrayListor enklare att hantera(vid loopar ex)
        return new ArrayList<>(places.get(node));
    }

    @Override
    public Edge<T> getEdgeBetween(T node1, T node2) {

        if (!places.containsKey(node1)) {
            throw new NoSuchElementException("Noden " + node1 + " finns inte med i grafen");
        }
        if (!places.containsKey(node2)) {
            throw new NoSuchElementException("Noden " + node2 + " finns inte med i grafen");
        }

        // hämta kanterna som går från nod1, detta ger ett set som innehåller alla kopplingar från node1 till andra noder
        Set<Edge<T>> edges = places.get(node1);

        // vi loopar igenom detta set, (alla kanter som finns i edges) för varje kant kontrollerar vi om dess destination är nod2).
        //om en matchande kant hittas returnerar vi kanten direkt. annars returneras null.
        for (Edge<T> edge : edges) {
            if (edge.getDestination().equals(node2)) {
                return edge;
            }
        }

        return null;
    }


    @Override
    public void disconnect(T node1, T node2) {
        if (!places.containsKey(node1) || !places.containsKey(node2)) {
            throw new NoSuchElementException(" En av noderna saknas i grafen ");
        }
        //hämtar kanter emellan, om ingen kant finns kastas ett undantag.
        Edge<T> edge = getEdgeBetween(node1, node2);
        if (edge == null) {
            throw new IllegalStateException(" Det finns ingen kant mellan " + node1 + " och " + node2);
        }

        // hämtar kantlistan för nod1(tar bort kanten som leder till nod2, vilket gör att nod1 inte är ansluten till nod2 längre).
        places.get(node1).remove(edge);

        // hämtar kantlistan för nod2, pga oriktad måste vi ta bort i båda riktningarna, Och eftersom varje nod har en separat Edge<T> -instans
        // måste vi skapa ny Edge för med samma data matcha instansen i den andra nodens Set<Edge>>.
        places.get(node2).remove(new Edge<>(node1, edge.getName(), edge.getWeight()));
    }

    @Override
    public void remove(T node) {
        if (!places.containsKey(node)) {
            throw new NoSuchElementException("Noden: " + node + " finns inte");
        }

        // loopar igenom alla noder i grafen, för varje nod hämtas dess lista av kanter,
        // sedan tas alla kanter bort där edge.Destination är lika med node(den aktuella som ska tas bort).
        places.forEach((otherNode, edges) -> edges.removeIf(edge -> edge.getDestination().equals(node)));

        // sedan tas noden bort från places(finns inte längre med i grafen)
        places.remove(node);
    }

    @Override
    public boolean pathExists(T from, T to) {
        //hålla reda på besökta noder genom ett hashset,
        // och kolla om from finns i places, om nej = false
        Set<T> visited = new HashSet<>();
        if (!places.containsKey(from)) {
            return false;
        }
        //om from finns ska vi se om det finns en väg
        return recursiveVisitAll(from, to, visited);

    }

    //rekursiv djupet-först sökning
    private boolean recursiveVisitAll(T node1, T searchingFor, Set<T> visited) {
        visited.add(node1); // lägger till node1 så vi inte lägger till den igen
        if (node1.equals(searchingFor)) {       // om detta är true har vi hittat en väg
            return true;
        }
        //loopa igenom alla kanter som utgår från node1
        for (Edge<T> e : places.get(node1)) {

            //om edge inte är null och destination inte besökts ännu fortsätter vi att söka rekursivt tills vi når e.getDestination.(sökningen är slut)
            //om rekursionen hittar en väg returneras true direkt annars returneras false.
            if (e != null && !visited.contains(e.getDestination())) {
                if (recursiveVisitAll(e.getDestination(), searchingFor, visited)) {
                    return true;
                }
            }
        }
        return false;
    }


    @Override
    public List<Edge<T>> getPath(T from, T to) {

        Map<T, T> connection = new HashMap<>();
        recursiveConnect(from, null, connection);

        LinkedList<Edge<T>> path = new LinkedList<>();
        T current = to;
        while (current != null && !current.equals(from)) {
            T next = connection.get(current);
            if (next == null) {
                return null;
            }
            Edge<T> edge = getEdgeBetween(next, current);
            path.addFirst(edge);
            current = next;
        }
        return path;

    }

    private void recursiveConnect(T to, T from, Map<T, T> connection) {
        connection.put(to, from);

        for (Edge<T> e : places.get(to)) {
            if (!connection.containsKey(e.getDestination())) {
                recursiveConnect(e.getDestination(), to, connection);
            }
        }

    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder("Noder");
        sb.append("\n");
        for (Map.Entry<T, Set<Edge<T>>> kv : places.entrySet()) 	{                   // för varje Entry(alltså varje nyckel-värde par(Location-och dess kanter)
            sb.append(kv.getKey()).append(": ").append(kv.getValue()).append("\n");     // kv.getKey hämtar nodens namn, kv.getValue hämtar mängden av kanter
                                                                                        // entrySet ger varje nod och dess motsvarande mängd av kanter
        }
        return sb.toString();
    }
}





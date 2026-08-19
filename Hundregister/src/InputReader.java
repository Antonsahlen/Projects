// Anton Sahlén ansa0433

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;


public class InputReader {

    private static final ArrayList<InputStream> USED_STREAMS = new ArrayList<>(); //håller koll vilka inputstream objekt som redan används
    private final Scanner scanner;                                     //statis ->delas med alla instanser av inputReader. //scanner som läser från en inputStream.

    public InputReader() {
        this(System.in);
    }


    //om in redan finns i listan ,kastas ett undantag.(förhindra att två försöker läsa från samma ström)
    //annars skapas en ny scanner för den angivna strömmen

    public InputReader(InputStream in) {
        if (USED_STREAMS.contains(in)) {
            throw new IllegalStateException("Felmeddelande");  //"upptagen"
        }
        USED_STREAMS.add(in);
        this.scanner = new Scanner(in);
    }

    public String readLine(String input) {
        System.out.print(input + "?>");
        return scanner.nextLine();
    }

    public int readInt(String input) {
        System.out.print(input + "?>");
        int number = scanner.nextInt();
        clearBuffer();
        return number;
    }

    public double readDouble(String input) {
        System.out.print(input + "?>");
        double number = scanner.nextDouble();
        clearBuffer();
        return number;
    }

    private void clearBuffer() {
        scanner.nextLine();
    }
}

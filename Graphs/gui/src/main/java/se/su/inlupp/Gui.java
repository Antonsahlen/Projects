// PROG2 VT2025,Inlämningsuppgift, del 2
// Grupp 067
// Oskar Persson ospe4502
// Felix Warmark fewa5233
// Anton Sahlén ansa0433

package se.su.inlupp;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.*;
import java.util.*;

public class Gui extends Application {
    private Button newPlaceButton;
    private ImageView imageView;
    private Stage primaryStage;
    private FlowPane buttons;
    private Pane graphPane;
    private List<Location> markedLocations = new ArrayList<>();
    private boolean newPlaceMode = false;
    private boolean changed = false;
    private Graph<Location> graph = new ListGraph<>();
    private FileChooser fileChooser = new FileChooser();

    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        BorderPane window = new BorderPane();

        imageView = new ImageView();
        imageView.setPreserveRatio(true);

        graphPane = new Pane();
        StackPane forImage = new StackPane(imageView, graphPane);

        Button findPathButton = new Button("Find Path");
        Button showConnectionButton = new Button("Show Connection");
        newPlaceButton = new Button("New Place");
        Button newConnectionButton = new Button("New Connection");
        Button changeConnectionButton = new Button("Change Connection");

        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");

        MenuItem newMap = new MenuItem("New Map");
        MenuItem openMap = new MenuItem("Open");
        MenuItem saveMap = new MenuItem("Save");
        MenuItem saveImage = new MenuItem("Save Image");
        MenuItem exitMenu = new MenuItem("Exit");

        fileMenu.getItems().addAll(newMap, openMap, saveMap, saveImage, exitMenu);
        menuBar.getMenus().add(fileMenu);

        newMap.setOnAction(new NewMapHandler());
        openMap.setOnAction(new OpenMapHandler());
        saveMap.setOnAction(new SaveHandler());
        saveImage.setOnAction(new SaveImageHandler());
        exitMenu.setOnAction(new ExitHandler());

        buttons = new FlowPane();
        buttons.getChildren().addAll(findPathButton, showConnectionButton, newPlaceButton, newConnectionButton, changeConnectionButton);
        buttons.setAlignment(Pos.CENTER);
        buttons.setDisable(true);

        VBox topLayout = new VBox(menuBar, buttons);

        findPathButton.setOnAction(new FindPathHandler());
        showConnectionButton.setOnAction(new ShowConnectionHandler());
        newPlaceButton.setOnAction(new NewPlaceHandler());
        newConnectionButton.setOnAction(new NewConnectionHandler());
        changeConnectionButton.setOnAction(new changeConnectionHandler());

        window.setTop(topLayout);
        window.setCenter(forImage);

        fileChooser.setInitialDirectory(new File("."));

        primaryStage.setTitle("PathFinder");
        Scene scene = new Scene(window, 500, 50);
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(new exitWindowHandler());
        primaryStage.show();
    }

    class NewPlaceHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            newPlaceMode = true;
            newPlaceButton.setDisable(true);
            graphPane.getScene().setCursor(Cursor.CROSSHAIR);


            //lyssnare för att lyssna på musklick på grafen när vi är i "newPlaceMode" alltså den är true.
            graphPane.setOnMouseClicked(mouseEvent -> {
               //om newPlaceMode är true hämtas koordinaterna för musklicket enligt mouseEvent.getX och getY()
                if (newPlaceMode) {
                    double x = mouseEvent.getX();
                    double y = mouseEvent.getY();

                    //dialog för namn
                    TextInputDialog dialog = new TextInputDialog();
                    dialog.setTitle("Name");
                    dialog.setHeaderText("Name of place:");
                    Optional<String> result = dialog.showAndWait();

                    if (result.isPresent() && !result.get().trim().isEmpty()) {
                        //om användaren fyllt i ett namn och det inte är tomt, skapas en ny location med de koordinaterna samt adderas till vår graph.
                        //sedan "ritas" den upp via addLocationOnMap och vår boolean "changed" sätts till true(för att hålla koll på ändringar i programmet)
                        //sedan återställer vi pekaren samt gör knappen tillgänglig igen och sätter newPlaceMope till false
                        Location newLocation = new Location(result.get().trim(), x, y);
                        graph.add(newLocation);
                        addLocationOnMap(graphPane, newLocation);
                        changed = true;
                    }
                    newPlaceMode = false;
                    graphPane.getScene().setCursor(Cursor.DEFAULT);
                    newPlaceButton.setDisable(false);
                }
            });
        }
    }

    private void addLocationOnMap(Pane graphPane, Location loc) {
        Circle circle = new Circle(loc.getX(), loc.getY(), 9, Color.BLUE);
        Text label = new Text(loc.getX() + 10, loc.getY(), loc.getName());


        //Lyssnare för att hantera markeringar och avmarkeringar av platser(cirklar)
        circle.setOnMouseClicked(event -> changeLocationColor(circle, loc));
        graphPane.getChildren().addAll(circle, label);
    }

    private void changeLocationColor(Circle circle, Location loc) {

        //om vi redan har en loc som är markerad. så om vi klickar på den igen så "avmarkeras den".
        if (markedLocations.contains(loc)) {
            circle.setFill(Color.BLUE);
            markedLocations.remove(loc);
            //om markedLocations är mindre än 2,alltså 0 eller 1 platser är markerade så ändras platsen färg till röd vid klickning.
        } else if (markedLocations.size() < 2) {
            circle.setFill(Color.RED);
            markedLocations.add(loc);
        }
    }

    class NewConnectionHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            if (markedLocations.size() != 2) {
                showError("Two locations have to be marked!");
                return;
            }
            // hämta de två markerade platserna, spara i from och to
            Location from = markedLocations.get(0);
            Location to = markedLocations.get(1);

            // kontroll om det redan finns en koppling mellan dessa noder, om det finns kommer ett errormeddelande
            if (graph.getEdgeBetween(from, to) != null) {
                showError("A connection between the two locations already exists!");
                return;
            }

            //En alert(liknande dialog)
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Connection");
            alert.setHeaderText("Connection from " + from.getName() + " to " + to.getName());

            // vi skapar två textfält där användarna kan mata in text.
            TextField nameField = new TextField();
            TextField timeField = new TextField();

            //och placerar dem i en Vbox med tillhörande etikett(name och time)
            VBox layout = new VBox(10, new Label("Name:"), nameField, new Label("Time:"), timeField);
            alert.getDialogPane().setContent(layout);


            Optional<ButtonType> result = alert.showAndWait();

            // hämtar användarens inmatning och användaren klickar ok
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    String name = nameField.getText().trim();               //hämtar namn
                    int time = Integer.parseInt(timeField.getText().trim());        //hämtar tid som är en sträng vid inmatning men konverteras här till en int
                    if (name.isEmpty()) {
                        throw new IllegalArgumentException("Connection name cannot be empty.");
                    }

                    graph.connect(from, to, name, time);                //anropar connect och setConnectionWeight för att koppla noderna samman samt sätta weight.
                    graph.setConnectionWeight(from, to, time);

                    showEdgeBetween(from, to, graphPane);       // anropar metod som ritar upp kopplingen
                    changed = true;

                } catch (NumberFormatException e) {
                    alert = new Alert(Alert.AlertType.ERROR, "Time must be an Integer!");
                    alert.showAndWait();
                } catch (IllegalArgumentException e) {
                    alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
                    alert.showAndWait();
                }
            }
        }
    }

    private void showEdgeBetween(Location from, Location to, Pane graphPane) {
        Line edgeBetween = new Line(from.getX(), from.getY(), to.getX(), to.getY());
        edgeBetween.setStroke(Color.BLACK);
        graphPane.getChildren().add(edgeBetween);
    }

    class ShowConnectionHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            if (markedLocations.size() != 2) {
                showError("Two locations have to be marked!");
                return;
            }

            Location from = markedLocations.get(0);     //hämta noderna
            Location to = markedLocations.get(1);
            Edge<Location> connection = graph.getEdgeBetween(from, to);     //hämta förbindelsen

            if (connection == null) {
                showError("There is no connection between " + from.getName() + " and " + to.getName());
                return;
            }
            //dialogruta
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Connection");
            alert.setHeaderText("Connection from " + from.getName() + " to " + to.getName());

            // hämtar weight från getWeight i vår edge-klass.
            // konverterar värdet till en sträng eftersom TextField kräver att det är en sträng.
            TextField timeField = new TextField(String.valueOf(connection.getWeight()));
            TextField nameField = new TextField(connection.getName());
            timeField.setDisable(true); //ska inte gå att ändra
            nameField.setDisable(true);


            VBox layout = new VBox(10, new Label("Name:"), nameField, new Label("Time:"), timeField);
            alert.getDialogPane().setContent(layout);

            alert.showAndWait();

        }
    }


    class changeConnectionHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            if (markedLocations.size() != 2) {
                showError("Two locations have to be marked!");
                return;
            }

            Location from = markedLocations.get(0);
            Location to = markedLocations.get(1);
            Edge<Location> connection = graph.getEdgeBetween(from, to);

            if (connection == null) {
                showError("There is no connection between " + from.getName() + " and " + to.getName());
                return;
            }

            TextField timeField = new TextField(String.valueOf(connection.getWeight()));
            TextField nameField = new TextField(connection.getName());
            nameField.setDisable(true);


            VBox layout = new VBox(10, new Label("Name:"), nameField, new Label("Time:"), timeField);


            //dialogruta
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Connection");
            alert.setHeaderText("Connection from " + from.getName() + " to " + to.getName());
            alert.getDialogPane().setContent(layout);

            //väntar på svar från användaren, om svaret är ok ->
            alert.showAndWait().ifPresent(result -> {
                if (result == ButtonType.OK) {
                    try {
                        int newTravelTime = Integer.parseInt(timeField.getText().trim());  //hämta nya inmatade värdet
                        updateConnectionTime(from, to, connection, newTravelTime, graphPane);      // här hade vi kunnat ha anropat setConnectionWeight direkt bara?

                    } catch (NumberFormatException e) {
                        showError("Time must be an integer!");
                    }
                }
            });
        }
    }

    private void updateConnectionTime(Location from, Location to, Edge<Location> edge, int newTravelTime, Pane graphPane) {
        edge.setWeight(newTravelTime);

        Edge<Location> edgeReverse = graph.getEdgeBetween(to, from);
        if (edgeReverse != null) {
            edgeReverse.setWeight(newTravelTime);
            changed = true;
        }

        //loadGraph(graphPane);
    }

    class FindPathHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {


            if (markedLocations.size() != 2) {
                showError("Two locations have to be marked!");
                return;
            }

            Location from = markedLocations.get(0);
            Location to = markedLocations.get(1);

            if (!graph.pathExists(from, to)) {
                showError("No path exists between " + from.getName() + " and " + to.getName());
                return;
            }

            //glömt ändra? upprepning!
            List<Edge<Location>> path = graph.getPath(from, to);
            if (path == null || path.isEmpty()) {
                showError("No path found between " + from.getName() + " and " + to.getName());
                return;
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Message");
            alert.setHeaderText("The Path from " + from.getName() + " to " + to.getName() + ":");


            //"formateringen" av det som ska visas som resultat vid findPath klickas. Vi använder os av en Stringbuilder.
            //initierar en totaltid med 0 och plussar på med varje sträckas "kostnad" eller weight.
            StringBuilder pathInfo = new StringBuilder();
            int totalTime = 0;

            for (Edge<Location> edge : path) {
                totalTime += edge.getWeight();
                pathInfo.append("to ").append(edge.getDestination().getName())
                        .append(" by ").append(edge.getName())
                        .append(" takes ").append(edge.getWeight()).append("\n");
            }

            pathInfo.append("Total ").append(totalTime);

            alert.setContentText(pathInfo.toString());
            alert.showAndWait();
        }
    }

    class NewMapHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            if (changed) {
                // om användaren klickar på avbryt anropas event.consume(); och vi avbryter resten av metoden. Alltså dyker inte filväljaren upp.

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Warning");
                alert.setHeaderText("Warning");
                alert.setContentText("Unsaved changes. continue anyway?");
                Optional<ButtonType> res = alert.showAndWait();
                if (res.isPresent() && res.get().equals(ButtonType.CANCEL)) {
                    event.consume();
                    return;
                }
            }

            //om användaren har gjort ändringar och trycker ok på "unsaved changes, continue anyway?" alternativt inte gjort några ändringar fortsätter metoden:

            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                Image image = new Image(selectedFile.toURI().toString());

                graphPane.getChildren().clear();
                imageView.setImage(image);
                updateWindowSize(image);

            }
        }
    }
    //Metod för att lösa ett problem vi hade med att hela bilden inte fick plats i vår stage.
    private void updateWindowSize(Image image) {

        primaryStage.setWidth(image.getWidth() + 20);
        primaryStage.setHeight(image.getHeight() + 80);
        buttons.setDisable(false);
    }

    class OpenMapHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {

            if (changed) {

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setContentText("Unsaved changes. continue anyway?");
                alert.setTitle("Warning");
                alert.setHeaderText("Warning");
                Optional<ButtonType> res = alert.showAndWait();
                if (res.isPresent() && res.get().equals(ButtonType.CANCEL)) {
                    // om användaren klickar på avbryt anropas event.consume(); och vi avbryter resten av metoden. Alltså dyker inte filväljaren upp.
                    event.consume();
                    return;
                }
            }
            //om användaren har gjort ändringar och trycker ok på "unsaved changes, continue anyway?" alternativt inte gjort några ändringar fortsätter metoden:

            FileChooser graphFiles = new FileChooser();
            graphFiles.getExtensionFilters().add(new FileChooser.ExtensionFilter("Graph files(*.graph)", "*.graph"));
            graphFiles.setInitialDirectory(new File("."));
            File file = graphFiles.showOpenDialog(primaryStage);
            if (file != null) {

                loadImage();
                resetGraph();
                open(file.getAbsolutePath());
                loadGraph(graphPane);

            }
        }
    }

    private void loadImage() {
        File imageFile = new File("C:\\Users\\anton_2agk5a5\\Pictures\\europa.gif");
        if (imageFile.exists()) {
            Image image = new Image(imageFile.toURI().toString());
            imageView.setImage(image);
            updateWindowSize(image);
        } else {
            System.out.println("Image file not found: europa.gif");
        }
    }

    private void open(String fileName) {

        try {
            FileReader file = new FileReader(fileName);
            BufferedReader in = new BufferedReader(file);
            Map<String, Location> locationsByName = new HashMap<>();                //lagrar noder(locations) med namn som nyckel
            String line = in.readLine();

            line = in.readLine(); //hoppa över bildfilnamnet

            //läsa in koordinaterna för varje Stad.
            String[] items = line.split(";");
            for (int i = 0; i < items.length; i += 3) {
                String name = items[i];
                double x = Double.parseDouble(items[i + 1]);
                double y = Double.parseDouble(items[i + 2]);
                Location loc = new Location(name, x, y);                        //skapar loc, lägger till i grafen samt lagrar i Hashmappen
                graph.add(loc);
                locationsByName.put(name, loc);
            }

            //läsa in förbindelserna enligt Stockholm;Paris;flyg;3 (exempelvis).
            while ((line = in.readLine()) != null) {                //läser in ny rad tills filen är "slut"(inga fler rader med strängar)
                items = line.split(";");
                String fromName = items[0];
                String toName = items[1];
                String edgeName = items[2];
                int weight = Integer.parseInt(items[3]);
                Location from = locationsByName.get(fromName);
                Location to = locationsByName.get(toName);
                if (graph.getEdgeBetween(from, to) == null)
                    graph.connect(from, to, edgeName, weight);

            }
            in.close();
            file.close();
            changed = false;

        } catch (FileNotFoundException e) {
            showError("File not found");

        } catch (IOException e) {
            showError("IO-error" + e.getMessage());
        }
    }

    private void loadGraph(Pane graphPane) {

        //hämtar alla noder i grafen, och för varje loc av de hämtade noderna så "ritar" vi upp platserna på kartan via addLocationOnMap!
        for (Location loc : graph.getNodes()) {
            addLocationOnMap(graphPane, loc);
        }
        //För varje nod from hämtas alla kanter med graph.getEdgesFrom(from). Vi går igenom varje Edge<Location> som utgår från from.
        //sedan hämtar vi destinationen och sparar i variabeln to.
        for (Location from : graph.getNodes()) {
            for (Edge<Location> edge : graph.getEdgesFrom(from)) {
                Location to = edge.getDestination();

                //Sist men inte minst ritas förbindelsen ut genom att hämta koordinaterna från loc. (getX() och getY()finns implementerade i Location klassen.
                Line line = new Line(from.getX(), from.getY(), to.getX(), to.getY());
                line.setStroke(Color.GRAY);
                graphPane.getChildren().add(line);
            }
        }
    }

    private void resetGraph() {

        graphPane.getChildren().clear();
        markedLocations.clear();
        for (Location loc : graph.getNodes()) {
            graph.remove(loc);
        }
        changed = false;
    }

    class ExitHandler implements EventHandler<ActionEvent> {
        public void handle(ActionEvent event) {
            primaryStage.fireEvent(new WindowEvent(primaryStage, WindowEvent.WINDOW_CLOSE_REQUEST));
        }
    }

    //hanterar stängningen av "krysset" på fönstret
    class exitWindowHandler implements EventHandler<WindowEvent> {
        @Override
        public void handle(WindowEvent event) {
            if (changed) {

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Warning");
                alert.setHeaderText("Warning");
                alert.setContentText("Unsaved changes. continue anyway?");
                Optional<ButtonType> res = alert.showAndWait();
                if (res.isPresent() && res.get().equals(ButtonType.CANCEL))
                    event.consume();
            }
        }
    }

    class SaveImageHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            try {

                WritableImage image = primaryStage.getScene().snapshot(null);       // tar ett snapshot
                File file = new File("capture.png");                                    //skapar en ny fil där bilden kommer sparas
                RenderedImage renderedImage = SwingFXUtils.fromFXImage(image, null);    //konverterar bilden så att den kan "skrivas" till en fil
                ImageIO.write(renderedImage, "png", file);      // ImageIO skriver den konverterade bilden renderedImage till "capture.png" filen

            } catch (IOException e) {
                showError("IO-error " + e.getMessage());
            }
        }
    }

    class SaveHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            File file = fileChooser.showSaveDialog(primaryStage);
            if (file != null) {
                save(file.getAbsolutePath());       //filens absoluta sökväg skickas till vår save-metod
                changed = false;
            }
        }
    }

    private void save(String fileName) {
        if (!fileName.endsWith(".graph")) {
            fileName += ".graph";
        }
        //skapar en skrivare för att skriva till filen
        try (PrintWriter writer = new PrintWriter(new FileOutputStream(fileName))) {

            //hämtar bildens URL från imageView och skriver den till filen
            String imagePath = imageView.getImage().getUrl();
            writer.println(imagePath);

            //sparar alla noder och deras koordinater precis som vi läser in en graffil
            StringBuilder locationCoordinates = new StringBuilder();
            for (Location loc : graph.getNodes()) {
                locationCoordinates.append(loc.getName()).append(";")
                        .append(loc.getX()).append(";")
                        .append(loc.getY()).append(";");
            }
            //skriver "resultatet av stringbuildern" till filen
            writer.println(locationCoordinates);

            //sparar och skriver enligt samma format som vi läser in en graffil
            for (Location from : graph.getNodes()) {
                for (Edge<Location> edge : graph.getEdgesFrom(from)) {
                    Location to = edge.getDestination();
                    writer.println(from.getName() + ";" + to.getName() + ";" + edge.getName() + ";" + edge.getWeight());
                }
            }

        } catch (FileNotFoundException e) {
            showError("File not found");
        } catch (IOException e) {
            showError("IO-error " + e.getMessage());
        }
    }


    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    private static class Location {
        String name;
        double x, y;

        Location(String name, double x, double y) {
            this.name = name;
            this.x = x;
            this.y = y;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public String getName() {
            return name;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}


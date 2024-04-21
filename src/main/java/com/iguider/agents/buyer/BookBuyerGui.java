package com.iguider.agents.buyer;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class BookBuyerGui extends Application {
    protected BookBuyerAgent bookBuyerAgent;
    protected ListView<String> listViewMessages; //pour afficher les message reçu par l'agent
    protected ObservableList<String> observableListData;

    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) throws Exception {
        startContainer();
        primaryStage.setTitle("Book Buyer Gui");
        BorderPane borderPane = new BorderPane();
        VBox vBox = new VBox();
        vBox.setPadding(new Insets(10));
        observableListData = FXCollections.observableArrayList();
        listViewMessages= new ListView<String>(observableListData);
        vBox.getChildren().add(listViewMessages);
        borderPane.setCenter(vBox);
        Scene scene = new Scene(borderPane,400,500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void startContainer() throws Exception {
        Runtime runtime = Runtime.instance();
        ProfileImpl profile = new ProfileImpl();
        profile.setParameter(Profile.MAIN_HOST,"localhost");// ou se trouve le main container
        AgentContainer container = runtime.createAgentContainer(profile);
        //BookBuyerAgent.class.getName() => "com.iguider.agents.buyer.BookBuyerAgent"
        AgentController agentController= container.createNewAgent(
                "BookBuyerAgent",
                BookBuyerAgent.class.getName(),
                new Object[]{this});// this => la référence vers l'interface graphique
        agentController.start();
    }

    public void logMessage(ACLMessage aclMessage){
        Platform.runLater(()->{
            //l'agent à chaque fois qu'ils veux log quelque chouse dans l'interface il n'a qu'à faire appel à cette méthode
            observableListData.add(aclMessage.getSender().getName()
                    +"=>"+aclMessage.getContent());
        });
    }
}

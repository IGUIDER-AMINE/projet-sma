package com.iguider.containers;

import com.iguider.agents.AcheteurAgent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AcheteurGui extends Application {
    public AcheteurAgent acheteurAgent;
    protected ObservableList<String> observableListData;
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) throws Exception {
        startContainer();
        primaryStage.setTitle("Acheteur :");
        BorderPane borderPane = new BorderPane();
        VBox vBox = new VBox();
        observableListData = FXCollections.observableArrayList();
        ListView<String> listView= new ListView<String>(observableListData);
        vBox.getChildren().add(listView);
        borderPane.setCenter(vBox);
        Scene scene = new Scene(borderPane,400,500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void startContainer() throws Exception {
        Runtime runtime = Runtime.instance();
        ProfileImpl profileImpl= new ProfileImpl();
        profileImpl.setParameter(Profile.MAIN_HOST,"localhost");
        AgentContainer agentContainer = runtime.createAgentContainer(profileImpl);
        AgentController agentController= agentContainer.createNewAgent(
                "ACHETEUR",
                AcheteurAgent.class.getName(),
                new Object[]{this});// this => la référence vers l'interface graphique
        agentController.start();
    }

    public void logMessage(ACLMessage aclMessage){
        Platform.runLater(()->{
            //l'agent à chaque fois qu'ils veux log quelque chouse dans l'interface il n'a qu'à faire appel à cette méthode
            observableListData.add(aclMessage.getContent() + ", " + aclMessage.getSender().getName());
        });
    }

}

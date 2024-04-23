package com.iguider.containers;

import com.iguider.agents.VendeurAgent;
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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class VendeurGui extends Application {
    public VendeurAgent vendeurAgent;
    protected ListView<String> listViewMessages; //pour afficher les message reçu par l'agent
    protected ObservableList<String> observableListData;
    protected AgentContainer agentContainer;

    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) throws Exception {
        startContainer();
        primaryStage.setTitle("Vendeur Interface :");

        HBox hBox = new HBox();
        hBox.setPadding(new Insets(10));
        hBox.setSpacing(10);

        Label label = new Label("Agent name :");
        TextField textFieldAgentName = new TextField();
        Button buttonDeploy = new Button("Deploy");
        hBox.getChildren().addAll(label,textFieldAgentName,buttonDeploy);

        BorderPane borderPane = new BorderPane();
        VBox vBox = new VBox();
        vBox.setPadding(new Insets(10));
        observableListData = FXCollections.observableArrayList();
        listViewMessages= new ListView<String>(observableListData);
        vBox.getChildren().add(listViewMessages);

        borderPane.setTop(hBox);
        borderPane.setCenter(vBox);

        Scene scene = new Scene(borderPane,400,500);
        primaryStage.setScene(scene);
        primaryStage.show();

        buttonDeploy.setOnAction((evt)->{
            //deploy l'agent
            try {
                String name=textFieldAgentName.getText();
                //BookBuyerAgent.class.getName() => "com.iguider.agents.buyer.VendeurGui"
                AgentController agentController= agentContainer.createNewAgent(
                        name,
                        VendeurAgent.class.getName(),
                        new Object[]{this});// this => la référence vers l'interface graphique
                agentController.start();
            } catch (StaleProxyException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void startContainer() throws Exception {
        Runtime runtime = Runtime.instance();
        ProfileImpl profile = new ProfileImpl();
        profile.setParameter(Profile.MAIN_HOST,"localhost");// où se trouve le main container
        agentContainer = runtime.createAgentContainer(profile);
    }

    public void logMessage(ACLMessage aclMessage){
        Platform.runLater(()->{
            //l'agent à chaque fois qu'ils veut log quelque chose dans l'interface il n'a qu'à faire appel à cette méthode
            observableListData.add(aclMessage.getSender().getName() + " " + aclMessage.getContent());
        });
    }
}

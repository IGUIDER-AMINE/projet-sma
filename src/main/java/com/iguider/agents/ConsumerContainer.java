package com.iguider.agents;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class ConsumerContainer extends Application {
    // la relation entre l'interface graphique et l'agent c'est une relation qui bidirectionnelle => l'interface graphique a besoin d'une référence vers l'agent et l'agent a besoin d'une références vers l'interface graphique
    protected ConsumerAgent consumerAgent;
    protected ObservableList<String> observableListData;

    //déployer un agent
    public static void main(String[] args) throws ControllerException {
        launch(args); // lancer javafx
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        startContainer();
        primaryStage.setTitle("Consumer Container");
        BorderPane borderPane = new BorderPane();

        HBox hBox1 =new HBox();
        hBox1.setPadding(new Insets(10));
        hBox1.setSpacing(10);
        Label labelBookName = new Label("Book Name");
        TextField textFieldBookName=new TextField();
        Button buttonOk = new Button("OK");
        hBox1.getChildren().addAll(labelBookName,textFieldBookName,buttonOk);
        borderPane.setTop(hBox1);

        observableListData = FXCollections.observableArrayList();
        ListView<String> listViewMessages= new ListView<String>(observableListData);
        VBox vBox2 =new VBox();
        vBox2.getChildren().add(listViewMessages);
        borderPane.setCenter(vBox2);
        vBox2.setPadding(new Insets(10));
        vBox2.setSpacing(10);

        buttonOk.setOnAction(evt->{
            String bookName = textFieldBookName.getText();
            GuiEvent guiEvent = new GuiEvent(this,1);
            guiEvent.addParameter(bookName);
            //observableListData.add(bookName);
            consumerAgent.onGuiEvent(guiEvent);
        });

        Scene scene = new Scene(borderPane,600,400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void startContainer() throws Exception {
        Runtime runtime = Runtime.instance();
        ProfileImpl profile = new ProfileImpl();
        profile.setParameter(Profile.MAIN_HOST,"localhost");
        AgentContainer container = runtime.createAgentContainer(profile);
        AgentController consumerController= container.createNewAgent("consumer","com.iguider.agents.ConsumerAgent",new Object[]{this});// this => la référence vers l'interface graphique
        consumerController.start();
    }

    public void logMessage(ACLMessage aclMessage){
        Platform.runLater(()->{
            //l'agent à chaque fois qu'ils veux log quelque chouse dans l'interface il n'a qu'à faire appel à cette méthode
            observableListData.add(aclMessage.getSender().getName()
                    +"=>"+aclMessage.getContent());
        });
    }

}

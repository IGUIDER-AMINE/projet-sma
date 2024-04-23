package com.iguider.agents;

import com.iguider.containers.ConsumerContainer;
import jade.core.AID;
import jade.core.behaviours.*;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.wrapper.ControllerException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

//quand vous avez un agent qui utilise une interface graphique => hériter de la class GuiAgent
public class ConsumerAgent extends GuiAgent {
    protected ConsumerContainer consumerContainer;

    @Override
    protected void setup() {
        String bookName=null;
        if(this.getArguments().length==1){
            // la relation bidirectionnelle entre l'agent et l'interface graphique
            consumerContainer=(ConsumerContainer)getArguments()[0];//refrence vers l'interface => protected ConsumerAgent consumerAgent;
            consumerContainer.consumerAgent=this; //this => consumerAgent
        }
        System.out.println("Initialisation de l'agent " + this.getAID().getName());
        System.out.println("I'm trying to buy the book " + bookName);

        //gérer plusieurs tâches en parallèle
        ParallelBehaviour parallelBehaviour = new ParallelBehaviour();
        //affecter les comportements
        addBehaviour(parallelBehaviour);

        parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                MessageTemplate messageTemplate=
                        MessageTemplate.and(
                        MessageTemplate.MatchPerformative(ACLMessage.CFP),
                        MessageTemplate.MatchLanguage("fr"));
                //ACLMessage aclMessage = receive(messageTemplate);
                ACLMessage aclMessage = receive();
                if(aclMessage!=null){
                    System.out.println("Sender : " + aclMessage.getSender().getName());
                    System.out.println("Content : " + aclMessage.getContent());
                    System.out.println("SpeechAct : "+ ACLMessage.getPerformative(aclMessage.ACCEPT_PROPOSAL));

                    switch (aclMessage.getPerformative()){
                        case ACLMessage.CONFIRM:
                            consumerContainer.logMessage(aclMessage);
                            break;
                        default:
                            break;
                    }

                }else {
                    System.out.println("Bolc......");
                    // si un message qui arrive qui concerne l'agent est tout simplement il reçoit une notification ,ce qui permet donc de débloquer cette méthode bloc et automatiquement il revient la méthode d'action
                    block();
                }
            }
        });

    }

    @Override
    protected void beforeMove() {
        try {
            System.out.println("Before Migration from "+ this.getContainerController().getContainerName());
        } catch (ControllerException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void afterMove() {
        try {
            System.out.println("After Migration to "+ this.getContainerController().getContainerName());
        } catch (ControllerException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void takeDown() {
        System.out.println("I'm going to die ....");
    }

    @Override
    public void onGuiEvent(GuiEvent evt) {
        // exécuter lorsqu'un événement se produit dans l'interface graphique
        if(evt.getType()==1){
            String bookName = (String)evt.getParameter(0);
            ACLMessage aclMessage=new ACLMessage(ACLMessage.REQUEST);
            aclMessage.setContent("wants " + bookName);
            aclMessage.addReceiver(new AID("ACHETEUR",AID.ISLOCALNAME));
            send(aclMessage);
        }
    }
}

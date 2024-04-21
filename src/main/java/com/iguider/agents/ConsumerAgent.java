package com.iguider.agents;

import jade.core.AID;
import jade.core.Agent;
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
            // la relation bidirectionnelle entre l'agent et l-interface graphique
            consumerContainer=(ConsumerContainer)getArguments()[0];//refrence vers l'interface => protected ConsumerAgent consumerAgent;
            consumerContainer.consumerAgent=this; //this => consumerAgent
        }
        System.out.println("Initialisation de l'agent " + this.getAID().getName());
        System.out.println("I'm trying to buy the book " + bookName);
        //gerer plusieurs tache en parallele
        ParallelBehaviour parallelBehaviour = new ParallelBehaviour();
        //affecter les comportements
        addBehaviour(parallelBehaviour);

        /*addBehaviour(new Behaviour() {
            private int counter=0;
            @Override
            public void action() {
                System.out.println("----------------");
                System.out.println("Step " + counter);
                System.out.println("----------------");
                ++counter;
            }

            @Override
            public boolean done() {
                return (counter==10);
            }
        });*/

        parallelBehaviour.addSubBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                System.out.println("One Shot Behaviour");
            }
        });

        /*addBehaviour(new CyclicBehaviour() {
            private int counter=0;
            @Override
            public void action() {
                System.out.println("Counter =>"+counter);
                ++counter;
            }
        });*/

        /*addBehaviour(new TickerBehaviour(this,1000) {
            @Override
            protected void onTick() {
                System.out.println("Tic");
                System.out.println(myAgent.getAID().getLocalName());
            }
        });*/

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy:HH:mm");
        Date date= null;
        try {
            date= dateFormat.parse("24/05/2021:10:59");
        }catch (ParseException e){
            e.printStackTrace();
        }
        parallelBehaviour.addSubBehaviour(new WakerBehaviour(this,date) {
            @Override
            protected void onWake() {
                System.out.println("Waker Behaviour ....");
            }
        });

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
                    //System.out.println("SpeechAct : "+ ACLMessage.getPerformative(aclMessage.ACCEPT_PROPOSAL));

                    /*ACLMessage reply = new ACLMessage(ACLMessage.CONFIRM);
                    reply.addReceiver(aclMessage.getSender());
                    reply.setContent("Price=900");
                    send(reply);*/
                    consumerContainer.logMessage(aclMessage);
                }else {
                    System.out.println("Bolc......");
                    // qu'il ya un message qui arrive qui concerne l'agent est tout simplement il reçoit une notification
                    // ce qui permet donc de débloquer cette méthode bloc et automatiquement il reviet la methode d'action
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
    protected void onGuiEvent(GuiEvent evt) {
        // execute lorque un événement se produit dans l'interface graphique
        if(evt.getType()==1){
            String bookName = (String)evt.getParameter(0);
            //System.out.printf("Agent => " + getAID().getName() +"=>"+bookName);
            ACLMessage aclMessage=new ACLMessage(ACLMessage.REQUEST);
            aclMessage.setContent(bookName);
            //aclMessage.addReceiver(new AID("BookBuyerAgent",AID.ISLOCALNAME));
            aclMessage.addReceiver(new AID("ACHETEUR",AID.ISLOCALNAME));
            send(aclMessage);
        }
    }
}

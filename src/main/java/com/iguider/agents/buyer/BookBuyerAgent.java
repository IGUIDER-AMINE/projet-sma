package com.iguider.agents.buyer;

import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;

public class BookBuyerAgent extends GuiAgent {
    protected BookBuyerGui gui;
    @Override
    protected void setup() {
        if(getArguments().length==1){
            gui=(BookBuyerGui)getArguments()[0];
            gui.bookBuyerAgent=this;
        }
        ParallelBehaviour parallelBehaviour= new ParallelBehaviour();
        addBehaviour(parallelBehaviour);

        parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                //attendre des messages
                ACLMessage aclMessage = receive();
                if(aclMessage!=null){
                    // log le message dans l'interface graphique
                    gui.logMessage(aclMessage);
                    //ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
                    //reply.addReceiver(aclMessage.getSender());
                    ACLMessage replay = aclMessage.createReply(); // je vais répondre aux memes sender
                    replay.setPerformative(ACLMessage.INFORM);
                    replay.setContent("Trying to buye"+aclMessage.getContent());
                    send(replay);
                }
                else{
                    block();
                }
            }
        });
    }

    @Override
    protected void onGuiEvent(GuiEvent guiEvent) {

    }
}

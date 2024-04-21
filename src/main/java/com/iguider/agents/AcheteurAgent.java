package com.iguider.agents;

import com.iguider.agents.buyer.BookBuyerGui;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;

public class AcheteurAgent extends GuiAgent {
    protected AcheteurGui gui;
    @Override
    protected void setup() {
        if(getArguments().length==1){
            gui=(AcheteurGui) getArguments()[0];
            gui.acheteurAgent=this;
        }

        ParallelBehaviour parallelBehaviour= new ParallelBehaviour();
        addBehaviour(parallelBehaviour);

        parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                //attendre des messages
                ACLMessage aclMessage = receive();
                if(aclMessage!=null){
                    String livre = aclMessage.getContent();
                    // log le message dans l'interface graphique
                    gui.logMessage(aclMessage);

                    ACLMessage replay = aclMessage.createReply(); // je vais répondre aux memes sender
                    replay.setContent("Ok pou "+aclMessage.getContent());
                    send(replay);

                    ACLMessage aclMessage3 = new ACLMessage(ACLMessage.CFP);//call for proposal
                    aclMessage3.setContent(livre);
                    aclMessage3.addReceiver(new AID("VENDEUR", AID.ISLOCALNAME));
                    send(aclMessage3);
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

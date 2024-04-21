package com.iguider.agents;

import com.iguider.agents.buyer.BookBuyerGui;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;

public class VendeurAgent extends GuiAgent {
    protected VendeurGui gui;

    @Override
    protected void setup() {
        if(getArguments().length==1){
            gui=(VendeurGui)getArguments()[0];
            gui.vendeurAgent=this;
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

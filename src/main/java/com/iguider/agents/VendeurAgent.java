package com.iguider.agents;

import com.iguider.containers.VendeurGui;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;

import java.util.Random;

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

        parallelBehaviour.addSubBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                DFAgentDescription agentDescription = new DFAgentDescription();
                agentDescription.setName(getAID());
                ServiceDescription serviceDescription = new ServiceDescription();
                serviceDescription.setType("transaction");
                serviceDescription.setName("vente-livres");
                agentDescription.addServices(serviceDescription);
                try {
                    //deploy service
                    DFService.register(myAgent,agentDescription);//myAgent=>referce vers l'agent
                } catch (FIPAException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                //attendre des messages
                ACLMessage aclMessage = receive();
                if(aclMessage!=null){
                    // log le message dans l'interface graphique
                    gui.logMessage(aclMessage);
                    ACLMessage reply = aclMessage.createReply();

                    switch (aclMessage.getPerformative()){
                        case ACLMessage.CFP :
                            reply.setPerformative(ACLMessage.PROPOSE);
                            reply.setContent(String.valueOf(500+new Random().nextInt(1000)));// nomber entre 0 et 1000
                            break;
                        case ACLMessage.ACCEPT_PROPOSAL:
                            // decide whether to accept proposal or not
                            Random random = new Random();
                            double randomNumber = random.nextDouble();
                            boolean accept = randomNumber < 0.7;
                            if(accept){
                                reply.setPerformative(ACLMessage.AGREE);// il faut consulter le stock pour répondre par accepte ou refuse
                                reply.setContent(aclMessage.getContent());
                            }else{
                                reply.setPerformative(ACLMessage.REFUSE);
                            }
                            break;
                        default:
                            break;
                    }
                    send(reply);
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

    @Override
    protected void takeDown() {
        try {
            //Supprimer tous les services qui sont publiés par cet agent.
            DFService.deregister(this);
        } catch (FIPAException e) {
            throw new RuntimeException(e);
        }
    }
}

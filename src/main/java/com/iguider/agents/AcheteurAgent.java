package com.iguider.agents;

import com.iguider.containers.AcheteurGui;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.ArrayList;
import java.util.List;

public class AcheteurAgent extends GuiAgent {
    protected AcheteurGui gui;
    protected AID[] vendeurs;
    @Override
    protected void setup() {
        if(getArguments().length==1){
            gui=(AcheteurGui) getArguments()[0];
            gui.acheteurAgent=this;
        }

        ParallelBehaviour parallelBehaviour= new ParallelBehaviour();
        addBehaviour(parallelBehaviour);

        parallelBehaviour.addSubBehaviour(new TickerBehaviour(this,5000) {
            @Override
            protected void onTick() {
                //chercher des services chaque 5 secondes
                DFAgentDescription dfAgentDescription = new DFAgentDescription();
                ServiceDescription serviceDescription = new ServiceDescription();
                serviceDescription.setType("transaction");
                serviceDescription.setName("vente-livres");
                dfAgentDescription.addServices(serviceDescription);
                try {
                    DFAgentDescription[] results = DFService.search(myAgent,dfAgentDescription);
                    vendeurs = new AID[results.length];
                    for (int i=0;i<vendeurs.length;i++){
                        vendeurs[i]=results[i].getName();
                    }
                } catch (FIPAException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
            private int counter=0;
            private List<ACLMessage> replies = new ArrayList<ACLMessage>();
            @Override
            public void action() {
                //accept just les message REQUEST & fr
                //MessageTemplate messageTemplate=MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
                MessageTemplate messageTemplate = MessageTemplate.or(
                        MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
                        MessageTemplate.or(
                                MessageTemplate.MatchPerformative(ACLMessage.PROPOSE),
                                MessageTemplate.or(
                                        MessageTemplate.MatchPerformative(ACLMessage.AGREE),
                                        MessageTemplate.MatchPerformative(ACLMessage.REFUSE)
                                )
                        )
                );
                //attendre des messages
                ACLMessage aclMessage = receive(messageTemplate);
                if(aclMessage!=null){

                    switch (aclMessage.getPerformative()){ //act de communication
                        case ACLMessage.REQUEST :
                            String livre = aclMessage.getContent();
                            ACLMessage aclMessage2 = new ACLMessage(ACLMessage.CFP);
                            aclMessage2.setContent(livre);
                            for(AID aid:vendeurs){
                                aclMessage2.addReceiver(aid);
                            }
                            send(aclMessage2);

                            break;
                        case ACLMessage.PROPOSE :
                            ++counter;
                            replies.add(aclMessage);
                            if(counter==vendeurs.length){
                                ACLMessage meilleurOffre = replies.get(0);
                                double mini=Double.parseDouble(meilleurOffre.getContent());

                                for(ACLMessage offre:replies){
                                    double price=Double.parseDouble(offre.getContent());
                                    if(price<mini){
                                        meilleurOffre=offre;
                                        mini=price;
                                    }
                                }
                                ACLMessage aclMessageAccept = meilleurOffre.createReply();
                                aclMessage.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                                send(aclMessageAccept);
                            }
                            break;
                        case ACLMessage.AGREE :
                            ACLMessage aclMessage3 = new ACLMessage(ACLMessage.CONFIRM);
                            aclMessage3.addReceiver(new AID("Consumer",AID.ISLOCALNAME));
                            aclMessage3.setContent(aclMessage.getContent());
                            send(aclMessage3);
                            break;
                        case ACLMessage.REFUSE :
                            break;
                        default:
                            break;
                    }

                    String livre = aclMessage.getContent();
                    // log le message dans l'interface graphique
                    gui.logMessage(aclMessage);

                    ACLMessage replay = aclMessage.createReply(); // je vais répondre aux memes sender
                    replay.setContent("Ok pou "+aclMessage.getContent());
                    send(replay);

                    /*ACLMessage aclMessage3 = new ACLMessage(ACLMessage.CFP);//call for proposal
                    aclMessage3.setContent(livre);
                    aclMessage3.addReceiver(new AID("VENDEUR", AID.ISLOCALNAME));
                    send(aclMessage3);*/
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

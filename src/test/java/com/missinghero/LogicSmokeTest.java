package com.missinghero;

import java.util.*;

/** Tests for the consolidated three-map story and its puzzles. */
public final class LogicSmokeTest {
    private static void require(boolean ok,String message){if(!ok)throw new AssertionError(message);}
    private static void engage(GameState g,String id){
        GameState.WorldObject obj=g.objects().stream().filter(o->o.id().equals(id)).findFirst()
            .orElseThrow(()->new AssertionError("Object not in this scene: "+id));
        g.placeForTest(obj.x(),obj.y());
        require(g.nearest()!=null&&g.nearest().id().equals(id),"Object obscured/unreachable: "+id);
        g.interact();
    }
    private static void clue(GameState g,String id){
        engage(g,id);
        require(g.collected().contains(id),"Clue cannot be collected: "+id);
        require(id.equals(g.openClue()),"Missing evidence popup: "+id);
        g.closeDialog();
    }
    private static void talk(GameState g,String id){
        engage(g,id);
        require(g.interviewed().contains(id),"Not interviewed: "+id);
        g.closeDialog();
    }
    private static void pass(GameState g,GameState.Scene destination){
        engage(g,"next");require(g.scene()==destination,"Failed to pass to "+destination+": "+g.messageBody());g.closeDialog();
    }
    public static void main(String[] args){
        GameState g=new GameState();require(g.scene()==GameState.Scene.TITLE,"title");g.start();g.closeDialog();
        require(g.scene()==GameState.Scene.BEDROOM&&g.stage()==1,"start in bedroom");
        g.placeForTest(1,3);g.move(-1,0);require(g.playerX()==1,"world border");
        engage(g,"next");require(g.scene()==GameState.Scene.BEDROOM,"bedroom not gated");g.closeDialog();
        clue(g,"collar");g.toggleFlashlight();engage(g,"door_scratch");
        require(!g.collected().contains("door_scratch"),"Clue needing flashlight was collectible in darkness");g.closeDialog();g.toggleFlashlight();
        clue(g,"door_scratch");clue(g,"ball");
        engage(g,"next");require(g.scene()==GameState.Scene.BEDROOM,"crates not required");g.closeDialog();
        engage(g,"crates");require(g.puzzle()==GameState.Puzzle.CRATES,"crate UI");
        g.placeCrate(0,1);require(!g.crates()[0],"incorrect crate placement accepted");
        for(int i=0;i<3;i++)g.placeCrate(i,i);require(g.crateSolved(),"crates unsolved");g.closeDialog();
        pass(g,GameState.Scene.LIVING);require(g.stage()==2,"living stage");
        // All old toy-room, storage and porch content must now be accessible in the living room.
        Set<String> livingIds=new HashSet<>();for(GameState.WorldObject o:g.objects())livingIds.add(o.id());
        require(livingIds.containsAll(List.of("photo","parcel","receipt","camera","drawer","cctv","next")),"missing merged living objects");
        for(String id:List.of("bowl","window","photo","parcel","receipt","camera"))clue(g,id);
        engage(g,"next");require(g.scene()==GameState.Scene.LIVING,"living exit skipped key/camera");g.closeDialog();
        engage(g,"drawer");require(g.puzzle()==GameState.Puzzle.DRAWER,"drawer in living");
        for(char d:"0000".toCharArray())g.addDigit(d);g.submitCode();require(!g.drawerSolved(),"wrong code accepted");g.closeDialog();
        for(char d:"0735".toCharArray())g.addDigit(d);g.submitCode();require(g.drawerSolved(),"receipt code rejected");g.closeDialog();
        engage(g,"next");require(g.scene()==GameState.Scene.LIVING,"camera step skipped");g.closeDialog();
        engage(g,"cctv");require(g.puzzle()==GameState.Puzzle.CAMERA,"camera puzzle in living");
        g.connectWire(0,0);require(!g.wires()[0],"wrong wire accepted");g.closeDialog();
        g.connectWire(0,2);g.connectWire(1,0);g.connectWire(2,1);require(g.cameraSolved(),"camera unsolved");g.closeDialog();
        pass(g,GameState.Scene.NEIGHBOR);require(g.stage()==3,"shared outdoor stage");
        Set<String> outdoorIds=new HashSet<>();for(GameState.WorldObject o:g.objects())outdoorIds.add(o.id());
        require(outdoorIds.containsAll(List.of("paw_hero","paw_other","max_collar","alley_photo","child","courier","guard","max","timeline")),"missing combined outdoor object");
        require(g.objects().stream().noneMatch(o->o.id().equals("next")),"no separate street/delivery/guard/alley stages");
        for(String id:List.of("paw_hero","paw_other"))clue(g,id);
        for(String id:List.of("max_collar","alley_photo"))clue(g,id);
        engage(g,"timeline");require(g.puzzle()!=GameState.Puzzle.TIMELINE,"not all witnesses interviewed");g.closeDialog();
        for(String id:List.of("child","courier","guard","max"))talk(g,id);
        require(g.collected().size()==GameState.TOTAL_CLUES&&g.interviewed().size()==4,"13 clues or 4 witnesses missing");
        engage(g,"timeline");require(g.puzzle()==GameState.Puzzle.TIMELINE,"timeline not opened");
        g.chooseEvent("friend");require(g.timeline().isEmpty(),"incorrect chronology accepted");g.closeDialog();
        for(String event:List.of("food","courier","guard","leave","walk","friend"))g.chooseEvent(event);
        require(g.timelineSolved(),"chronology wrong");g.closeDialog();
        require(g.scene()==GameState.Scene.INTERROGATION&&g.stage()==4,"interrogation");
        g.finalAnswer(4);require(g.scene()==GameState.Scene.TRUE_END,"true ending");
        g.start();g.closeDialog();g.previewScene(GameState.Scene.INTERROGATION);g.finalAnswer(1);
        require(g.scene()==GameState.Scene.BAD_END,"bad ending");
        for(String id:GameState.CLUES.keySet()){Art.get("clue_"+id);Art.get("item_"+id);}
        for(String map:List.of("bedroom","living","neighbor"))Art.get(map);
        System.out.println("PASS: 3 contiguous scenes, 13 evidence images, combined NPC area, 4 puzzles, both endings");
    }
}

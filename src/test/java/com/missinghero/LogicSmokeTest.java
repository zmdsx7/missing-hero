package com.missinghero;

/** Self-contained assertions: run with java -ea; no testing dependencies. */
public final class LogicSmokeTest {
    public static void main(String[] args) {
        GameState game=new GameState();
        if(game.scene!=GameState.Scene.TITLE) throw new AssertionError("title scene");
        game.start();game.dismissDialog();
        if(game.scene!=GameState.Scene.BEDROOM)throw new AssertionError("start bedroom");
        // Player cannot cross world boundary.
        game.x=1;game.y=3;game.move(-1,0);
        if(game.x!=1)throw new AssertionError("outside boundary");
        // Collect bedroom clues and unlock stairs.
        pickup(game,"collar");pickup(game,"photo");
        gotoSpot(game,"to_downstairs");
        if(game.scene!=GameState.Scene.DOWNSTAIRS)throw new AssertionError("stairs transition");
        game.dismissDialog();
        pickup(game,"bowl");pickup(game,"window");pickup(game,"scratch");
        gotoSpot(game,"to_street");
        if(game.scene!=GameState.Scene.STREET)throw new AssertionError("street transition");
        game.dismissDialog();
        pickup(game,"paw");pickup(game,"drawing");pickup(game,"tag");
        talk(game,"child");talk(game,"courier");talk(game,"guard");talk(game,"max");
        gotoSpot(game,"to_final");
        if(game.scene!=GameState.Scene.INTERROGATION)throw new AssertionError("interrogation");
        game.finalAnswer(5);game.finalAnswer(1);
        if(game.scene!=GameState.Scene.TRUE_END)throw new AssertionError("true ending");
        game.start();game.dismissDialog();
        game.scene=GameState.Scene.INTERROGATION;game.finalAnswer(2);
        if(game.scene!=GameState.Scene.BAD_END)throw new AssertionError("bad ending");
        System.out.println("PASS: title, movement, collection, gates, dialogues, true/bad endings");
    }
    private static void gotoSpot(GameState game,String id){
        GameState.Spot spot=game.spots().stream().filter(s->s.id().equals(id)).findFirst().orElseThrow();
        game.x=spot.x();game.y=spot.y();game.interact();game.dismissDialog();
    }
    private static void pickup(GameState game,String id){gotoSpot(game,id);if(!game.collected.contains(id))throw new AssertionError(id);}
    private static void talk(GameState game,String id){gotoSpot(game,id);if(!game.interviewed.contains(id))throw new AssertionError(id);}
}
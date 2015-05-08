import org.engineFRP.core.Engine;
import org.engineFRP.core.Game;
import org.engineFRP.core.GameObject;
import org.engineFRP.core.Scene;
import org.engineFRP.maths.Vector3f;
import org.engineFRP.rendering.MeshUtil;

/**
 * Created by TekMaTek on 06/05/2015.
 */
public class SimpleGame implements Game {

    public static void main(String[] args) {
        Engine.runGame(new SimpleGame());
//        Engine.runGame(() -> Scene.graph.add(
//                        new GameObject(Vector3f.ZERO, MeshUtil.BuildSquare())
//                                .name("MySquare"))
//        );//This is also a valid way of defining a game.
//        Engine.runGame(SimpleGame::setupScene);//For this to work setupScene must be static.
    }

    /**
     * This is a simple game that has nothing going on.
     *
     * @return The scene graph the engine should use.
     */
    @Override
    public Scene setupScene() {
        return Scene.graph.add(
                new GameObject(Vector3f.ZERO, MeshUtil.BuildSquare())
                        .name("MySquare")
        );
    }
}
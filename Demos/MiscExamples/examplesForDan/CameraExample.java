package MiscExamples.examplesForDan;

import org.engineFRP.FRP.FRPKeyboard;
import org.engineFRP.maths.Vector3f;
import sodium.Cell;
import sodium.Stream;
import sodium.StreamSink;

import java.lang.Boolean;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Created by TekMaTek on 24/04/2015.
 */
public class CameraExample {

    public static final StreamSink<GameState> curState = new StreamSink<>();
    private static GameState currentState = GameState.OTHER;

    private Cell<Vector3f> actualCameraPos;

    /**
     * So we compose for the instance of CameraExample (could be a single camera) a single stream for it's position below.
     * This stream is depended on three main streams FRPKeyboard, the playersMovement, and FRPKeyboard again.
     * This means any one of these streams being fired would cause the rest of the stream setup below to propagated.
     *
     * The streams are actually setup in a graph structure. Anything defined after something is considered 'Up Stream'
     * and only parts of the behaviour up stream will continue to propagate. So if playersMovement gets updated then
     * the first line movement(GameState.PLAY) isn't ever propagated. If movement(GameState.Play) is propagated then the
     * movementBasedOn(playersMovement) (which is two streams in itself) aren't propagated. Just like a river they merge
     * at this point and anything down stream is unreachable.
     *
     * When we hit the right arrow key, two of the three streams are propagated. It's only when one of them reaches the
     * state machine filter does it stop propagating further up stream. The only way to stop all from updating at the
     * same time here is through the game state machine we've defined. Otherwise all streams would be active which is
     * not the desired behaviour.
     *
     * This example isn't perfect and probably would need some tinkering to work perfectly but I think this is enough
     * to show how multiple camera logics would be put into a single stream.
     *
     * @param playersMovement
     */
    public CameraExample(Stream<Vector3f> playersMovement) {
        this.actualCameraPos = movement(GameState.PLAY)
                .merge(movementBasedOn(playersMovement))
                .coalesce((sameFiringFirstStream, sameFiringSecondStream) ->
                        sameFiringFirstStream.add(sameFiringSecondStream))//This is important!
                //because there are two stream that originate from the same source, when they fire they fire at the
                // same time. This defines how they should be dealt with when they both reach this point at the same
                // time. I'm not entirely sure how this works but it is needed otherwise one stream is just thrown away.
                .accum(Vector3f.ZERO, (curV, newV) -> curV.add(newV));
    }

    public static Stream<Vector3f> movement(GameState gameState) {
        final float MOVE_AMOUNT = 1.0f;
        return FRPKeyboard.keyEvent
                .gate(isMode(gameState))
                .filter(key -> FRPKeyboard.isArrowKeyPressed(key.code))
                .map(key -> {//Play state movement for camera
                    switch(key.code) {
                        case (GLFW_KEY_RIGHT):
                            return new Vector3f(-MOVE_AMOUNT, 0.0f, 0.0f);
                        case (GLFW_KEY_LEFT):
                            return new Vector3f(MOVE_AMOUNT, 0.0f, 0.0f);
                        case (GLFW_KEY_UP):
                            return new Vector3f(0.0f, -MOVE_AMOUNT, 0.0f);
                        case (GLFW_KEY_DOWN):
                            return new Vector3f(0.0f, MOVE_AMOUNT, 0.0f);
                        default:
                            assert false;//This shouldn't be called.
                            return Vector3f.ZERO;
                    }
                });
    }

    public static Stream<Vector3f> movementBasedOn(Stream<Vector3f> otherMovement) {
        return movement(GameState.CUT_SCENE)
                .merge(otherMovement);
    }

    public static void changeState(GameState newState) {
        currentState = newState;//this can be implemented without the currentState.
        curState.send(newState);
    }

    public static Cell<Boolean> isMode(GameState desiredState) {
        return curState
                .map(gameState -> gameState == desiredState)
                .hold(currentState == desiredState);
    }

    public enum GameState {
        PLAY,
        CUT_SCENE,
        OTHER
    }
}
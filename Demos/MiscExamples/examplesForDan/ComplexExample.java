package MiscExamples.examplesForDan;

import org.engineFRP.FRP.FRPKeyboard;
import org.engineFRP.maths.Vector3f;
import sodium.Cell;

import static MiscExamples.examplesForDan.CameraExample.GameState.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;

/**
 * Created by TekMaTek on 24/04/2015.
 */
public class ComplexExample {

    /**
     * This should create a smooth movement in the direction the arrow keys are pressed.
     *
     * @return The behaviour that should be assigned to the camera's position.
     */
    public static Cell<Vector3f> simple_lerp() {
        return CameraExample.movement(PLAY)
                .gate(CameraExample.isMode(PLAY))//Defines how to check the game mode.
                .accum(Vector3f.ZERO, (curValue, newValue) -> {
                    final float LERP_FACTOR = 0.2f;
                    return curValue.lerp(newValue, LERP_FACTOR);
                });//lerps most recent camera position onto existing position.
    }

    /**
     * This should create a smooth movement in the direction the arrow keys are pressed.
     * Note as this stream is defined from the Messaging stream this movement actually only triggers when a message is
     * sent from the messaging stream. There is no real reason to do this other than to make this example more
     * complex.
     *
     * @return The behaviour that should be assigned to the camera's position.
     */
    public static Cell<Vector3f> complex_example() {
        return Messaging.stream
                .gate(CameraExample.isMode(PLAY))//Defines how to check the game mode.
                .snapshot(CameraExample.movement(PLAY).hold(Vector3f.ZERO),//Defines how to take the most recent
                        // value received from the stream defined in movement.
                        (message, mostRecentCameraPos) -> mostRecentCameraPos.mul(message.myMessage.length()))//
                        // increases the camera's movement by the size of message for no real reason.
                .accum(Vector3f.ZERO, (curValue, newValue) -> {
                    final float LERP_FACTOR = 0.2f;
                    return curValue.lerp(newValue, LERP_FACTOR);
                });//lerps most recent camera position onto existing position.
    }

    public static void howToStartMovements() {
        Messaging.stream.send(new Messaging("1", Messaging.MType.MISC));
        FRPKeyboard.keyEvent.send(new FRPKeyboard.Key(GLFW_KEY_RIGHT, GLFW_PRESS));//This would also be triggered
        // in the game engine I've built when a key is pressed normally.
    }

}

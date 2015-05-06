package MiscExamples.examplesForDiss;

import org.engineFRP.FRP.FRPMouse;
import org.engineFRP.FRP.FRPTime;
import org.engineFRP.FRP.FRPUtil;
import org.engineFRP.core.GameObject;
import org.engineFRP.maths.Vector3f;
import org.lwjgl.openal.Util;
import sodium.Cell;
import sodium.Stream;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by TekMaTek on 30/04/2015.
 */
public class Diss {

    public static void imperativeSolution() {
        List<Integer> integers = Arrays.asList(1, 2, 3, 4, 5);
        List<Integer> integers2 = new LinkedList<>();
        for(int i = 0; i < integers.size(); i++) {
            integers2.add(i, integers.get(i) * 2);
        }
        for(Integer num : integers2) {
            System.out.println(num);
        }
    }

    public static void rpSolution() {
        Arrays.asList(1, 2, 3, 4, 5).stream()
                .map(f -> f * 2)
                .forEach(System.out::println);
    }

    public static Stream<Vector3f> circularMotion() {
        return FRPTime.streamDelta(FRPTime.THIRTY_PER_SECOND)
                .accum(0.0f, (total, delta) -> total += delta)
                .updates()
                .map(delta -> new Vector3f((float) Math.sin(0.75f * delta) / 80.0f,
                        (float) Math.cos(0.75f * delta) / 80.0f, 0.0f));
    }

    public static Stream<Vector3f> complexMovement() {
        return FRPMouse.cursorPosStream//Cursor position on the screen
                .map(cursor -> FRPMouse.screenToWorldSpace(cursor.position))
                .map(point2d -> new Vector3f(point2d.x, point2d.y, 0.0f));//Screen to 3d world coordinates
//                .merge(circularMotion());//Both streams now update the movement when either is invoked
//                .accum(Vector3f.ZERO, (curValue, newValue) -> curValue.add(newValue));//Updates the movement
    }
}
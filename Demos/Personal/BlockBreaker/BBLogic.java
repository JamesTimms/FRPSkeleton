package Personal.BlockBreaker;

import org.engineFRP.FRP.FRPKeyboard;
import org.engineFRP.FRP.FRPTime;
import org.engineFRP.FRP.FRPUtil;
import org.engineFRP.Physics.JBoxWrapper;
import org.engineFRP.FRP.ListenerArrayList;
import org.engineFRP.Physics.JBoxCollisionListener;
import org.engineFRP.Util.Util;
import org.engineFRP.core.GameObject;
import org.engineFRP.core.Scene;
import org.engineFRP.maths.Vector3f;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.contacts.Contact;
import sodium.Cell;
import sodium.Stream;
import sodium.Tuple2;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Created by TekMaTek on 22/04/2015.
 */
public class BBLogic {

    public static ListenerArrayList l = new ListenerArrayList();

    public static GameObject bouncyCollisionsWith(String otherGO, GameObject go) {
        BBLogic.l.add(JBoxCollisionListener.end
                .filter(contact -> areObjectsPresent(go, Scene.graph.find(otherGO).sample(), contact))
                .listen(contact -> {
                    GameObject go1 = JBoxWrapper.getGOFromBody(contact.getFixtureA().getBody());
                    GameObject go2 = JBoxWrapper.getGOFromBody(contact.getFixtureB().getBody());
                    GameObject thisGO = go.equals(go1) ? go1 : go2;
                    Vector3f v = go1.transform.translation.sample();
                    Vector3f v2 = go2.transform.translation.sample();

                    float xForce = v2.x - v.x;
                    thisGO.applyForce(new Vec2(xForce / 5.0f, 0.05f));
                }));
        return go;
    }

    public static GameObject betterBouncyCollisionsWith(String otherGO, GameObject go) {
        BBLogic.l.add(JBoxCollisionListener.end
                .filter(contact -> areObjectsPresent(go, Scene.graph.find(otherGO).sample(), contact))
                .listen(contact -> {
//                    GameObject go1 = JBoxWrapper.getGOFromBody(contact.getFixtureA().getBody());
//                    GameObject go2 = JBoxWrapper.getGOFromBody(contact.getFixtureB().getBody());
//                    GameObject thisGO = go.equals(go1) ? go1 : go2;
//                    Vec2 v = contact.getFixtureA().getBody().getLinearVelocity();
//                    thisGO.applyForce(new Vec2(v.x, 0.05f));
                }));
        return go;
    }

    protected static boolean areObjectsPresent(GameObject g, GameObject otherGO, Contact contact) {
        GameObject go = JBoxWrapper.getGOFromBody(contact.getFixtureA().getBody());
        GameObject go2 = JBoxWrapper.getGOFromBody(contact.getFixtureB().getBody());
        return (g.equals(go) || g.equals(go2)) &&
                (otherGO.equals(go) || otherGO.equals(go2));
    }

    public static GameObject canBeDestroyedBy(String otherGO, GameObject go) {
        BBLogic.l.add(JBoxCollisionListener.end
                .filter(contact -> areObjectsPresent(Scene.graph.find(otherGO).sample(), go, contact))
                .listen(contact -> {
                    JBoxWrapper.markForDeletion(go.physics.body);
                    Scene.graph.destroy(go);
                }));
        return go;
    }

    public static Stream<Vector3f> paddleMovement(float moveAmount) {
        return FRPKeyboard.keyEventSmooth
                .filter(key -> key.action != GLFW_RELEASE
                        && FRPKeyboard.isArrowKeyPressed(key.code))
                .map(key -> {
                    switch(key.code) {
                        case (GLFW_KEY_RIGHT):
                            return new Vector3f(-moveAmount, 0.0f, 0.0f);
                        case (GLFW_KEY_LEFT):
                            return new Vector3f(moveAmount, 0.0f, 0.0f);
                        default:
                            return Vector3f.ZERO;
                    }
                });
    }

    public static GameObject velocityOfBatStream(String a, GameObject go) {//TODO: clean up parameters and use.
        final float MAX_SPEED = 30.0f;
        final Vec2 SLOW_DOWN = new Vec2(20.0f, 0.0f);
        BBLogic.l.add(paddleMovement(-1.5f)
                        .map(Util::Vector3fToVec2)
                        .accum(new Vec2(0.0f, 0.0f), (newValue, curValue) -> {
                            Vec2 _speed = newValue.add(curValue);
                            if(Math.abs(_speed.x) > MAX_SPEED) {
                                _speed.x = MAX_SPEED * ((_speed.x > 0.0f) ? 1.0f : -1.0f);
                            }
                            return _speed;
                        })
                        .listen(go.physics.body::setLinearVelocity)
        );
        BBLogic.l.add(FRPTime.streamDelta(30)
                        .map(SLOW_DOWN::mul)
                        .listen(slowDown -> {
                            Vec2 curV = go.physics.body.getLinearVelocity();
                            float v = Math.abs(go.physics.body.getLinearVelocity().x);
                            if(v > 0.01f) {
                                Vec2 s = slowDown.mul(curV.x).negate();//.mul((curV.x > 0.0f) ? 1.0f : -1.0f);
                                go.physics.body.setLinearVelocity(curV.add(s));
                            }
                        })
        );
        return go;
    }
}

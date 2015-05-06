package MiscExamples.examplesForDan;

import sodium.Listener;
import sodium.StreamSink;import java.lang.String;

/**
 * Created by TekMaTek on 24/04/2015.
 */
public class Messaging {

    public static final StreamSink<Messaging> stream = new StreamSink<>();

    public String myMessage;
    public MType type;

    public Messaging(String message, MType type) {
        this.myMessage = message;
        this.type = type;
    }

    public static Listener example_use_of_stream() {
        return stream
                .filter(message -> message.type == MType.NORMAL)
                .map(messaging -> messaging.myMessage)
                .listen(m -> {//Listener should usually be avoided but using it here for simplicity.
                    /* some logic executed here */
                });
    }

    public enum MType {
        NORMAL,
        SUPER,
        MISC
    }
}
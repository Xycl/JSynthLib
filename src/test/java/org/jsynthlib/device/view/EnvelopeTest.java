package org.jsynthlib.device.view;

import static org.junit.Assert.assertEquals;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.jemmy.fx.AppExecutor;
import org.jemmy.fx.NodeDock;
import org.jemmy.fx.SceneDock;
import org.jsynthlib.device.model.DefaultEnvelopeModel;
import org.jsynthlib.device.model.EnvelopeNode;
import org.jsynthlib.device.model.EnvelopeXParam;
import org.jsynthlib.device.model.EnvelopeYParam;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class EnvelopeTest {

    private static TestApplication application;

    @BeforeClass
    public static void setUpClass() {
        AppExecutor.executeNoBlock(TestApplication.class);
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test() throws Exception {
        SceneDock scene = new SceneDock();
        NodeDock envelope = new NodeDock(scene.asParent(), Envelope.class);

//        Point start = new Point(47, 290);
//        Point end = new Point(47, 200);
//        Mouse mouse = envelope.mouse();
//        mouse.move(start);
//        Thread.sleep(1000);
//        mouse.press();
//        mouse.move(end);
//        Thread.sleep(1000);
//        mouse.release();

        Thread.sleep(60000);
        assertEquals(20, application.node1X.valueProperty().get());
    }

    public static class TestApplication extends Application {

        private final EnvelopeXParam node1X = new EnvelopeXParam(0, 0, "Time 1", false);
        private final EnvelopeYParam node1Y = new EnvelopeYParam(0, 100, "Level 1", 0);
        private final EnvelopeXParam node2X = new EnvelopeXParam(0, 100, "Time 2", false);
        private final EnvelopeYParam node2Y = new EnvelopeYParam(0, 100, "Level 2", 0);
        private final EnvelopeXParam node3X = new EnvelopeXParam(0, 100, "Time 3", false);
        private final EnvelopeYParam node3Y = new EnvelopeYParam(0, 100, "Level 3", 0);
        private final EnvelopeXParam node4X = new EnvelopeXParam(0, 100, "Time 4", false);
        private final EnvelopeYParam node4Y = new EnvelopeYParam(0, 100, "Level 4", 0);

        public TestApplication() {
            application = this;
        }

        @Override
        public void start(Stage arg0) throws Exception {
            Parent root =
                    FXMLLoader
                            .load(getClass().getResource("EnvelopeTest.fxml"));
            Scene scene = new Scene(root, 800, 600);
            ObservableList<Node> children =
                    scene.getRoot().getChildrenUnmodifiable();

            for (Node child : children) {
                if (child instanceof Envelope) {
                    Envelope envelope = (Envelope) child;
                    DefaultEnvelopeModel model =
                            new DefaultEnvelopeModel(new EnvelopeNode[] {
                                    new EnvelopeNode(node1X, node1Y),
                                    new EnvelopeNode(node2X, node2Y),
                                    new EnvelopeNode(node3X, node3Y),
                                    new EnvelopeNode(node4X, node4Y) });
                    envelope.setModel(model);
                }
            }
            arg0.setTitle("FXML Welcome");
            arg0.setScene(scene);

            node1Y.valueProperty().set(10);
            node2X.valueProperty().set(20);
            node2Y.valueProperty().set(30);
            node3X.valueProperty().set(40);
            node3Y.valueProperty().set(50);
            node4X.valueProperty().set(60);
            node4Y.valueProperty().set(70);

            arg0.show();
        }

        public static void main(String[] args) {
            launch();
        }
    }
}

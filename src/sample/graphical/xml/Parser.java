package sample.graphical.xml;

import javafx.util.Pair;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import sample.graphical.entity.GraphicalLineSection;
import sample.graphical.entity.GraphicalPicture;
import sample.graphical.entity.GraphicalPoint;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Parser {
    private final int MULT_PARAM = 100;

    public GraphicalPicture parse() {
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = documentBuilder.parse("src/resources/geogebra.xml");

            Node root = document.getDocumentElement();

            List<Pair<String, String>> connections = new ArrayList<>();
            Map<String, GraphicalPoint> points = new HashMap<>();

            NodeList nodes = root.getChildNodes();
            for (int i = 0; i < nodes.getLength(); i++) {
                for (int j = 0; j < nodes.item(i).getChildNodes().getLength(); j++) {
                    Node element = nodes.item(i).getChildNodes().item(j);

                    if ("element".equals(element.getNodeName()) && element.getAttributes().getNamedItem("type") != null && "point".equals(element.getAttributes().getNamedItem("type").getNodeValue())) {
                        for (int k = 0; k < element.getChildNodes().getLength(); k++) {
                            if ("coords".equals(element.getChildNodes().item(k).getNodeName())) {
                                points.put(element.getAttributes().getNamedItem("label").getNodeValue(),
                                        GraphicalPoint.builder()
                                                .x((int) (Double.parseDouble(String.valueOf(element.getChildNodes().item(k).getAttributes().getNamedItem("x").getNodeValue())) * MULT_PARAM))
                                                .y((int) (Double.parseDouble(String.valueOf(element.getChildNodes().item(k).getAttributes().getNamedItem("y").getNodeValue())) * MULT_PARAM))
                                                .build());
                            }
                        }

                    } else if ("command".equals(element.getNodeName()) && element.getAttributes().getNamedItem("name") != null && "Segment".equals(element.getAttributes().getNamedItem("name").getNodeValue())) {
                        for (int k = 0; k < element.getChildNodes().getLength(); k++) {
                            if ("input".equals(element.getChildNodes().item(k).getNodeName())) {
                                connections.add(new Pair<>(element.getChildNodes().item(k).getAttributes().getNamedItem("a0").getNodeValue(),
                                        element.getChildNodes().item(k).getAttributes().getNamedItem("a1").getNodeValue()));
                            }
                        }
                    }
                }
            }

            int deltaX = points.values().stream().map(GraphicalPoint::getX).min(Integer::compareTo).orElse(-1) + 1;
            int deltaY = points.values().stream().map(GraphicalPoint::getY).min(Integer::compareTo).orElse(-1) + 1;

            points.values().forEach(point -> {
                point.setX(point.getX() - deltaX);
                point.setY(point.getY() - deltaY);
            });

            List<GraphicalLineSection> sections = new ArrayList<>();

            connections.forEach(connection -> sections.add(GraphicalLineSection.builder()
            .startX(points.get(connection.getKey()).getX())
            .startY(points.get(connection.getKey()).getY())
            .endX(points.get(connection.getValue()).getX())
            .endY(points.get(connection.getValue()).getY())
            .build()));

            GraphicalPicture picture = GraphicalPicture.builder()
//                    .centerX((int) points.values().stream().mapToInt(GraphicalPoint::getX).average().orElse(0))
//                    .centerY((int) points.values().stream().mapToInt(GraphicalPoint::getY).average().orElse(0))
                    .centerX(GraphicalPicture.canonicalCenterX)
                    .centerY(GraphicalPicture.canonicalCenterY)
                    .build();
            picture.addPointsToList(sections);
            return picture;

        } catch (ParserConfigurationException | IOException | SAXException ex) {
            ex.printStackTrace(System.out);
        }
        return null;
    }
}

package cage.viewer.twoview;

import cage.EdgeIterator;
import cage.EmbeddableGraph;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public abstract class TwoViewPainter {

    private EmbeddableGraph graph;
    private int graphSize;
    private float coordinate[][];
    private FloatingPoint p[];
    private double xMin, xMax, yMin, yMax;
    private double horMin, horMax, verMin, verMax;
    private int horSign, verSign;
    private double scale, delta, horOffset, verOffset;
    private int rotation = 0;

    private boolean isPartOfHighlightedFace[][];
    private boolean highlightedFacesAlreadyDetermined = false;

    protected TwoViewModel model;

    public TwoViewPainter(TwoViewModel model) {
        this.model = model;
        this.model.addTwoViewListener(new TwoViewAdapter() {
            @Override
            public void highlightedFacesChanged() {
                highlightedFacesAlreadyDetermined = false;
                isPartOfHighlightedFace = new boolean[graphSize+1][graphSize+1];
            }
        });
    }

    public void setGraph(EmbeddableGraph graph) {
        this.graph = graph;
        coordinate = graph.get2DCoordinates();
        graphSize = graph.getSize();
        if (graphSize <= 0) {
            return;
        }
        calculateBoundingBox();

        isPartOfHighlightedFace = new boolean[graphSize+1][graphSize+1];
        highlightedFacesAlreadyDetermined = false;
    }

    /**
     * @param embedding a list with the ordered list of neighbours of each vertex
     * @return true if edge v1 v2 is part of a pentagon
     */
    private boolean edgeIsPartOfFaceWithGivenSize(int v1, int v2, List<List<Integer>> embedding) {
        int v1_temp = v1;
        int v2_temp = v2;

        //First investigating the face at one of the edge
        for(int i = 0; i < model.getHighlightedFacesSize(); i++) {
            List<Integer> neighbours = embedding.get(v2_temp);
            int previous_index = neighbours.indexOf(v1_temp);
            if(previous_index == -1) {
                throw new RuntimeException("Vertex " + v1_temp + " not found in list of neighbours of " + v2_temp);
            }
            v1_temp = v2_temp;
            v2_temp = neighbours.get((previous_index - 1 + neighbours.size()) % neighbours.size());
            if(v1_temp == v1 && v2_temp == v2 && i < model.getHighlightedFacesSize()-1) {
                //prevent faces that have a size that is a divisor of the requested
                //size to be highlighted
                v1_temp = -1;
                break;
            }
        }
        if(v1_temp == v1 && v2_temp == v2) {
            return true;
        }

        v1_temp = v1;
        v2_temp = v2;
        //Investigating the face to the other side of the edge
        for(int i = 0; i < model.getHighlightedFacesSize(); i++) {
            List<Integer> neighbours = embedding.get(v2_temp);
            int previous_index = neighbours.indexOf(v1_temp);
            if(previous_index == -1) {
                throw new RuntimeException("Vertex " + v1_temp + " not found in list of neighbours of " + v2_temp);
            }
            v1_temp = v2_temp;
            v2_temp = neighbours.get((previous_index + 1) % neighbours.size());
            if(v1_temp == v1 && v2_temp == v2 && i < model.getHighlightedFacesSize()-1) {
                //prevent faces that have a size that is a divisor of the requested
                //size to be highlighted
                v1_temp = -1;
                break;
            }
        }

        return v1_temp == v1 && v2_temp == v2;
    }

    private void determineHighlightedFaces() {
        //The neighbours of each vertex
        List<List<Integer>> embedding = new ArrayList<>();

        //For i = 0
        embedding.add(new ArrayList<Integer>());
        for (int i = 1; i <= graphSize; i++) {
            EdgeIterator it = graph.getEdgeIterator(i);
            List<Integer> neighbours = new ArrayList<>();
            while(it.hasNext()) {
                neighbours.add(it.nextEdge());
            }
            embedding.add(neighbours);
        }

        for (int i = graphSize; i > 0; --i) {
            EdgeIterator it = graph.getEdgeIterator(i);
            while(it.hasNext()) {
                int j = it.nextEdge();

                //Algorithm is not really efficient, but it is certainly not a bottleneck
                if(edgeIsPartOfFaceWithGivenSize(i, j, embedding)) {
                    isPartOfHighlightedFace[i][j] = true;
                    isPartOfHighlightedFace[j][i] = true;
                }
                //TODO: why does this not work? Fixed by recreating array when face size changes
                //else {
                //    isPartOfHighlightedFace[i][j] = false;
                //    isPartOfHighlightedFace[j][i] = false;
                //}
            }
        }

        highlightedFacesAlreadyDetermined = true;

    }

    public void setPaintArea(double horMin, double horMax, double verMin, double verMax) {
        this.verMin = verMin;
        this.verMax = verMax;
        if (horMin <= horMax) {
            horSign = +1;
            this.horMin = horMin;
            this.horMax = horMax;
        } else {
            horSign = -1;
            this.horMin = horMax;
            this.horMax = horMin;
        }
        if (verMin <= verMax) {
            verSign = +1;
            this.verMin = verMin;
            this.verMax = verMax;
        } else {
            verSign = -1;
            this.verMin = verMax;
            this.verMax = verMin;
        }
        viewportChanged();
    }

    public int getRotation() {
        return rotation;
    }

    public void setRotation(int rotation) {
        if(Math.abs(rotation)<=180){
            this.rotation = rotation;
            rotationChanged();
        }
    }
    
    void rotationChanged() {
        calculateBoundingBox();
    }
    
    void calculateBoundingBox(){
        //don't do anything if the graph hasn't been set yet
        if(coordinate==null){
            return;
        }
        
        double angle = rotation*Math.PI/180;
        xMin = xMax = coordinate[0][0]*Math.cos(angle)-coordinate[0][1]*Math.sin(angle);
        yMin = yMax = coordinate[0][0]*Math.sin(angle)+coordinate[0][1]*Math.cos(angle);
        for (int i = 0; i < graphSize; ++i) {
            double rotatedX = coordinate[i][0]*Math.cos(angle)-coordinate[i][1]*Math.sin(angle);
            double rotatedY = coordinate[i][0]*Math.sin(angle)+coordinate[i][1]*Math.cos(angle);
            xMin = Math.min(xMin, rotatedX);
            xMax = Math.max(xMax, rotatedX);
            yMin = Math.min(yMin, rotatedY);
            yMax = Math.max(yMax, rotatedY);
        }
        //viewportChanged needs to be called since xMin, xMax, yMin or yMax might have changed
        viewportChanged();
    }

    void viewportChanged() {
        if (horSign != 0 && verSign != 0) {
            double horRng = horMax - horMin;
            double verRng = verMax - verMin;
            if (xMin == xMax || yMin == yMax) {
                delta = Math.max((xMax - xMin) / verRng, (yMax - yMin) / horRng) / 1e6;
            } else {
                delta = Math.min((xMax - xMin) / horRng, (yMax - yMin) / verRng) / 1e6;
            }
            scale = Math.max(delta, Math.min(horRng / (xMax - xMin + delta), verRng / (yMax - yMin + delta)));
            horOffset = (xMin + xMax + delta * horSign) / 2 * scale * horSign - horRng / 2 - horMin;
            verOffset = (yMin + yMax + delta * verSign) / 2 * scale * verSign - verRng / 2 - verMin;
            if (graphSize <= 0) {
                p = null;
            } else {
                p = new FloatingPoint[graphSize + 1];
                for (int i = 0, j = 1; i < graphSize; i = j++) {
                    p[j] = getPoint(coordinate[i][0], coordinate[i][1]);
                }
            }
        }
    }

    /**
     * Translates the coordinates of a vertex in 'graph space' to a coordinate
     * in 'component space'.
     * @param x
     * @param y
     * @return the transformed coordinates
     */
    public FloatingPoint getPoint(double x, double y) {
        double angle = rotation*Math.PI/180;
        double rotatedX = x*Math.cos(angle)-y*Math.sin(angle);
        double rotatedY = x*Math.sin(angle)+y*Math.cos(angle);
        FloatingPoint point = new FloatingPoint();
        point.x = Math.round((rotatedX * scale * horSign - horOffset - horMin) / delta) * delta + horMin;
        point.y = Math.round((rotatedY * scale * verSign - verOffset - verMin) / delta) * delta + verMin;
        return point;
    }

    /**
     * Translates a set of coordinates in 'component space' to a set of
     * coordinates in 'graph space'.
     * @param px
     * @param py
     * @return the transformed coordinates
     */
    public FloatingPoint getCoordinate(double px, double py) {
        FloatingPoint point = new FloatingPoint();
        double rotatedPx = (px + horOffset) / scale * horSign;
        double rotatedPy = (py + verOffset) / scale * verSign;
        double angle = -rotation*Math.PI/180;
        point.x = rotatedPx*Math.cos(angle)-rotatedPy*Math.sin(angle);
        point.y = rotatedPx*Math.sin(angle)+rotatedPy*Math.cos(angle);
        return point;
    }

    public FloatingPoint[] getBoundingBox() {
        FloatingPoint[] box = new FloatingPoint[2];
        box[0] = getPoint(xMin, yMin);
        box[1] = getPoint(xMax, yMax);
        return box;
    }

    /**
     * Returns the size of the current graph.
     *
     * @return the size of the current graph
     */
    protected int getGraphSize() {
        return graphSize;
    }

    /**
     * Returns the coordinates of vertex <i>n</i>. The first vertex has
     * number 1.
     *
     * @param n the index of the vertex
     * @return the coordinates of vertex <i>n</i>
     */
    protected FloatingPoint getCoordinatePoint(int n) {
        return p[n];
    }

    protected boolean startWithEdges(){
        return true;
    }

    public void paintGraph() {
        if(model.highlightFaces() && !highlightedFacesAlreadyDetermined)
            determineHighlightedFaces();

        beginGraph();

        if(p==null){
            throw new IllegalStateException("Vertex coordinates have not been initialized.");
        }

        if(startWithEdges()){
            beginEdges();
            for (int i = graphSize; i > 0; --i) {
                EdgeIterator it = graph.getEdgeIterator(i);
                while (it.hasNext()) {
                    int j = it.nextEdge();
                    if (j >= i) {
                        continue; // draw only edges to vertices that aren't drawn yet
                    }

                    paintEdge(p[i].x, p[i].y, p[j].x, p[j].y, i, j,
                            model.highlightFaces() && isPartOfHighlightedFace[i][j]);
                }
            }
            beginVertices();
            for (int i = graphSize; i > 0; --i) {
                paintVertex(p[i].x, p[i].y, i);
            }
        } else {
            beginVertices();
            for (int i = graphSize; i > 0; --i) {
                paintVertex(p[i].x, p[i].y, i);
            }
            beginEdges();
            for (int i = graphSize; i > 0; --i) {
                EdgeIterator it = graph.getEdgeIterator(i);
                while (it.hasNext()) {
                    int j = it.nextEdge();
                    if (j >= i) {
                        continue; // draw only edges to vertices that aren't drawn yet
                    }

                    paintEdge(p[i].x, p[i].y, p[j].x, p[j].y, i, j,
                            model.highlightFaces() && isPartOfHighlightedFace[i][j]);
                }
            }
        }
        endGraph();
        
    }

    protected abstract void beginGraph();

    protected abstract void beginEdges();

    protected abstract void paintEdge(double x1, double y1, double x2, double y2, int v1, int v2, boolean useSpecialColour);

    protected abstract void beginVertices();

    protected abstract void paintVertex(double x, double y, int number);

    protected abstract void endGraph();
}

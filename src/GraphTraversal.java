/**
 * @author Maxx Boehme
 */

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

public class GraphTraversal implements Runnable{

    private Graph graph;
    private TraversalType t;
    private Graph.Vertex start;
    private Graph.Vertex goal;
    private boolean stop;
    private boolean isStoped;

    private int waitTime = 0;
    private Heuristic h;

    private GraphTraversalVisualPanel vp;

    GraphTraversal(Graph g, TraversalType t, Graph.Vertex s, Graph.Vertex goal, int wt, Heuristic h, GraphTraversalVisualPanel vp){
        this.graph = g;
        this.t = t;
        this.start = s;
        this.goal = goal;
        this.stop = false;
        this.isStoped = true;
        this.waitTime = wt;
        this.h = h;

        this.vp = vp;
    }

    @Override
    public void run() {
        this.isStoped = false;
        boolean foundPath = false;
        if(this.graph != null && this.t != null){
            if(this.t == TraversalType.Dijkstra){
                foundPath = this.dijkstra(this.start, this.goal);
            } else {
                foundPath = this.astar(this.start, this.goal, this.h);
            }
        }
        if(!this.stop && foundPath){
            this.vp.setPath(this.getPath());
        }
        this.isStoped = true;
    }

    public boolean isStoped(){
        return this.isStoped;
    }

    public void stop(){
        this.stop = true;
    }

    private boolean dijkstra(Graph.Vertex s, Graph.Vertex g) {
        for(Graph.Vertex v : this.graph.getGraph()){
            v.setVisited(false);
            v.setCost(Integer.MAX_VALUE);
        }
        s.setCost(0);
        s.setParent(null);
        Comparator<Graph.Vertex> c = new Comparator<Graph.Vertex>(){
            public int compare(Graph.Vertex i, Graph.Vertex j){
                double difference = (i.getCost()-j.getCost());
                if(difference > 0){
                    return 1;
                }
                if(difference < 0){
                    return -1;
                }
                return 0;
            }
        };
        PriorityQueue<Graph.Vertex> fringe = new PriorityQueue<Graph.Vertex>(20, c);
        fringe.add(s);
        while(!fringe.isEmpty() && !this.stop){
            Graph.Vertex v = fringe.remove();
            v.setInFringe(false);
            if(v.equals(g)){
                return true;
            }else if(!v.isVisited()){
                v.setVisited(true);
                for(Graph.Edge e: v.getEdges()){
                    if(this.stop){
                        break;
                    }
                    double newcost = v.getCost() + e.getCost();
                    Graph.Vertex to = e.getTo();
                    if(to.getType() != Graph.Vertex.VertexType.Block && newcost < to.getCost()){
                        to.setCost(newcost);
                        to.setParent(v);
                        fringe.add(to);
                        to.setInFringe(true);

                    }
                    if(!this.stop){
                        try {
                            Thread.sleep(this.waitTime);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean astar(Graph.Vertex start, Graph.Vertex goal, Heuristic h ) {
        for(Graph.Vertex v : this.graph.getGraph()){
            v.setVisited(false);
            v.setCost(Integer.MAX_VALUE);
        }
        start.setCost(0);
        start.setParent(null);
        Comparator<Graph.Vertex> c = new Comparator<Graph.Vertex>(){
            public int compare(Graph.Vertex i, Graph.Vertex j){
                double difference = (i.getEstimate()-j.getEstimate());
                if(difference > 0){
                    return 1;
                }
                if(difference < 0){
                    return -1;
                }
                return 0;
            }
        };
        PriorityQueue<Graph.Vertex> fringe = new PriorityQueue<Graph.Vertex>(20, c);
        fringe.add(start);
        while(!fringe.isEmpty() && !this.stop){
            Graph.Vertex v = fringe.remove();
            v.setInFringe(false);
            if(v.equals(goal)){
                return true;
            }else if(!v.isVisited()){
                v.setVisited(true);
                for(Graph.Edge e: v.getEdges()){
                    if(this.stop){
                        break;
                    }
                    double newcost = v.getCost() + e.getCost();
                    Graph.Vertex to = e.getTo();
                    if(to.getType() == Graph.Vertex.VertexType.Empty && newcost < to.getCost()){
                        to.setCost(newcost);
                        to.setEstimate(h.estimate(to, goal));
                        to.setParent(v);
                        fringe.add(to);
                        to.setInFringe(true);
                        if(!this.stop){
                            try {
                                Thread.sleep(this.waitTime);
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public ArrayList<Graph.Vertex> getPath(){
        Graph.Vertex v = this.goal;
        ArrayList<Graph.Vertex> result = new ArrayList<Graph.Vertex>();
        while(v != null){
            result.add(v);
            v = v.getParent();
        }
        return result;
    }

    public static enum TraversalType {
        Dijkstra, Astar;
    }
}

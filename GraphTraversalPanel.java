/**
 * @author Maxx Boehme
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Random;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;



public class GraphTraversalPanel extends JPanel implements Runnable{

	private static final long serialVersionUID = 6382904795938277521L;

	private GraphTraversalVisualPanel vp;
	private Graph graph;
	private GraphTraversal gt;

	public static final int FRAMES_PER_SECOND = 24;
	/**
	 * The number of milliseconds that should pass between each frame.
	 */
	private static final long FRAME_TIME = 1000L / FRAMES_PER_SECOND;

	private Heuristic[] heuristics;
	
	private JButton startButton;
	private JButton clearButton;
	private JComboBox<GraphTraversal.TraversalType> comboBox;
	private JSpinner spinner;
	private JButton clearTraversal;
	private JComboBox<HeuristicTypes> heuristicChooser;
	private JComboBox<Integer> graphSizeBox;
	private final JComboBox<GraphTraversalVisualPanel.PathDisplay> pathDisplayBox;

	/**
	 * Create the panel.
	 */
	public GraphTraversalPanel() {
		this.graph = new Graph();
		this.graph.resetToSize(20);
		setLayout(null);
		this.setSize(800, 600);
		this.setPreferredSize(getSize());

		this.vp = new GraphTraversalVisualPanel(this.graph);
		this.vp.setSize(561, 561);
		this.vp.setLocation(10, 11);
		this.vp.setVisible(true);
		this.add(this.vp);

		this.heuristics = new Heuristic[5];
		this.heuristics[0] = new Heuristic() {
			public double estimate(Graph.Vertex from, Graph.Vertex to) {
				return from.distanceTo(to); }
		};

		this.heuristics[1] = new Heuristic() {
			public double estimate(Graph.Vertex from, Graph.Vertex to) {
				return from.distanceTo(to)/2;
			}
		};

		this.heuristics[2] = new Heuristic() {
			public double estimate(Graph.Vertex from, Graph.Vertex to) {
				return 0;
			}
		};

		final Random rng = new Random();

		this.heuristics[3] = new Heuristic() {
			public double estimate(Graph.Vertex from, Graph.Vertex to) {
				return (from.distanceTo(to)*rng.nextDouble());
			}
		};

		this.heuristics[4] = new Heuristic() {
			public double estimate(Graph.Vertex from, Graph.Vertex to) {
				return (rng.nextDouble() * 5000.0); }
		};

		this.startButton = new JButton("Start");
		startButton.setBounds(602, 216, 89, 23);
		add(startButton);

		this.clearButton = new JButton("Clear Screen");
		clearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(gt != null){
					gt.stop();
				}
				graph.clear();
				vp.setPath(null);
			}
		});
		clearButton.setBounds(642, 250, 125, 23);
		add(clearButton);

		this.gt = null;


		this.comboBox = new JComboBox<GraphTraversal.TraversalType>();
		comboBox.setModel(new DefaultComboBoxModel<GraphTraversal.TraversalType>(GraphTraversal.TraversalType.values()));
		comboBox.setBounds(602, 40, 89, 20);
		add(comboBox);

		this.spinner = new JSpinner();
		spinner.setModel(new SpinnerNumberModel(0, 0, 100, 1));
		spinner.setBounds(602, 94, 89, 20);
		add(spinner);

		this.clearTraversal = new JButton("Clear");
		clearTraversal.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(gt != null){
					gt.stop();
				}
				graph.clearStates();
				vp.setPath(null);
			}
		});
		clearTraversal.setBounds(701, 216, 89, 23);
		add(clearTraversal);
		
		this.heuristicChooser = new JComboBox<HeuristicTypes>();
		heuristicChooser.setModel(new DefaultComboBoxModel<HeuristicTypes>(HeuristicTypes.values()));
		heuristicChooser.setBounds(602, 150, 89, 20);
		add(heuristicChooser);
		
		this.graphSizeBox = new JComboBox<Integer>();
		graphSizeBox.setModel(new DefaultComboBoxModel<Integer>(getSizes()));
		graphSizeBox.setSelectedIndex(8);
		graphSizeBox.setBounds(602, 387, 63, 20);
		add(graphSizeBox);
		
		JLabel lblGraphTraversalAlgorithm = new JLabel("Graph Traversal Algorithm");
		lblGraphTraversalAlgorithm.setBounds(602, 11, 165, 18);
		add(lblGraphTraversalAlgorithm);
		
		JLabel lblWaitmilliseconds = new JLabel("Wait (milliseconds)");
		lblWaitmilliseconds.setBounds(602, 71, 165, 14);
		add(lblWaitmilliseconds);
		
		JLabel lblAHeristic = new JLabel("A* Heristic");
		lblAHeristic.setBounds(602, 125, 165, 14);
		add(lblAHeristic);
		
		JLabel lblGraphSizewidth = new JLabel("Graph Size");
		lblGraphSizewidth.setBounds(602, 362, 89, 14);
		add(lblGraphSizewidth);
		
		this.pathDisplayBox = new JComboBox<GraphTraversalVisualPanel.PathDisplay>();
		pathDisplayBox.setModel(new DefaultComboBoxModel<GraphTraversalVisualPanel.PathDisplay>(GraphTraversalVisualPanel.PathDisplay.values()));
		pathDisplayBox.setBounds(701, 387, 66, 20);
		add(pathDisplayBox);
		
		JLabel lblNewLabel = new JLabel("Path Display");
		lblNewLabel.setBounds(701, 362, 89, 14);
		add(lblNewLabel);
		
		graphSizeBox.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg){
				if(gt != null){
					gt.stop();
				}
				graph.resetToSize(graphSizeBox.getItemAt(graphSizeBox.getSelectedIndex()));
				vp.setGraph(graph);
			}
		});
		
		pathDisplayBox.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg){
				vp.setPathDisplay(pathDisplayBox.getItemAt(pathDisplayBox.getSelectedIndex()));
			}
		});

		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(gt != null){
					gt.stop();
				}
				gt = null;

				graph.clearStates();
				vp.setPath(null);
				gt = new GraphTraversal(graph, comboBox.getItemAt(comboBox.getSelectedIndex()), vp.getStart(), vp.getGoal(), (Integer)spinner.getValue(), heuristics[heuristicChooser.getItemAt(heuristicChooser.getSelectedIndex()).ordinal()], vp);
				Thread t = new Thread(gt);
				t.start();
			}
		});
	}

	@Override
	public void run() {
		while(true){
			long start = System.currentTimeMillis();

			this.vp.repaint();
			/*
			 * Calculate the delta time between since the start of the frame
			 * and sleep for the excess time to cap the frame rate. While not
			 * incredibly accurate, it is sufficient for our purposes.
			 */
			long delta = (System.currentTimeMillis() - start);
			//			System.out.println("Delta: "+delta);
			if(delta < FRAME_TIME) {
				try {
					Thread.sleep(FRAME_TIME - delta);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private Integer[] getSizes(){
		double width = this.vp.getWidth()-1;
		LinkedList<Integer> result = new LinkedList<Integer>();
		for(int i = 1; i < width; i++){
			double n = (int)width/i;
			if((width/i) == n){
				result.addFirst((int)n);
			}
		}
		result.removeLast();
		Integer[] r = new Integer[result.size()];
		result.toArray(r);
		return r;
	}

	/**
	 * Used to deceiver which Heuristic to use for the A* Algorithm
	 */
	private static enum HeuristicTypes{
		dist, halfass, zip, randombs, randomlies;
	}
}

package synopticgwt.client.model;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Used to create the graphic representing the Synoptic model.
 */
public class ModelGraphic {
    // //////////////////////////////////////////////////////////////////////////
    // JSNI methods -- JavaScript Native Interface methods. The method body of
    // these calls is pure JavaScript.

    /**
     * Updates the model edges to display transition probabilities.
     */
    public static native void useProbEdgeLabels() /*-{
		var g = $wnd.GRAPH_HANDLER.getGraph();

		var edges = g.edges;
		for (i = 0; i < edges.length; i++) {
			edges[i].connection.label.attr({
				text : edges[i].style.labelProb
			});
		}
    }-*/;

    /**
     * Updates the model edges to display transition counts.
     */
    public static native void useCountEdgeLabels() /*-{
		var g = $wnd.GRAPH_HANDLER.getGraph();

		var edges = g.edges;
		for (i = 0; i < edges.length; i++) {
			edges[i].connection.label.attr({
				text : edges[i].style.labelCount
			});
		}
    }-*/;

    /**
     * A JSNI method to create and display a graph.
     * 
     * @param modelTab
     *            A reference to the modelTab instance containing this graphic.
     * @param nodes
     *            An array of nodes, each consecutive pair is a <id,label>
     * @param edges
     *            An array of edges, each consecutive pair is <node id, node id>
     * @param width
     *            width of the graph
     * @param height
     *            height of the graph
     * @param canvasId
     *            the div id with which to associate the resulting graph
     */
    public static native void createGraph(ModelTab modelTab,
            JavaScriptObject nodes, JavaScriptObject edges, int width,
            int height, String canvasId, String initial, String terminal) /*-{

		// Define all global functions.
		@synopticgwt.client.model.ModelGraphic::defineGlobalFunctions(Lsynopticgwt/client/model/ModelTab;)(modelTab);

		// Create the graph.
		var g = new $wnd.Graph();
		g.edgeFactory.template.style.directed = true;

		// Add each node to graph.
		for ( var i = 0; i < nodes.length; i += 2) {
			g.addNode(nodes[i], {
				label : nodes[i + 1],
				render : $wnd.GRAPH_HANDLER.render
			});
		}

		// TODO: refactor this code, and merge with same (edge creation) logic
		// in graphhandler.js (under "updateRefinedGraph") 

		// Add each edge to graph.
		var showCounts = modelTab.@synopticgwt.client.model.ModelTab::getShowEdgeCounts()();
		$wnd.GRAPH_HANDLER.currentEdges = [];
		for ( var i = 0; i < edges.length; i += 4) {
			// edges[i]: source, edges[i+1]: target, edges[i+2]: weight for the label.
			if (showCounts) {
				labelVal = edges[i + 3];
			} else {
				labelVal = edges[i + 2];
			}
			style = {
				label : labelVal,
				labelProb : edges[i + 2],
				labelCount : edges[i + 3],
			};
			edge = g.addEdge(edges[i], edges[i + 1], style);
			$wnd.GRAPH_HANDLER.currentEdges.push({
				"edge" : edge,
				"style" : style
			});
		}
		// Give stable layout to graph elements.
		var layouter = new $wnd.Graph.Layout.Stable(g, initial, terminal);

		// Render the graph.
		var renderer = new $wnd.Graph.Renderer.Raphael(canvasId, g, width,
				height);

		// Store graph state.
		$wnd.GRAPH_HANDLER.initializeStableIDs(nodes, edges, renderer,
				layouter, g);
    }-*/;

    private static native void defineGlobalFunctions(ModelTab modelTab) /*-{
		// Determinize Math.random() calls for deterministic graph layout. Relies on seedrandom.js
		$wnd.Math.seedrandom($wnd.randSeed);

		// Export the handleLogRequest globally.
		$wnd.viewLogLines = function(id) {
			@synopticgwt.client.model.ModelGraphic::clearEdgeState()();
			modelTab.@synopticgwt.client.model.ModelTab::handleLogRequest(I)(id);
		};

		// Export global add/remove methods for selected nodes (moving 
		// nodes to model tab).
		$wnd.addSelectedNode = function(id) {
			modelTab.@synopticgwt.client.model.ModelTab::addSelectedNode(I)(id);
		};

		$wnd.removeSelectedNode = function(id) {
			modelTab.@synopticgwt.client.model.ModelTab::removeSelectedNode(I)(id);
		};
    }-*/;

    /**
     * A JSNI method to update and display a refined graph, animating the
     * transition to a new layout.
     * 
     * @param nodes
     *            An array of nodes, each consecutive pair is a <id,label>
     * @param edges
     *            An array of edges, each consecutive pair is <node id, node id>
     * @param refinedNode
     *            the ID of the refined node
     * @param canvasId
     *            the div id with which to associate the resulting graph
     */
    public static native void createChangingGraph(JavaScriptObject nodes,
            JavaScriptObject edges, int refinedNode, String canvasId,
            ModelTab modelTab) /*-{

		// Determinize Math.random() calls for deterministic graph layout. Relies on seedrandom.js
		$wnd.Math.seedrandom($wnd.randSeed);

		// Clear the selected nodes from the graph's state.
		$wnd.clearSelectedNodes();

		// update graph and fetch array of new nodes
		var showCounts = modelTab.@synopticgwt.client.model.ModelTab::getShowEdgeCounts()();
		var newNodes = $wnd.GRAPH_HANDLER.updateRefinedGraph(nodes, edges,
				refinedNode, showCounts);

		// fetch the current layouter
		var layouter = $wnd.GRAPH_HANDLER.getLayouter();

		// update each graph element's position, re-assigning a position
		layouter.updateLayout($wnd.GRAPH_HANDLER.getGraph(), newNodes);

		// fetch the renderer
		var renderer = $wnd.GRAPH_HANDLER.getRenderer();

		// re-draw the graph, animating transitions from old to new position
		renderer.draw();
    }-*/;

    /**
     * A JSNI method for updating the graph. This is supposed to be called upon
     * resizing the graph, as the graph is assumed not to have changed at all
     * when calling this method. Changes the size of the Raphael canvas and the
     * model div to the width and height of the parameters.
     * 
     * @param width
     *            The new width of the graph's canvas.
     * @param height
     *            The new height of the graph's canvas.
     */
    public static native void resizeGraph(int width, int height) /*-{
		// Determinize Math.random() calls for deterministic graph layout. Relies on seedrandom.js
		$wnd.Math.seedrandom($wnd.randSeed);

		// Get the current layout so it can be updated.
		var layouter = $wnd.GRAPH_HANDLER.getLayouter();

		// Update the layout for all nodes.
		layouter.updateLayout($wnd.GRAPH_HANDLER.getGraph(), $wnd.GRAPH_HANDLER
				.getCurrentNodes());

		// Grab a pointer to the current renderer.
		var rend = $wnd.GRAPH_HANDLER.getRenderer();

		// Change the appropriate height/width of the div.
		rend.width = width;
		rend.height = height;

		// Change the width/height of the Raphael canvas.
		rend.r.setSize(width, height);

		// Draw the new graph with all of the repositioned nodes.
		rend.draw();
    }-*/;

    // For all selected nodes in model, change their border to given color.
    public static native void updateNodesBorder(String color) /*-{
		$wnd.setShiftClickNodesState(color);
    }-*/;

    /**
     * Clears the state of the edges in the graph, but does not redraw the
     * graph. this has to be done after this method is called (and any
     * subsequent alterations to the graph that may have occurred thenceforth).
     * <p>
     * IMPORTANT NOTE: When changing the state of the edges in the Dracula Model
     * make sure to change the attributes using the "attr" command to change the
     * "connection.fg" field within each specific edge. This is because, when
     * changing the style attributes of the edge -- for example, edge.style.fill
     * = "#fff" -- when Dracula redraws the edge, more often than not, it
     * creates a new field (edge.connection.bg) to fill the color behind the
     * edge in question. This is important to note because all style changes
     * done outside of this method currently adhere to altering only the
     * edge.connection.fg field. So, if any changes are made to the edges where
     * the edge.connection.bg field is introduced, this WILL NOT clear those
     * changes from the edge's state, and may have to be appended to this code.
     * </p>
     */
    public static native void clearEdgeState() /*-{
		var g = $wnd.GRAPH_HANDLER.getGraph();

		var edges = g.edges;
		for (i = 0; i < edges.length; i++) {
			// Set the edge color back to black,
			// and set the width back to normal.
			$wnd.console.log(edges[i]);
			edges[i].connection.fg.attr({
				stroke : "#000",
				"stroke-width" : 1
			});
		}
    }-*/;

    /**
     * Highlights a path through the model based on array of edges passed.
     * Changes the edges' styles as to be reversible by the
     * {@code clearEdgeState} static method
     */
    public static native void highlightEdges(JavaScriptObject edges) /*-{
		var g = $wnd.GRAPH_HANDLER.getGraph();

		@synopticgwt.client.model.ModelGraphic::clearEdgeState()();
		var modelEdges = g.edges;
		for ( var i = 0; i < modelEdges.length; i++) {
			for ( var j = 0; j < edges.length; j += 4) {
				// If this edges matches one of the ones that needs to be highlighted,
				// then replace it with the new edge.
				if (modelEdges[i].source.id == edges[j]
						&& modelEdges[i].target.id == edges[j + 1]) {
					// Highlight the edge with the
					// highlighting color and set the stroke-width to
					// the selection stroke-width
					modelEdges[i].connection.fg.attr({
						stroke : $wnd.HIGHLIGHT_COLOR,
						"stroke-width" : $wnd.SELECT_STROKE_WIDTH
					});
					break;
				}
			}
		}
    }-*/;

    // </JSNI methods>
    // //////////////////////////////////////////////////////////////////////////
}

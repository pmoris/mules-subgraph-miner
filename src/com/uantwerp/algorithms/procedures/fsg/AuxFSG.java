package com.uantwerp.algorithms.procedures.fsg;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import com.uantwerp.algorithms.MiningState;
import com.uantwerp.algorithms.common.DFScode;
import com.uantwerp.algorithms.common.DFSedge;
import com.uantwerp.algorithms.common.Edge;
import com.uantwerp.algorithms.common.GraphParameters;
import com.uantwerp.algorithms.common.History;
import com.uantwerp.algorithms.common.PairStrValues;
import com.uantwerp.algorithms.procedures.gspan.AuxiliaryFunctions;
import com.uantwerp.algorithms.procedures.gspan.EdgeFunctions;
import com.uantwerp.algorithms.utilities.AlgorithmUtility;
import com.uantwerp.algorithms.utilities.HashFuctions;
import com.uantwerp.algorithms.utilities.PrintUtility;

public abstract class AuxFSG {
	
	public static void generateTwoFirstEdges(){		
		String canCode = "";
		HashMap<String, List<HashMap<Integer, String>>> newRoot = new HashMap<>();
		for (String strCode: FSG.root.keySet()){
			DFScode<DFSedge> dfsCode = FSG.canCodeDFSCode.get(strCode);
			List<HashMap<Integer, String>> projected = FSG.root.get(strCode);
			HashMap<String, List<HashMap<Integer, String>>> forwardRoot = new HashMap<>();
			for (HashMap<Integer, String> p : projected) {
				History history = new History(dfsCode, p);	
				/**************************** Self Edge *************************/
				if (GraphParameters.undirected == 0){
					Edge selfEdge = new Edge();
					selfEdge = EdgeFunctions.getSelfEdge(GraphParameters.graph, history.edges.get(0), history, dfsCode.get(0).isOrdered());
					if (selfEdge != null) {
						for (String label: GraphParameters.graph.vertex.get(selfEdge.to)){
//							DFScode<DFSedge> code = new DFScode<>();
//							code.addAll(dfsCode);
							DFScode<DFSedge> code = dfsCode.deepClone();
							canCode = summaryFunctionsTwoEdges(code, dfsCode.get(0).isOrdered() ?
										new DFSedge(dfsCode.get(0).getTargetId(), label, dfsCode.get(0).getTargetId(), label):
											new DFSedge(dfsCode.get(0).getSourceId(), label, dfsCode.get(0).getSourceId(), label), p.get(1));
							if (canCode != null){
								forwardRoot = AuxiliaryFunctions.addRootString(forwardRoot, canCode, p);
							}
						}
					}
				}
				if (GraphParameters.maxsize > 2){
					/********************** Pure forward **********************/
					List<Edge> edges = EdgeFunctions.getForwardPureEdge(GraphParameters.graph, history.edges.get(0), history,dfsCode.get(0).isOrdered());
					for (Edge e : edges) {						
						for (String label: GraphParameters.graph.vertex.get(e.to)){
							DFScode<DFSedge> code = dfsCode.deepClone();
							canCode = summaryFunctionsTwoEdges(code, dfsCode.get(0).isOrdered()?
									new DFSedge(dfsCode.get(0).getTargetId(), dfsCode.get(0).getTargetLabel(), 3, label):
										new DFSedge(dfsCode.get(0).getSourceId(), dfsCode.get(0).getSourceLabel(), 3, label), p.get(1));
							if (canCode != null){
								FSG.linearGraphs.add(canCode);
								forwardRoot = AuxiliaryFunctions.addRootString(forwardRoot, canCode, p);
							}
						}
					}
					/********************** Pure forward incoming**********************/
					if (GraphParameters.undirected == 0){
						List<Edge> edgesIncoming = EdgeFunctions.getForwardPureIncomingEdge(GraphParameters.graph, history.edges.get(0), history,dfsCode.get(0).isOrdered());
						for (Edge e : edgesIncoming) {													
							for (String label: GraphParameters.graph.vertex.get(e.from)){
								DFScode<DFSedge> code = dfsCode.deepClone();
								canCode = summaryFunctionsTwoEdges(code, dfsCode.get(0).isOrdered() ?
									new DFSedge(3, label, dfsCode.get(0).getTargetId(), dfsCode.get(0).getTargetLabel(), false):
										new DFSedge(3, label, dfsCode.get(0).getSourceId(), dfsCode.get(0).getSourceLabel(), false), p.get(1));
								if (canCode != null){
									FSG.linearGraphs.add(canCode);
									forwardRoot = AuxiliaryFunctions.addRootString(forwardRoot, canCode, p);
								}
							}							
						}						
					}
					/**********************rmpath forward **********************/
					List<Edge> edgesF = EdgeFunctions.getForwardRmpathEdge(GraphParameters.graph, history.edges.get(0), history,dfsCode.get(0).isOrdered());
					for (Edge e : edgesF) {
						for (String label: GraphParameters.graph.vertex.get(e.to)){
							DFScode<DFSedge> code = dfsCode.deepClone();
							canCode = summaryFunctionsTwoEdges(code, dfsCode.get(0).isOrdered() ?
									new DFSedge(dfsCode.get(0).getSourceId(), dfsCode.get(0).getSourceLabel(), 3, label):
										new DFSedge(dfsCode.get(0).getTargetId(), dfsCode.get(0).getTargetLabel(), 3, label), p.get(1));
							if (canCode != null)
								forwardRoot = AuxiliaryFunctions.addRootString(forwardRoot, canCode, p);
						}
					}	
					/**********************rmpath forward incoming**********************/
					if (GraphParameters.undirected == 0){
						List<Edge> edgesRMPIncoming = EdgeFunctions.getForwardRmpathIncomingEdge(GraphParameters.graph, history.edges.get(0), history,dfsCode.get(0).isOrdered());
						for (Edge e : edgesRMPIncoming) {							
							for (String label: GraphParameters.graph.vertex.get(e.from)){
								DFScode<DFSedge> code = dfsCode.deepClone();
								canCode = summaryFunctionsTwoEdges(code, dfsCode.get(0).isOrdered()?
										new DFSedge(3, label, dfsCode.get(0).getSourceId(), dfsCode.get(0).getSourceLabel(), false):
											new DFSedge(3, label, dfsCode.get(0).getTargetId(), dfsCode.get(0).getTargetLabel(), false), p.get(1));
								if (canCode != null)
									forwardRoot = AuxiliaryFunctions.addRootString(forwardRoot, canCode, p);
							}							
						}
					}
				}
			}
			Iterator<String> itCodes = forwardRoot.keySet().iterator();
			while (itCodes.hasNext()){
				String cannRep = itCodes.next();						
				calculateSupport(cannRep,itCodes,2);
			}
			newRoot.putAll(forwardRoot);
		}	
		FSG.root = new HashMap<>();
		FSG.canCodeDFSCode = new HashMap<>();
		FSG.canCodeDFSCode.putAll(FSG.canCodeDFSCodeSec);
		FSG.canCodeDFSCodeSec = new HashMap<>();
	}
	
	private static String summaryFunctionsTwoEdges(DFScode<DFSedge> code, DFSedge edge, String idNode){
		code.add(edge);
		if (code.getMinDfsCode().dfsCodeToString().equals(code.dfsCodeToString())){								
			String canCode = getCanCode(code);
			addCores(code,canCode);
			HashFuctions.updateHashHashSet(FSG.supportedNodes, canCode, idNode);
			return canCode;
		}
		return null;
	}
	
	private static void addCores(DFScode<DFSedge> code, String canRep){
		String codeStr = code.dfsCodeToString();
		if (!FSG.codeDFSRep.containsKey(codeStr))
			FSG.codeDFSRep.put(codeStr, code);
		for (DFSedge edge: code){
			DFScode<DFSedge> coreCode = code.deepClone();
			coreCode.removeEdge(edge);
			if (coreCode.get(0).getSourceId() == 1 || coreCode.get(0).getTargetId() == 1 ){
				String codeCodeStr = coreCode.getMinDfsCode().dfsCodeToString();
				if (FSG.codeCores.containsKey(canRep)){
					if (!FSG.codeCores.get(canRep).contains(codeCodeStr))
						FSG.codeCores.get(canRep).add(codeCodeStr);
				} else{
					HashSet<String> values  = new HashSet<>();
					values.add(codeCodeStr);
					FSG.codeCores.put(canRep, values);
				}	
			}
		}
	}
	
	/**
	 * Looks for the canonical code, if this is not yet created generates the matrix and use vertex invariants
	 */
	private static String getCanCode(DFScode<DFSedge> code){
		String canCode = "";
		String codeSrt = code.dfsCodeToString();
		if (FSG.codeCanonical.containsKey(codeSrt)){
			canCode = FSG.codeCanonical.get(codeSrt);			
		}else{
			VertexInvariant matrixRepresentation = new VertexInvariant(code.dfsCodeToGraph());
			canCode = matrixRepresentation.getRepresentation();
			FSG.canCodeDFSCodeSec.put(canCode, code);
			FSG.codeCanonical.put(code.dfsCodeToString(), canCode);
		}
		return canCode;
	}	
	
	/**
	 * Generates one edge Motifs and its history is saved in the root with the respective dfsCode
	 */
	public static void oneEdgeMotifs(){
		String canCode = "";
		for (String node: GraphParameters.graph.bgnodes){
			for (String neighbord: GraphParameters.graph.getAllEdges(node, GraphParameters.graph)){
				//for a directed graph is finding only the outgoing edges, have to add condition for adding the incoming ones				
				HashMap<Integer, String> match = new HashMap<>();
				match.put(1, node);
				match.put(2, neighbord);
				if (GraphParameters.singleLabel==1){					
					DFScode<DFSedge> dfscode = new DFScode<>();
					dfscode.add(new DFSedge(1, GraphParameters.graph.vertexOneLabel.get(node), 2, GraphParameters.graph.vertexOneLabel.get(neighbord)));
					canCode = getCanCode(dfscode);
					FSG.canCodeDFSCode.put(canCode, dfscode);
					FSG.codeDFSRep.put(canCode, dfscode);
					HashFuctions.updateHashHashSet(FSG.supportedNodes, canCode, match.get(1));
					FSG.root = AuxiliaryFunctions.addRootString(FSG.root, canCode, match);
				}else{
					for (PairStrValues lbls: getMixedLabels(node,neighbord)){
						DFScode<DFSedge> dfscode = new DFScode<>();
						dfscode.add(new DFSedge(1, lbls.getFromlb(), 2, lbls.getTolb()));
						canCode = getCanCode(dfscode);
						FSG.canCodeDFSCode.put(canCode, dfscode);
						FSG.codeDFSRep.put(canCode, dfscode);
						HashFuctions.updateHashHashSet(FSG.supportedNodes, canCode, match.get(1));
						FSG.root = AuxiliaryFunctions.addRootString(FSG.root, canCode, match);
					}
				}
			}
			if (GraphParameters.undirected == 0){
				for(String neighbord: GraphParameters.graph.getIncomingEdges(node, GraphParameters.graph)){
					HashMap<Integer, String> match = new HashMap<>();
					match.put(1, node);
					match.put(2, neighbord);
					if (GraphParameters.singleLabel==1){					
						DFScode<DFSedge> dfscode = new DFScode<>();
						dfscode.add(new DFSedge(2, GraphParameters.graph.vertexOneLabel.get(neighbord),1, GraphParameters.graph.vertexOneLabel.get(node), false));
						canCode = getCanCode(dfscode);
						FSG.canCodeDFSCode.put(canCode, dfscode);
						FSG.codeDFSRep.put(canCode, dfscode);
						HashFuctions.updateHashHashSet(FSG.supportedNodes, canCode, match.get(1));
						FSG.root = AuxiliaryFunctions.addRootString(FSG.root, canCode, match);
					}else{
						for (PairStrValues lbls: getMixedLabels(node,neighbord)){
							DFScode<DFSedge> dfscode = new DFScode<>();
							dfscode.add(new DFSedge(2, lbls.getTolb(), 1, lbls.getFromlb(), false));
							canCode = getCanCode(dfscode);
							FSG.canCodeDFSCode.put(canCode, dfscode);
							FSG.codeDFSRep.put(canCode, dfscode);
							HashFuctions.updateHashHashSet(FSG.supportedNodes, canCode, match.get(1));
							FSG.root = AuxiliaryFunctions.addRootString(FSG.root, canCode, match);
						}
					}
				}
			}
		}
		Iterator<String> it2 = FSG.root.keySet().iterator();
		while (it2.hasNext()){
			String strCode = it2.next();
			calculateSupport(strCode, it2, 1);
		}
	}
	
	/**
	 * For a multiple label configuration, creates all the possible combination of labels between two nodes
	 */
	private static HashSet<PairStrValues> getMixedLabels(String node, String neighbord){
		HashSet<PairStrValues> mixedLbl = new HashSet<>();
		for(String lbl1 :GraphParameters.graph.vertex.get(node)){
			for(String lbl2 :GraphParameters.graph.vertex.get(neighbord)){
				mixedLbl.add(new PairStrValues(lbl1, lbl2));
			}
		}
		return mixedLbl;
	}
	
	public static int calculateGroupSupport(HashSet<String> support){
		HashSet<String> valueSet = new HashSet<>();
		valueSet.addAll(support);
		valueSet.retainAll(GraphParameters.graph.group);
		return valueSet.size();
	}
	
	/**
	 * Checks the group support of a given code, it calculates based on the projections of the code
	 */
	public static void calculateSupport(String canonicalCode, Iterator<String> it2, int k){
		HashSet<String> support = FSG.supportedNodes.get(canonicalCode);			
		int groupSupport = calculateGroupSupport(support);			
		MiningState.checkedMotifsGroupSupport.put(canonicalCode, groupSupport);
		if (groupSupport < GraphParameters.supportcutoff){
			if (it2 != null)
				it2.remove();
			FSG.supportedNodes.remove(canonicalCode);
		}else{
			HashFuctions.updateHashHashSet(FSG.frequentGraphs, k, canonicalCode);
			MiningState.supportedMotifsGraphSupport.put(canonicalCode, support.size());
			double prob = AlgorithmUtility.getProbability(0, GraphParameters.graph.group.size(), support.size(), groupSupport);
			if (GraphParameters.verbose == 1)
				PrintUtility.print2LogView(canonicalCode + "\t" + groupSupport + "\t" + support.size() + "\t" + prob);
			MiningState.supportedMotifsPValues.put(canonicalCode, prob);
		}
	}

}

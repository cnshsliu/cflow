package com.lkh.cflow;

import com.lkh.cflow.Node;
import com.lkh.cflow.ProcessT;
import com.lkh.cflow.Work;
import com.lkh.cflow.WorkflowDocument;

public class Context{
	private ProcessT process = null;
	private WorkflowDocument.Workflow workflow=null;;
	private Work work = null;
	private Node node = null;

	public void setProcess(ProcessT process){
		this.process = process;
	}
	public void setWorkflow(WorkflowDocument.Workflow workflow){
		this.workflow = workflow;
	}
	public void setWork(Work work){
		this.work = work;
	}
	public void setNode(Node node){
		this.node = node;
	}
	public ProcessT getProcess(){
		return process;
	}
	public WorkflowDocument.Workflow getWorkflow(){
		return workflow;
	}
	public Work getWork(){
		return work;
	}
	public Node getNode(){
		return node;
	}

}

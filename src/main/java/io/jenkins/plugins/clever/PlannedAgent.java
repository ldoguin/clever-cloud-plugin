package io.jenkins.plugins.clever;

import hudson.model.Label;
import hudson.model.Node;
import hudson.slaves.NodeProvisioner;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

/**
 * @author <a href="mailto:nicolas.deloof@gmail.com">Nicolas De Loof</a>
 */
public class PlannedAgent extends NodeProvisioner.PlannedNode implements Callable<Node> {


    public PlannedAgent(Label label, AgentTemplate template) {
        super(template.getDisplayName(), new CompletableFuture<>(), 1);
    }

    /* package */ CompletableFuture<Node> promise() {
        return (CompletableFuture<Node>) super.future;
    }


    @Override
    public Node call() throws Exception {
        return null;
    }

}

/*
 * The MIT License
 *
 * Copyright 2018 ndeloof.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package io.jenkins.plugins.clever;

import hudson.model.Computer;
import hudson.model.Label;
import hudson.slaves.AbstractCloudImpl;
import hudson.slaves.NodeProvisioner;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Crazy class name
 * @author <a href="mailto:nicolas.deloof@gmail.com">Nicolas De Loof</a>
 */
public class CleverCloudCloud extends AbstractCloudImpl {

    private final List<AgentTemplate> templates;

    @DataBoundConstructor
    public CleverCloudCloud(String name, String instanceCapStr, List<AgentTemplate> templates) {
        super(name, instanceCapStr);
        this.templates = templates;
    }

    @Override
    public Collection<NodeProvisioner.PlannedNode> provision(Label label, int excessWorkload) {

        if (Jenkins.getInstance().isQuietingDown()) {
            return Collections.emptyList();
        }

        final AgentTemplate template = getTemplate(label);
        if (template == null) {
            return Collections.emptyList();
        }

        final List<PlannedAgent> r = new ArrayList<>();

        for (int i = 0; i < excessWorkload; i++) {
            r.add(new PlannedAgent(label, template));
        }

        for (PlannedAgent plannedNode : r) {
            Computer.threadPoolForRemoting.submit(() -> {
                try {
                    CleverCloudAgent agent = _provision(label, template);
                    plannedNode.promise().complete(agent);
                } catch (Throwable t) {
                    plannedNode.promise().completeExceptionally(t);
                }
            });
        }

        return new ArrayList<NodeProvisioner.PlannedNode>(r);
    }

    private CleverCloudAgent _provision(Label label, AgentTemplate template) {
        return null;
    }

    public List<AgentTemplate> getTemplates() {
        return templates;
    }

    @Override
    public boolean canProvision(Label label) {
        AgentTemplate template = getTemplate(label);
        return (template != null);
    }

    private AgentTemplate getTemplate(Label label) {
        for (AgentTemplate template : templates) {
            if (template.matches(label)) return template;
        }
        return null;
    }
}

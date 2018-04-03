package io.jenkins.plugins.clever;

import hudson.model.Label;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * @author <a href="mailto:nicolas.deloof@gmail.com">Nicolas De Loof</a>
 */
public class AgentTemplate {

    private final String label;

    @DataBoundConstructor
    public AgentTemplate(String label) {
        this.label = label;
    }

    public final String getLabel() {
        return label;
    }

    public boolean matches(Label l) {
        return l.matches(Label.get(label).listAtoms());
    }

    public String getDisplayName() {
        return "Agent template";
    }
}

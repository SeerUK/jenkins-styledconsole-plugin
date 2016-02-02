/**
 * This file is part of the "collapsing-output" project.
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the LICENSE is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * <p>
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package co.elliotwright.jenkins.plugins.collapsingoutput;

import co.elliotwright.jenkins.plugins.collapsingoutput.SectionConsoleNote.Kind;
import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.BuildStepListener;
import hudson.tasks.BuildStep;

import java.io.IOException;

@Extension
public class SectionBuildStepListener extends BuildStepListener {

    @Override
    public void started(AbstractBuild build, BuildStep bs, BuildListener listener) {
        try {
            listener.annotate(new SectionConsoleNote(Kind.BUILD_SECTION_END, getCounter(build)));
        } catch (IOException e) {
            listener.getLogger().println("Unable to insert StepConsoleNote");
        }
    }

    @Override
    public void finished(AbstractBuild build, BuildStep bs, BuildListener listener, boolean canContinue) {
        try {
            listener.annotate(new SectionConsoleNote(Kind.BUILD_SECTION_START, incrementCounter(build)));
        } catch (IOException e) {
            listener.getLogger().println("Unable to insert SectionConsoleNote");
        }
    }

    private SectionBuildSectionAction getAction(AbstractBuild build) {
        SectionBuildSectionAction action = build.getAction(SectionBuildSectionAction.class);
        if (action == null) {
            action = new SectionBuildSectionAction();
            build.addAction(action);
        }
        return action;
    }

    private int getCounter(AbstractBuild build) {
        return getAction(build).getSectionNumber();
    }

    private int incrementCounter(AbstractBuild build) {
        return getAction(build).increment();
    }
}

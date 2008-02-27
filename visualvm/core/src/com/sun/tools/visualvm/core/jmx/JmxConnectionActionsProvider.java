/*
 * Copyright 2008 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */

package com.sun.tools.visualvm.core.jmx;

import com.sun.tools.visualvm.core.application.JmxApplication;
import com.sun.tools.visualvm.core.application.JmxApplicationProvider;
import com.sun.tools.visualvm.core.datasource.DataSourceRoot;
import com.sun.tools.visualvm.core.datasource.Host;
import com.sun.tools.visualvm.core.explorer.ExplorerActionDescriptor;
import com.sun.tools.visualvm.core.explorer.ExplorerActionsProvider;
import com.sun.tools.visualvm.core.explorer.ExplorerContextMenuFactory;
import com.sun.tools.visualvm.core.host.RemoteHostsContainer;
import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Set;
import javax.management.remote.JMXServiceURL;
import javax.swing.AbstractAction;
import org.openide.util.Exceptions;

/**
 *
 * @author Luis-Miguel Alventosa
 */
class JmxConnectionActionsProvider {

    private static final AddJmxConnectionAction addJmxConnectionAction =
            new AddJmxConnectionAction();
    private static final RemoveJmxConnectionAction removeJmxConnectionAction =
            new RemoveJmxConnectionAction();

    static void initialize() {
        ExplorerContextMenuFactory explorer = ExplorerContextMenuFactory.sharedInstance();
        explorer.addExplorerActionsProvider(new HostActionProvider(), Host.class);
        explorer.addExplorerActionsProvider(new RemoteHostsContainerActionProvider(), RemoteHostsContainer.class);
        explorer.addExplorerActionsProvider(new JmxApplicationActionProvider(), JmxApplication.class);
        explorer.addExplorerActionsProvider(new DataSourceRootActionProvider(), DataSourceRoot.class);
    }

    private static class AddJmxConnectionAction extends AbstractAction {

        public AddJmxConnectionAction() {
            super("Add JMX Connection...");
        }

        public void actionPerformed(ActionEvent e) {
            JmxConnectionConfigurator addJmxConnectionConfiguration =
                    JmxConnectionConfigurator.addJmxConnection();
            if (addJmxConnectionConfiguration != null) {
                try {
                    String urlStr = addJmxConnectionConfiguration.getConnection();
                    if (!urlStr.startsWith("service:jmx:")) {
                        urlStr = "service:jmx:rmi:///jndi/rmi://" + urlStr + "/jmxrmi";
                    }
                    JMXServiceURL url = new JMXServiceURL(urlStr);
                    // TODO: Compute Host and add new remote host node if necessary
                    new JmxApplicationProvider().processNewJmxApplication(Host.LOCALHOST, addJmxConnectionConfiguration.getDisplayName(), url);
                } catch (MalformedURLException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
    }

    private static class RemoveJmxConnectionAction extends AbstractAction {

        public RemoveJmxConnectionAction() {
            super("Remove");
        }

        public void actionPerformed(ActionEvent e) {
            JmxApplication app = (JmxApplication) e.getSource();
        // TODO: Not implemented yet
        }
    }

    private static class HostActionProvider implements ExplorerActionsProvider<Host> {

        public ExplorerActionDescriptor getDefaultAction(Host host) {
            return null;
        }

        public Set<ExplorerActionDescriptor> getActions(Host host) {
            Set<ExplorerActionDescriptor> actions =
                    new HashSet<ExplorerActionDescriptor>();
            actions.add(new ExplorerActionDescriptor(addJmxConnectionAction, 110));
            return actions;
        }
    }

    private static class RemoteHostsContainerActionProvider
            implements ExplorerActionsProvider<RemoteHostsContainer> {

        public ExplorerActionDescriptor getDefaultAction(RemoteHostsContainer container) {
            return null;
        }

        public Set<ExplorerActionDescriptor> getActions(RemoteHostsContainer container) {
            Set<ExplorerActionDescriptor> actions =
                    new HashSet<ExplorerActionDescriptor>();
            actions.add(new ExplorerActionDescriptor(addJmxConnectionAction, 30));
            return actions;
        }
    }

    private static class JmxApplicationActionProvider
            implements ExplorerActionsProvider<JmxApplication> {

        public ExplorerActionDescriptor getDefaultAction(JmxApplication app) {
            return null;
        }

        public Set<ExplorerActionDescriptor> getActions(JmxApplication app) {
            Set<ExplorerActionDescriptor> actions =
                    new HashSet<ExplorerActionDescriptor>();
            actions.add(new ExplorerActionDescriptor(removeJmxConnectionAction, 100));
            return actions;
        }
    }

    private static class DataSourceRootActionProvider
            implements ExplorerActionsProvider<DataSourceRoot> {

        public ExplorerActionDescriptor getDefaultAction(DataSourceRoot root) {
            return null;
        }

        public Set<ExplorerActionDescriptor> getActions(DataSourceRoot root) {
            Set<ExplorerActionDescriptor> actions =
                    new HashSet<ExplorerActionDescriptor>();
            actions.add(new ExplorerActionDescriptor(addJmxConnectionAction, 20));
            return actions;
        }
    }
}
